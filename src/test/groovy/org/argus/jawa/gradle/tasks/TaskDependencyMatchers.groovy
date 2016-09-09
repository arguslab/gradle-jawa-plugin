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

import org.gradle.api.Task
import org.hamcrest.*

import static org.hamcrest.Matchers.equalTo

class TaskDependencyMatchers {
    @Factory
    static Matcher<Task> dependsOn(final String... tasks) {
        return dependsOn(equalTo(new HashSet<String>(Arrays.asList(tasks))))
    }

    @Factory
    static Matcher<Task> dependsOn(Matcher<? extends Iterable<String>> matcher) {
        return dependsOn(matcher, false)
    }

//    @Factory
//    static Matcher<Task> dependsOnPaths(Matcher<? extends Iterable<String>> matcher) {
//        return dependsOn(matcher, true)
//    }

    static Matcher<Task> dependsOn(final Matcher<? extends Iterable<String>> matcher, final boolean matchOnPaths) {
        return new BaseMatcher<Task>() {
            public boolean matches(Object o) {
                Task task = (Task) o
                Set<String> names = new HashSet<String>()
                Set<? extends Task> depTasks = task.getTaskDependencies().getDependencies(task)
                for (Task depTask : depTasks) {
                    names.add(matchOnPaths ? depTask.getPath() : depTask.getName())
                }
                boolean matches = matcher.matches(names)
                if (!matches) {
                    StringDescription description = new StringDescription()
                    matcher.describeTo(description)
                    System.out.println(String.format("expected %s, got %s.", description.toString(), names))
                }
                return matches
            }

            public void describeTo(Description description) {
                description.appendText("a Task that depends on ").appendDescriptionOf(matcher)
            }
        }
    }

//    @Factory
//    public static <T extends Buildable> Matcher<T> builtBy(String... tasks) {
//        return builtBy(equalTo(new HashSet<String>(Arrays.asList(tasks))))
//    }

//    @Factory
//    public static <T extends Buildable> Matcher<T> builtBy(final Matcher<? extends Iterable<String>> matcher) {
//        return new BaseMatcher<T>() {
//            public boolean matches(Object o) {
//                Buildable task = (Buildable) o
//                Set<String> names = new HashSet<String>()
//                Set<? extends Task> depTasks = task.getBuildDependencies().getDependencies(null)
//                for (Task depTask : depTasks) {
//                    names.add(depTask.getName())
//                }
//                boolean matches = matcher.matches(names)
//                if (!matches) {
//                    StringDescription description = new StringDescription()
//                    matcher.describeTo(description)
//                    System.out.println(String.format("expected %s, got %s.", description.toString(), names))
//                }
//                return matches
//            }
//
//            public void describeTo(Description description) {
//                description.appendText("a Buildable that is built by ").appendDescriptionOf(matcher)
//            }
//        }
//    }
}
