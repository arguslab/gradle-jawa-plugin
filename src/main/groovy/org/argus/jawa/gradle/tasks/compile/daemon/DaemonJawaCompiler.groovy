/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.tasks.compile.daemon

import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec
import org.gradle.api.internal.tasks.compile.daemon.AbstractDaemonCompiler
import org.gradle.api.internal.tasks.compile.daemon.CompilerDaemonFactory
import org.gradle.api.internal.tasks.compile.daemon.DaemonForkOptions
import org.gradle.language.base.internal.compile.Compiler

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class DaemonJawaCompiler extends AbstractDaemonCompiler<JawaJavaJointCompileSpec> {

    public DaemonJawaCompiler(File daemonWorkingDir, Compiler<JawaJavaJointCompileSpec> delegate, CompilerDaemonFactory daemonFactory) {
        super(daemonWorkingDir, delegate, daemonFactory)
    }

    @Override
    protected DaemonForkOptions toDaemonOptions(JawaJavaJointCompileSpec spec) {
        return createJavaForkOptions(spec).mergeWith(createJawaForkOptions(spec))
    }

    private static DaemonForkOptions createJavaForkOptions(JawaJavaJointCompileSpec spec) {
        def options = spec.getCompileOptions().getForkOptions()
        return new DaemonForkOptions(options.getMemoryInitialSize(), options.getMemoryMaximumSize(), options.getJvmArgs())
    }

    private static DaemonForkOptions createJawaForkOptions(JawaJavaJointCompileSpec spec) {
        def options = spec.getJawaCompileOptions().getForkOptions()
        return new DaemonForkOptions(options.getMemoryInitialSize(), options.getMemoryMaximumSize(), options.getJvmArgs())
    }
}