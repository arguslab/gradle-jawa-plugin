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

import org.argus.jawa.gradle.tasks.JawaCompileOptions
import org.gradle.api.internal.tasks.compile.DefaultJavaCompileSpec

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class DefaultJawaJavaJointCompileSpec extends DefaultJavaCompileSpec implements JawaJavaJointCompileSpec {
    private JawaCompileOptions compileOptions

    @Override
    public JawaCompileOptions getJawaCompileOptions() {
        return compileOptions
    }

    public void setJawaCompileOptions(JawaCompileOptions compileOptions) {
        this.compileOptions = compileOptions
    }
}
