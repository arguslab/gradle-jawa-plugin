/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.tasks.compile

import org.gradle.api.Task
import org.gradle.api.internal.AbstractTask
import org.gradle.api.internal.ConventionAwareHelper
import org.gradle.api.internal.ConventionTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.project.taskfactory.ITaskFactory
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.util.GUtil
import spock.lang.Specification

import static org.junit.Assert.*

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
abstract class AbstractConventionTaskTest extends Specification {

    public abstract AbstractTask getTask()

    protected DefaultProject project

    def setup() {
        project = ProjectBuilder.builder().build() as DefaultProject
    }

    public <T extends AbstractTask> T createTask(Class<T> type) {
        return createTask(type, project, "testTask")
    }

    public Task createTask(ProjectInternal project, String name) {
        return createTask(getTask().getClass(), project, name) as Task
    }

    public <T extends AbstractTask> T createTask(Class<T> type, ProjectInternal project, String name) {
        Task task = project.getServices().get(ITaskFactory.class).createTask(GUtil.map(Task.TASK_TYPE, type, Task.TASK_NAME, name))
        assertTrue(type.isAssignableFrom(task.getClass()))
        return type.cast(task)
    }

    def "is aware of conventions"() {
        given:
        ConventionTask task = (ConventionTask) getTask();

        expect:
        task.getConventionMapping() instanceof ConventionAwareHelper

        when:
        ConventionAwareHelper conventionMapping = (ConventionAwareHelper) task.getConventionMapping();

        then:
        conventionMapping.getConvention().is(project.convention)
    }
}