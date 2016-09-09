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
import org.gradle.api.internal.artifacts.configurations.Configurations
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.junit.Before
import org.junit.Test

import static org.argus.jawa.gradle.tasks.TaskDependencyMatchers.dependsOn
import static org.gradle.util.WrapUtil.toLinkedSet
import static org.hamcrest.Matchers.*
import static org.junit.Assert.*

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
class JawaPluginTest {
    Project project
    JawaPlugin jawaPlugin

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        jawaPlugin = new JawaPlugin()
    }

    @Test
    public void appliesTheJavaPluginToTheProject() {
        jawaPlugin.apply(project)

        assertTrue(project.getPlugins().hasPlugin(JavaPlugin));
    }

    @Test
    public void addsJawaConfigurationToTheProject() {
        jawaPlugin.apply(project)

        def configuration = project.configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME)
        assertThat(Configurations.getNames(configuration.extendsFrom), new BaseMatcher<Iterable<?>>() {
            public boolean matches(Object o) {
                Iterable<?> iterable = (Iterable<?>) o;
                return iterable != null && !iterable.iterator().hasNext();
            }

            public void describeTo(Description description) {
                description.appendText("an empty Iterable");
            }
        })
        assertFalse(configuration.visible)
        assertTrue(configuration.transitive)
    }

    @Test
    public void addsJawaConventionToEachSourceSet() {
        jawaPlugin.apply(project)

        def sourceSet = project.sourceSets.main
        assertThat(sourceSet.jawa.displayName, equalTo("main Jawa source"))
        assertThat(sourceSet.jawa.srcDirs, equalTo(toLinkedSet(project.file("src/main/jawa"))))

        sourceSet = project.sourceSets.test
        assertThat(sourceSet.jawa.displayName, equalTo("test Jawa source"))
        assertThat(sourceSet.jawa.srcDirs, equalTo(toLinkedSet(project.file("src/test/jawa"))))
    }

    @Test
    public void addsCompileTaskForEachSourceSet() {
        jawaPlugin.apply(project)

        def task = project.tasks['compileJawa']
        assertThat(task, instanceOf(JawaCompile))
        assertThat(task.description, equalTo('Compiles the main Jawa source.'))
        assertThat(project.tasks[JavaPlugin.COMPILE_JAVA_TASK_NAME], dependsOn('compileJawa'))

        task = project.tasks['compileTestJawa']
        assertThat(task, instanceOf(JawaCompile))
        assertThat(task.description, equalTo('Compiles the test Jawa source.'))
        assertThat(task, dependsOn(JavaPlugin.CLASSES_TASK_NAME))
        assertThat(project.tasks[JavaPlugin.COMPILE_TEST_JAVA_TASK_NAME], dependsOn('compileTestJawa', JavaPlugin.CLASSES_TASK_NAME))
    }

    @Test
    public void dependenciesOfJavaPluginTasksIncludeJawaCompileTasks() {
        jawaPlugin.apply(project)

        def task = project.tasks[JavaPlugin.CLASSES_TASK_NAME]
        assertThat(task, dependsOn(hasItem('compileJawa')))

        task = project.tasks[JavaPlugin.TEST_CLASSES_TASK_NAME]
        assertThat(task, dependsOn(hasItem('compileTestJawa')))
    }
}
