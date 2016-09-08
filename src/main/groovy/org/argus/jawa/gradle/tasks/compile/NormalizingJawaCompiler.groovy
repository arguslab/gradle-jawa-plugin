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
public class NormalizingJawaCompiler implements Compiler<JawaJavaJointCompileSpec> {
    private static final Logger LOGGER = Logging.getLogger(NormalizingJawaCompiler.class)
    private final Compiler<JawaJavaJointCompileSpec> delegate

    public NormalizingJawaCompiler(Compiler<JawaJavaJointCompileSpec> delegate) {
        this.delegate = delegate
    }

    @Override
    public WorkResult execute(JawaJavaJointCompileSpec spec) {
        resolveAndFilterSourceFiles(spec)
        logSourceFiles(spec)
        logCompilerArguments(spec)
        return delegateAndHandleErrors(spec)
    }

    private static void resolveAndFilterSourceFiles(final JawaJavaJointCompileSpec spec) {
        final List<String> fileExtensions = CollectionUtils.collect(spec.getJawaCompileOptions().getFileExtensions(), new Transformer<String, String>() {
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

    private static void logSourceFiles(JawaJavaJointCompileSpec spec) {
        if (!spec.getJawaCompileOptions().isListFiles()) {
            return
        }

        StringBuilder builder = new StringBuilder()
        builder.append("Source files to be compiled:")
        for (File file : spec.getSource()) {
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
            if (spec.getCompileOptions().isFailOnError()) {
                throw e
            }
            LOGGER.debug("Ignoring compilation failure.")
            return new SimpleWorkResult(false)
        }
    }
}