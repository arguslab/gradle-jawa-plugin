/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.tasks.compile.spec

import org.gradle.api.internal.tasks.compile.AbstractJavaCompileSpecFactory
import org.gradle.api.internal.tasks.compile.CommandLineJavaCompileSpec
import org.gradle.api.internal.tasks.compile.ForkingJavaCompileSpec
import org.gradle.api.tasks.compile.CompileOptions

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
class DefaultJawaJavaJointCompileSpecFactory extends AbstractJavaCompileSpecFactory<DefaultJawaJavaJointCompileSpec> {
    public DefaultJawaJavaJointCompileSpecFactory(CompileOptions compileOptions) {
        super(compileOptions);
    }

    @Override
    protected DefaultJawaJavaJointCompileSpec getCommandLineSpec() {
        return new DefaultCommandLineJawaJavaJointCompileSpec();
    }

    @Override
    protected DefaultJawaJavaJointCompileSpec getForkingSpec() {
        return new DefaultForkingJawaJavaJointCompileSpec();
    }

    @Override
    protected DefaultJawaJavaJointCompileSpec getDefaultSpec() {
        return new DefaultJawaJavaJointCompileSpec();
    }

    private static class DefaultCommandLineJawaJavaJointCompileSpec extends DefaultJawaJavaJointCompileSpec implements CommandLineJavaCompileSpec {
    }

    private static class DefaultForkingJawaJavaJointCompileSpec extends DefaultJawaJavaJointCompileSpec implements ForkingJavaCompileSpec {
    }
}
