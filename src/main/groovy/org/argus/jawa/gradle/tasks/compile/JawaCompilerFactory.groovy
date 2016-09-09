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

import org.argus.jawa.gradle.tasks.compile.daemon.DaemonJawaCompiler
import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.compile.JavaCompilerFactory
import org.gradle.api.internal.tasks.compile.daemon.CompilerDaemonFactory
import org.gradle.api.internal.tasks.compile.daemon.CompilerDaemonManager
import org.gradle.api.internal.tasks.compile.daemon.InProcessCompilerDaemonFactory
import org.gradle.language.base.internal.compile.Compiler
import org.gradle.language.base.internal.compile.CompilerFactory

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaCompilerFactory implements CompilerFactory<JawaJavaJointCompileSpec> {
    private ProjectInternal project
    private JavaCompilerFactory javaCompilerFactory
    private CompilerDaemonManager compilerDaemonFactory
    private InProcessCompilerDaemonFactory inProcessCompilerDaemonFactory

    public JawaCompilerFactory(ProjectInternal project, JavaCompilerFactory javaCompilerFactory, CompilerDaemonManager compilerDaemonManager,
                                 InProcessCompilerDaemonFactory inProcessCompilerDaemonFactory) {
        this.project = project
        this.javaCompilerFactory = javaCompilerFactory
        this.compilerDaemonFactory = compilerDaemonManager
        this.inProcessCompilerDaemonFactory = inProcessCompilerDaemonFactory
    }

    @Override
    public Compiler<JawaJavaJointCompileSpec> newCompiler(JawaJavaJointCompileSpec spec) {
        JawaCompileOptions jawaOptions = spec.getJawaCompileOptions()
        Compiler<JawaJavaJointCompileSpec> jawaCompiler = new JawaCompiler(project.getGradle().getGradleUserHomeDir())
        CompilerDaemonFactory daemonFactory
        if (jawaOptions.isFork()) {
            daemonFactory = compilerDaemonFactory
        } else {
            daemonFactory = inProcessCompilerDaemonFactory
        }
        jawaCompiler = new DaemonJawaCompiler(project.getRootProject().getProjectDir(), jawaCompiler, daemonFactory)
        return new NormalizingJawaCompiler(jawaCompiler)
    }
}