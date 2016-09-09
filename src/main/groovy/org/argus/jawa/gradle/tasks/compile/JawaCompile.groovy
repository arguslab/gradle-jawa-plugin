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

import org.argus.jawa.gradle.tasks.compile.spec.DefaultJawaJavaJointCompileSpec
import org.argus.jawa.gradle.tasks.compile.spec.DefaultJawaJavaJointCompileSpecFactory
import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.compile.JavaCompilerFactory
import org.gradle.api.internal.tasks.compile.daemon.CompilerDaemonManager
import org.gradle.api.internal.tasks.compile.daemon.InProcessCompilerDaemonFactory
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.WorkResult
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.language.base.internal.compile.Compiler

/**
 * Compiles Jawa source files.
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaCompile extends AbstractCompile {

    private Compiler<JawaJavaJointCompileSpec> compiler
    private final CompileOptions compileOptions = new CompileOptions()
    private final JawaCompileOptions jawaCompileOptions = new JawaCompileOptions()

    @Override
    @TaskAction
    protected void compile() {
        DefaultJawaJavaJointCompileSpec spec = createSpec()
        WorkResult result = getCompiler(spec).execute(spec)
        setDidWork(result.getDidWork())
    }

    protected Compiler<JawaJavaJointCompileSpec> getCompiler(JawaJavaJointCompileSpec spec) {
        if (compiler == null) {
            ProjectInternal projectInternal = (ProjectInternal) getProject()
            CompilerDaemonManager compilerDaemonManager = getServices().get(CompilerDaemonManager.class)
            InProcessCompilerDaemonFactory inProcessCompilerDaemonFactory = getServices().get(InProcessCompilerDaemonFactory.class)
            JavaCompilerFactory javaCompilerFactory = getServices().get(JavaCompilerFactory.class)
            JawaCompilerFactory groovyCompilerFactory = new JawaCompilerFactory(projectInternal, javaCompilerFactory, compilerDaemonManager, inProcessCompilerDaemonFactory)
            Compiler<JawaJavaJointCompileSpec> delegatingCompiler = groovyCompilerFactory.newCompiler(spec)
            compiler = new CleaningJawaCompiler(delegatingCompiler, getOutputs())
        }
        return compiler
    }

    private DefaultJawaJavaJointCompileSpec createSpec() {
        DefaultJawaJavaJointCompileSpec spec = new DefaultJawaJavaJointCompileSpecFactory(compileOptions).create()
        spec.setSource(getSource())
        spec.setDestinationDir(getDestinationDir())
        spec.setWorkingDir(getProject().getProjectDir())
        spec.setTempDir(getTemporaryDir())
        spec.setClasspath(getClasspath())
        spec.setSourceCompatibility(getSourceCompatibility())
        spec.setTargetCompatibility(getTargetCompatibility())
        spec.setCompileOptions(compileOptions)
        spec.setJawaCompileOptions(jawaCompileOptions)
        return spec
    }

    /**
     * Gets the options for the Groovy compilation.
     *
     * @return The Groovy compile options. Never returns null.
     */
    @Nested
    public JawaCompileOptions getJawaOptions() {
        return jawaCompileOptions
    }

    /**
     * Returns the options for Java compilation.
     *
     * @return The Java compile options. Never returns null.
     */
    @Nested
    public CompileOptions getOptions() {
        return compileOptions
    }

    public Compiler<JawaJavaJointCompileSpec> getCompiler() {
        return getCompiler(createSpec())
    }

    public void setCompiler(Compiler<JawaJavaJointCompileSpec> compiler) {
        this.compiler = compiler
    }
}