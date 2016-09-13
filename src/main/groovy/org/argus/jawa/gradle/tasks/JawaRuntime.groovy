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

import com.google.common.collect.Lists
import org.argus.jawa.gradle.plugins.JawaJarFile
import org.gradle.api.GradleException
import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.LazilyInitializedFileCollection
import org.gradle.api.internal.tasks.TaskDependencyResolveContext

/**
 * Provides information related to the Jawa runtime(s) used in a project. Added by the
 * {@link org.argus.jawa.gradle.plugins.JawaBasePlugin} as a project extension named {@code jawaRuntime}.
 *
 * <p>Example usage:
 *
 * <pre autoTested="">
 *     apply plugin: "org.argus.jawa"
 *
 *     repositories {
 *         mavenCentral()
 *     }
 *
 *     dependencies {
 *         compile "com.github.arguslab:jawa-compiler_2.11:1.0.1"
 *     }
 *
 *     def jawaClasspath = jawaRuntime.inferJawaClasspath(configurations.compile)
 *     // The returned class path can be used to configure the 'jawaClasspath' property of tasks
 *     // such as 'JawaCompile', or to execute these and other Jawa tools directly.
 * </pre>
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
@Incubating
class JawaRuntime {
    private Project project;

    public JawaRuntime(Project project) {
        this.project = project
    }

    /**
     * Searches the specified class path for Jawa Jars ({@code jawa-compiler}) and returns a corresponding class path for executing Jawa tools such as the Jawa
     * compiler. The tool versions will match those of the Jawa Jars found. If no Jawa Jars are found on the specified class path, a class path with the contents of the {@code
     * jawa} configuration will be returned.
     *
     * <p>The returned class path may be empty, or may fail to resolve when asked for its contents.
     *
     * @param classpath a class path containing Jawa Jars
     * @return a corresponding class path for executing Groovy tools such as the Groovy compiler and Groovydoc tool
     */
    public FileCollection inferJawaClasspath(final Iterable<File> classpath) {
        // alternatively, we could return project.files(Runnable)
        // would differ in at least the following ways: 1. live 2. no autowiring
        def myProject = project
        return new LazilyInitializedFileCollection() {
            @Override
            public String getDisplayName() {
                return "Jawa runtime classpath"
            }

            @Override
            public FileCollection createDelegate() {
                def jawaJar = findJawaJarFile(classpath)
                if (jawaJar == null) {
                    throw new GradleException(String.format("Cannot infer Jawa class path because no Jawa Jar was found on class path: %s", classpath))
                }
                if (myProject.repositories.isEmpty()) {
                    throw new GradleException("Cannot infer Jawa class path because no repository is declared for the project.")
                }

                String notation = jawaJar.getDependencyNotation()
                List<Dependency> dependencies = Lists.newArrayList()
                // project.getDependencies().create(String) seems to be the only feasible way to create a Dependency with a classifier
                dependencies.add(myProject.getDependencies().create(notation))
                return myProject.getConfigurations().detachedConfiguration(dependencies.toArray(new Dependency[0]))
            }

            // let's override this so that delegate isn't created at autowiring time (which would mean on every build)
            @Override
            public void visitDependencies(TaskDependencyResolveContext context) {
                if (classpath instanceof Buildable) {
                    context.add(classpath)
                }
            }
        }
    }

    public static JawaJarFile findJawaJarFile(Iterable<File> classpath) {
        if (classpath == null) {
            return null
        }
        for (File file : classpath) {
            def jawaJar = JawaJarFile.parse(file)
            if (jawaJar != null) {
                return jawaJar
            }
        }
        return null
    }
}
