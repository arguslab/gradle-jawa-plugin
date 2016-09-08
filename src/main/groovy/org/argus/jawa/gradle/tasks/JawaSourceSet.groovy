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

/**
 * A {@code JawaSourceSetConvention} defines the properties and methods added to a {@link org.gradle.api.tasks.SourceSet} by the {@link org.argus.jawa.gradle.plugins.JawaPlugin}.
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public interface JawaSourceSet {
    /**
     * Returns the source to be compiled by the Groovy compiler for this source set. Any Java source present in this set
     * will be passed to the Groovy compiler for joint compilation.
     *
     * @return The Groovy/Java source. Never returns null.
     */
    SourceDirectorySet getJawa()

    /**
     * Configures the Groovy source for this set.
     *
     * <p>The given closure is used to configure the {@link SourceDirectorySet} which contains the Groovy source.
     *
     * @param configureClosure The closure to use to configure the Groovy source.
     * @return this
     */
    JawaSourceSet jawa(Closure configureClosure)

    /**
     * All Groovy source for this source set.
     *
     * @return the Groovy source. Never returns null.
     */
    SourceDirectorySet getAllJawa()
}