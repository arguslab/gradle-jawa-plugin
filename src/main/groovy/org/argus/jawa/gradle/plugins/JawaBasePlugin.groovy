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
        project.pluginManager.apply JavaBasePlugin
        def javaBasePlugin = project.plugins.getPlugin JavaBasePlugin
        configureSourceSetDefaults(javaBasePlugin)
    }

    private void configureSourceSetDefaults(final JavaBasePlugin javaBasePlugin) {
        project.convention.getPlugin(JavaPluginConvention).sourceSets.all(new Action<SourceSet>() {
            public void execute(SourceSet sourceSet) {
                final def jawaSourceSet = new DefaultJawaSourceSet((sourceSet as DefaultSourceSet).displayName, sourceDirectorySetFactory)
                new DslObject(sourceSet).convention.plugins.put("jawa", jawaSourceSet)

                jawaSourceSet.jawa.srcDir(String.format("src/%s/jawa", sourceSet.name))
                sourceSet.resources.filter.exclude(new Spec<FileTreeElement>() {
                    public boolean isSatisfiedBy(FileTreeElement element) {
                        return jawaSourceSet.jawa.contains(element.file)
                    }
                })
                sourceSet.allJava.source jawaSourceSet.jawa
                sourceSet.allSource.source jawaSourceSet.jawa

                def compileTaskName = sourceSet.getCompileTaskName("jawa")
                JawaCompile compile = project.tasks.create(compileTaskName, JawaCompile)
                javaBasePlugin.configureForSourceSet(sourceSet, compile)
                compile.setDescription(String.format("Compiles the %s Jawa source.", sourceSet.name))
                compile.setSource(jawaSourceSet.jawa)
                project.tasks.getByName(sourceSet.compileJavaTaskName).dependsOn(compileTaskName)
                project.tasks.getByName(sourceSet.classesTaskName).dependsOn(compileTaskName)
            }
        })
    }
}