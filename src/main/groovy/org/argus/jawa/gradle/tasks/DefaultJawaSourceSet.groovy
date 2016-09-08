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
    private final SourceDirectorySet groovy
    private final SourceDirectorySet allGroovy

    public DefaultJawaSourceSet(String displayName, SourceDirectorySetFactory sourceDirectorySetFactory) {
        groovy = sourceDirectorySetFactory.create(String.format("%s Jawa source", displayName))
        groovy.getFilter().include("**/*.java", "**/*.pilar")
        allGroovy = sourceDirectorySetFactory.create(String.format("%s Jawa source", displayName))
        allGroovy.source(groovy)
        allGroovy.getFilter().include("**/*.pilar")
    }

    public SourceDirectorySet getJawa() {
        return groovy
    }

    public JawaSourceSet jawa(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getJawa())
        return this
    }

    public SourceDirectorySet getAllJawa() {
        return allGroovy
    }
}
