/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.tasks

import org.argus.jawa.gradle.platform.JawaPlatform
import org.gradle.api.Incubating
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.language.base.internal.compile.CompilerUtil
import org.gradle.language.scala.internal.toolchain.ScalaToolChainInternal

import javax.inject.Inject

/**
 * A platform-aware Jawa compile task.
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
@Incubating
@ParallelizableTask
public class PlatformJawaCompile extends AbstractJawaCompile {

    private JawaPlatform platform

    @Nested
    public JawaPlatform getPlatform() {
        return platform
    }

    public void setPlatform(JawaPlatform platform) {
        this.platform = platform;
    }

    @Inject
    protected ScalaToolChainInternal getToolChain() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Compiler<ScalaJavaJointCompileSpec> getCompiler(ScalaJavaJointCompileSpec spec) {
        return CompilerUtil.castCompiler(getToolChain().select(getPlatform()).newCompiler(spec.getClass()));
    }
}