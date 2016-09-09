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

import org.argus.jawa.gradle.tasks.DefaultJawaSourceSet
import org.argus.jawa.gradle.tasks.compile.JawaCompile
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTreeElement
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.SourceSet

import javax.inject.Inject

/**
 * Extends {@link org.gradle.api.plugins.JavaBasePlugin} to provide support for compiling Jawa
 * source files.
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
class JawaBasePlugin implements Plugin<Project> {
    private SourceDirectorySetFactory sourceDirectorySetFactory

    private Project project

    @Inject
    public JawaBasePlugin(SourceDirectorySetFactory sourceDirectorySetFactory) {
        this.sourceDirectorySetFactory = sourceDirectorySetFactory
    }

    public void apply(Project project) {
        this.project = project
        project.getPluginManager().apply(JavaBasePlugin.class)
        def javaBasePlugin = project.getPlugins().getPlugin(JavaBasePlugin.class)
        configureSourceSetDefaults(javaBasePlugin)
    }

    private void configureSourceSetDefaults(final JavaBasePlugin javaBasePlugin) {
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().all(new Action<SourceSet>() {
            public void execute(SourceSet sourceSet) {
                final DefaultJawaSourceSet jawaSourceSet = new DefaultJawaSourceSet(((DefaultSourceSet) sourceSet).getDisplayName(), sourceDirectorySetFactory)
                new DslObject(sourceSet).getConvention().getPlugins().put("jawa", jawaSourceSet)

                jawaSourceSet.jawa.srcDir(String.format("src/%s/jawa", sourceSet.getName()))
                sourceSet.getResources().getFilter().exclude(new Spec<FileTreeElement>() {
                    public boolean isSatisfiedBy(FileTreeElement element) {
                        return jawaSourceSet.jawa.contains(element.getFile())
                    }
                })
                sourceSet.allJava.source(jawaSourceSet.jawa)
                sourceSet.getAllSource().source(jawaSourceSet.jawa)

                String compileTaskName = sourceSet.getCompileTaskName("jawa")
                JawaCompile compile = project.getTasks().create(compileTaskName, JawaCompile.class)
                javaBasePlugin.configureForSourceSet(sourceSet, compile)
                compile.dependsOn(sourceSet.getCompileJavaTaskName())
                compile.setDescription(String.format("Compiles the %s Jawa source.", sourceSet.getName()))
                compile.setSource(jawaSourceSet.jawa)

                project.getTasks().getByName(sourceSet.getClassesTaskName()).dependsOn(compileTaskName)
            }
        })
    }
}