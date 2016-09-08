/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * <p>A {@link org.gradle.api.Plugin} which extends the {@link org.gradle.api.plugins.JavaPlugin} to provide support for compiling Jawa
 * source files.</p>
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getPluginManager().apply(JawaBasePlugin.class);
        project.getPluginManager().apply(JavaPlugin.class);
    }
}
