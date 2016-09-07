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

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class DefaultJawaPlatform implements JawaPlatform {
    private String jawaVersion

    public DefaultScalaPlatform(String version) {
        this.jawaVersion = version
    }

    @Override
    public String getJawaVersion() {
        return jawaVersion;
    }

    @Override
    public String getDisplayName() {
        return String.format("Jawa Platform (Jawa %s)", jawaVersion);
    }

    @Override
    public String getName() {
        return String.format("JawaPlatform%s", jawaVersion);
    }
}