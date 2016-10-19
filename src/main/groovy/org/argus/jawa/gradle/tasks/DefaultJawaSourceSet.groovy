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

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.util.ConfigureUtil

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class DefaultJawaSourceSet implements JawaSourceSet {
    private final SourceDirectorySet jawa
    private final SourceDirectorySet allJawa

    public DefaultJawaSourceSet(String displayName, SourceDirectorySetFactory sourceDirectorySetFactory) {
        jawa = sourceDirectorySetFactory.create(String.format("%s Jawa source", displayName))
        jawa.getFilter().include("**/*.java", "**/*.jawa")
        allJawa = sourceDirectorySetFactory.create(String.format("%s Jawa source", displayName))
        allJawa.source(jawa)
        allJawa.getFilter().include("**/*.jawa")
    }

    public SourceDirectorySet getJawa() {
        return jawa
    }

    public JawaSourceSet jawa(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getJawa())
        return this
    }

    public SourceDirectorySet getAllJawa() {
        return allJawa
    }
}
