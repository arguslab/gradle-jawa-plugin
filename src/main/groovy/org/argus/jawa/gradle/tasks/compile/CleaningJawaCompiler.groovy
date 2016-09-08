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

import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec
import org.gradle.api.internal.TaskOutputsInternal
import org.gradle.api.internal.tasks.compile.CleaningJavaCompilerSupport
import org.gradle.language.base.internal.compile.Compiler
import org.gradle.language.base.internal.tasks.SimpleStaleClassCleaner
import org.gradle.language.base.internal.tasks.StaleClassCleaner

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class CleaningJawaCompiler extends CleaningJavaCompilerSupport<JawaJavaJointCompileSpec> {
    private final Compiler<JawaJavaJointCompileSpec> compiler
    private final TaskOutputsInternal taskOutputs

    public CleaningJawaCompiler(Compiler<JawaJavaJointCompileSpec> compiler, TaskOutputsInternal taskOutputs) {
        this.compiler = compiler
        this.taskOutputs = taskOutputs
    }

    @Override
    protected Compiler<JawaJavaJointCompileSpec> getCompiler() {
        return compiler
    }

    @Override
    protected StaleClassCleaner createCleaner(JawaJavaJointCompileSpec spec) {
        return new SimpleStaleClassCleaner(taskOutputs)
    }
}