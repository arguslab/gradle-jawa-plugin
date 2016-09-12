/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.tasks.compile

import com.google.common.base.Joiner
import com.google.common.collect.Lists
import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec
import org.gradle.api.Transformer
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.internal.tasks.SimpleWorkResult
import org.gradle.api.internal.tasks.compile.CompilationFailedException
import org.gradle.api.internal.tasks.compile.JavaCompilerArgumentsBuilder
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.WorkResult
import org.gradle.language.base.internal.compile.Compiler
import org.gradle.util.CollectionUtils

import static org.gradle.internal.FileUtils.hasExtension

/**
 * A Jawa {@link Compiler} which does some normalization of the compile configuration and behaviour before delegating to some other compiler.
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class NormalizingJawaGradleCompiler implements Compiler<JawaJavaJointCompileSpec> {
    private static final Logger LOGGER = Logging.getLogger(NormalizingJawaGradleCompiler)
    private final Compiler<JawaJavaJointCompileSpec> delegate

    public NormalizingJawaGradleCompiler(Compiler<JawaJavaJointCompileSpec> delegate) {
        this.delegate = delegate
    }

    @Override
    public WorkResult execute(JawaJavaJointCompileSpec spec) {
        resolveAndFilterSourceFiles(spec)
        resolveClasspath(spec)
        resolveNonStringsInCompilerArgs(spec)
        logSourceFiles(spec)
        logCompilerArguments(spec)
        return delegateAndHandleErrors(spec)
    }

    private static void resolveAndFilterSourceFiles(final JawaJavaJointCompileSpec spec) {
        final List<String> fileExtensions = CollectionUtils.collect(spec.jawaCompileOptions.fileExtensions, new Transformer<String, String>() {
            @Override
            public String transform(String extension) {
                return '.' + extension
            }
        })
        FileCollection filtered = spec.getSource().filter(new Spec<File>() {
            public boolean isSatisfiedBy(File element) {
                for (String fileExtension : fileExtensions) {
                    if (hasExtension(element, fileExtension)) {
                        return true
                    }
                }
                return false
            }
        })
        spec.setSource(new SimpleFileCollection(filtered.getFiles()))
    }

    private static void resolveClasspath(JawaJavaJointCompileSpec spec) {
        // Necessary for Jawa compilation to pick up output of regular and joint Java compilation,
        // and for joint Java compilation to pick up the output of regular Java compilation.
        // Assumes that output of regular Java compilation (which is not under this task's control) also goes
        // into spec.getDestinationDir(). We could configure this on source set level, but then spec.getDestinationDir()
        // would end up on the compile class path of every compile task for that source set, which may not be desirable.
        def classPath = Lists.newArrayList(spec.classpath)
        classPath.add(spec.getDestinationDir())
        spec.setClasspath(classPath)

        spec.setJawaClasspath(Lists.newArrayList(spec.jawaClasspath))
    }

    private static void resolveNonStringsInCompilerArgs(JawaJavaJointCompileSpec spec) {
        // in particular, this is about GStrings
        spec.compileOptions.setCompilerArgs(CollectionUtils.toStringList(spec.compileOptions.compilerArgs))
    }

    private static void logSourceFiles(JawaJavaJointCompileSpec spec) {
        if (!spec.jawaCompileOptions.listFiles) {
            return
        }

        StringBuilder builder = new StringBuilder()
        builder.append("Source files to be compiled:")
        for (File file : spec.source) {
            builder.append('\n')
            builder.append(file)
        }

        LOGGER.quiet(builder.toString())
    }

    private static void logCompilerArguments(JawaJavaJointCompileSpec spec) {
        if (!LOGGER.isDebugEnabled()) {
            return
        }

        List<String> compilerArgs = new JavaCompilerArgumentsBuilder(spec).includeLauncherOptions(true).includeSourceFiles(true).build()
        String joinedArgs = Joiner.on(' ').join(compilerArgs)
        LOGGER.debug("Java compiler arguments: {}", joinedArgs)
    }

    private WorkResult delegateAndHandleErrors(JawaJavaJointCompileSpec spec) {
        try {
            return delegate.execute(spec)
        } catch (CompilationFailedException e) {
            if (spec.compileOptions.failOnError) {
                throw e
            }
            LOGGER.debug("Ignoring compilation failure.")
            return new SimpleWorkResult(false)
        }
    }
}