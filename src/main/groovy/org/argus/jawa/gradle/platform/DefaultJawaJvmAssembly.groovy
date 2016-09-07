/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.platform

import org.gradle.jvm.internal.DefaultJvmAssembly
import org.gradle.platform.base.internal.ComponentSpecIdentifier

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class DefaultJawaJvmAssembly extends DefaultJvmAssembly implements JawaJvmAssembly {
    private JawaPlatform jawaPlatform

    public DefaultJawaJvmAssembly(ComponentSpecIdentifier identifier) {
        super(identifier, JawaJvmAssembly.class)
    }

    @Override
    public JawaPlatform getJawaPlatform() {
        return jawaPlatform
    }

    public void setJawaPlatform(JawaPlatform jawaPlatform) {
        this.jawaPlatform = jawaPlatform
    }
}