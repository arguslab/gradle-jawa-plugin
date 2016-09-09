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

import org.argus.jawa.gradle.tasks.compile.JawaCompile
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.argus.jawa.gradle.tasks.TaskDependencyMatchers.dependsOn
import static org.gradle.util.WrapUtil.toLinkedSet
import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
class JawaBasePluginTest {
    private Project project

    @Before
    void before() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply(JawaBasePlugin)
    }

    @Test void appliesTheJavaBasePluginToTheProject() {
        assertTrue(project.getPlugins().hasPlugin(JavaBasePlugin))
    }

    @Test void appliesMappingsToNewSourceSet() {
        def sourceSet = project.sourceSets.create('custom')
        assertThat(sourceSet.jawa.displayName, equalTo("custom Jawa source"))
        assertThat(sourceSet.jawa.srcDirs, equalTo(toLinkedSet(project.file("src/custom/jawa"))))
    }

    @Test void addsCompileTaskToNewSourceSet() {
        project.sourceSets.create('custom')

        def task = project.tasks['compileCustomJawa']
        assertThat(task, instanceOf(JawaCompile))
        assertThat(task.description, equalTo('Compiles the custom Jawa source.'))
        assertThat(project.tasks['compileCustomJava'], dependsOn('compileCustomJawa'))
    }

    @Test void dependenciesOfJavaPluginTasksIncludeJawaCompileTasks() {
        project.sourceSets.create('custom')
        def task = project.tasks['customClasses']
        assertThat(task, dependsOn(hasItem('compileCustomJawa')))
    }
}
