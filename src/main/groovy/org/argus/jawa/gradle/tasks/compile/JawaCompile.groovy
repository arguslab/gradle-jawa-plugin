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
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.compile.JavaCompilerFactory
import org.gradle.api.internal.tasks.compile.daemon.CompilerDaemonManager
import org.gradle.api.internal.tasks.compile.daemon.InProcessCompilerDaemonFactory
import org.gradle.api.tasks.InputFiles
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
    private FileCollection jawaClasspath
    private final CompileOptions compileOptions = new CompileOptions()
    private final JawaCompileOptions jawaCompileOptions = new JawaCompileOptions()

    @Override
    @TaskAction
    protected void compile() {
        checkJawaClasspathIsNonEmpty()
        def spec = createSpec()
        def result = getCompiler(spec).execute(spec)
        setDidWork(result.didWork)
    }

    private void checkJawaClasspathIsNonEmpty() {
        if (getJawaClasspath().isEmpty()) {
            throw new InvalidUserDataException("'" + getName() + ".jawaClasspath' must not be empty. If a Jawa compile dependency is provided, "
                    + "the 'jawa-base' plugin will attempt to configure 'jawaClasspath' automatically. Alternatively, you may configure 'jawaClasspath' explicitly.")
        }
    }

    protected Compiler<JawaJavaJointCompileSpec> getCompiler(JawaJavaJointCompileSpec spec) {
        if (compiler == null) {
            def projectInternal = (ProjectInternal) project
            def compilerDaemonManager = services.get(CompilerDaemonManager)
            def inProcessCompilerDaemonFactory = services.get(InProcessCompilerDaemonFactory)
            def javaCompilerFactory = services.get(JavaCompilerFactory)
            def jawaCompilerFactory = new JawaCompilerFactory(projectInternal, javaCompilerFactory, compilerDaemonManager, inProcessCompilerDaemonFactory)
            def delegatingCompiler = jawaCompilerFactory.newCompiler(spec)
            compiler = new CleaningJawaCompiler(delegatingCompiler, outputs)
        }
        return compiler
    }

    private DefaultJawaJavaJointCompileSpec createSpec() {
        def spec = new DefaultJawaJavaJointCompileSpecFactory(compileOptions).create()
        spec.setSource(source)
        spec.setDestinationDir(destinationDir)
        spec.setWorkingDir(project.projectDir)
        spec.setTempDir(temporaryDir)
        spec.setClasspath(classpath)
        spec.setSourceCompatibility(sourceCompatibility)
        spec.setTargetCompatibility(targetCompatibility)
        spec.setJawaClasspath(getJawaClasspath())
        spec.setCompileOptions(compileOptions)
        spec.setJawaCompileOptions(jawaCompileOptions)
        return spec
    }

    /**
     * Gets the options for the Jawa compilation.
     *
     * @return The Jawa compile options. Never returns null.
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

    /**
     * Returns the classpath containing the version of Jawa to use for compilation.
     *
     * @return The classpath.
     */
    @InputFiles
    public FileCollection getJawaClasspath() {
        return jawaClasspath
    }

    /**
     * Sets the classpath containing the version of Jawa to use for compilation.
     *
     * @param jawaClasspath The classpath. Must not be null.
     */
    public void setJawaClasspath(FileCollection jawaClasspath) {
        this.jawaClasspath = jawaClasspath
    }

    public Compiler<JawaJavaJointCompileSpec> getCompiler() {
        return getCompiler(createSpec())
    }

    public void setCompiler(Compiler<JawaJavaJointCompileSpec> compiler) {
        this.compiler = compiler
    }
}