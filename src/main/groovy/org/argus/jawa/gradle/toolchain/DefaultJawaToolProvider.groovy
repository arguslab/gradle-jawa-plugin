/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.toolchain

import org.argus.jawa.gradle.tasks.compile.JawaCompiler
import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec
import org.gradle.api.internal.tasks.compile.daemon.CompilerDaemonManager
import org.gradle.language.base.internal.compile.CompileSpec
import org.gradle.language.base.internal.compile.Compiler
import org.gradle.platform.base.internal.toolchain.ToolProvider
import org.gradle.util.TreeVisitor

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class DefaultJawaToolProvider implements ToolProvider {

    private File gradleUserHomeDir
    private File rootProjectDir
    private CompilerDaemonManager compilerDaemonManager

    public DefaultJawaToolProvider(File gradleUserHomeDir, File rootProjectDir, CompilerDaemonManager compilerDaemonManager) {
        this.gradleUserHomeDir = gradleUserHomeDir
        this.rootProjectDir = rootProjectDir
        this.compilerDaemonManager = compilerDaemonManager
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CompileSpec> Compiler<T> newCompiler(Class<T> spec) {
        if (JawaJavaJointCompileSpec.class.isAssignableFrom(spec)) {
            return new JawaCompiler(gradleUserHomeDir) as Compiler<T>
        }
        throw new IllegalArgumentException(String.format("Cannot create Compiler for unsupported CompileSpec type '%s'", spec.getSimpleName()))
    }

    @Override
    public <T> T get(Class<T> toolType) {
        throw new IllegalArgumentException(String.format("Don't know how to provide tool of type %s.", toolType.getSimpleName()))
    }

    @Override
    public boolean isAvailable() {
        return true
    }

    @Override
    public void explain(TreeVisitor<? super String> visitor) {

    }
}