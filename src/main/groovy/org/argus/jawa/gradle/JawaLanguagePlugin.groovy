/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle

import org.argus.jawa.gradle.tasks.PlatformJawaCompile
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.internal.service.ServiceRegistry
import org.gradle.jvm.JvmByteCode
import org.gradle.jvm.internal.JvmAssembly
import org.gradle.jvm.internal.WithJvmAssembly
import org.gradle.language.base.LanguageSourceSet
import org.gradle.language.base.internal.JointCompileTaskConfig
import org.gradle.language.base.internal.registry.LanguageTransform
import org.gradle.language.base.internal.registry.LanguageTransformContainer
import org.gradle.language.base.plugins.ComponentModelBasePlugin
import org.gradle.language.java.JavaSourceSet
import org.gradle.language.jvm.plugins.JvmResourcesPlugin
import org.gradle.language.scala.internal.ScalaJvmAssembly
import org.gradle.model.Mutate
import org.gradle.model.RuleSource
import org.gradle.platform.base.BinarySpec
import org.gradle.platform.base.ComponentType
import org.gradle.platform.base.TypeBuilder

/**
 * JawaLanguagePlugin adds jawa language support to gradle.
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaLanguagePlugin implements Plugin<Project> {
    /**
     * Apply this plugin to the given target object.
     *
     * @param target The target object
     */
    @Override
    void apply(Project project) {
        project.getPluginManager().apply(ComponentModelBasePlugin.class)
        project.getPluginManager().apply(JvmResourcesPlugin.class)
    }

    @SuppressWarnings("UnusedDeclaration")
    static class Rules extends RuleSource {
        @ComponentType
        void registerLanguage(TypeBuilder<JawaLanguageSourceSet> builder) {
            builder.defaultImplementation(DefaultJawaLanguageSourceSet.class)
        }

        @Mutate
        void registerLanguageTransform(LanguageTransformContainer languages, ServiceRegistry serviceRegistry) {
            languages.add(new Jawa())
        }
    }

    private static class Jawa implements LanguageTransform<JawaLanguageSourceSet, JvmByteCode> {
        @Override
        public String getLanguageName() {
            return "jawa";
        }

        @Override
        public Class<JawaLanguageSourceSet> getSourceSetType() {
            return JawaLanguageSourceSet.class;
        }

        @Override
        public Map<String, Class<?>> getBinaryTools() {
            return Collections.emptyMap();
        }

        @Override
        public Class<JvmByteCode> getOutputType() {
            return JvmByteCode.class;
        }

        @Override
        public JointCompileTaskConfig getTransformTask() {
            return new JointCompileTaskConfig() {
                public String getTaskPrefix() {
                    return "compile"
                }

                @Override
                public boolean canTransform(LanguageSourceSet candidate) {
                    return candidate instanceof JawaLanguageSourceSet || candidate instanceof JavaSourceSet
                }

                public Class<? extends DefaultTask> getTaskType() {
                    return PlatformJawaCompile.class
                }

                public void configureTask(Task task, BinarySpec binarySpec, LanguageSourceSet sourceSet, ServiceRegistry serviceRegistry) {
                    PlatformJawaCompile compile = (PlatformJawaCompile) task;
                    configureJawaTask(compile, ((WithJvmAssembly) binarySpec).getAssembly(), "Compiles " + sourceSet + ".");
                    addSourceSetToCompile(compile, sourceSet);
                    addSourceSetClasspath(compile, (JawaLanguageSourceSet) sourceSet);
                }

                @Override
                public void configureAdditionalTransform(Task task, LanguageSourceSet sourceSet) {
                    PlatformJawaCompile compile = (PlatformJawaCompile) task;
                    addSourceSetToCompile(compile, sourceSet);
                }

                private void configureJawaTask(PlatformJawaCompile compile, JvmAssembly assembly, String description) {
                    assembly.builtBy(compile);

                    compile.setDescription(description);
                    compile.setDestinationDir(single(assembly.getClassDirectories()));
                    File analysisFile = new File(compile.getProject().getBuildDir(), "tmp/scala/compilerAnalysis/" + compile.getName() + ".analysis");
                    compile.getScalaCompileOptions().getIncrementalOptions().setAnalysisFile(analysisFile);

                    JavaPlatform javaPlatform = assembly.getTargetPlatform();
                    String targetCompatibility = javaPlatform.getTargetCompatibility().toString();
                    compile.setTargetCompatibility(targetCompatibility);
                    compile.setSourceCompatibility(targetCompatibility);

                    if (assembly instanceof ScalaJvmAssembly) {
                        compile.setPlatform(((ScalaJvmAssembly) assembly).getScalaPlatform());
                    } else {
                        compile.setPlatform(new DefaultScalaPlatform());
                    }
                }

                private void addSourceSetToCompile(PlatformJawaCompile compile, LanguageSourceSet sourceSet) {
                    compile.dependsOn(sourceSet);
                    compile.source(sourceSet.getSource());
                }

                private void addSourceSetClasspath(PlatformJawaCompile compile, JawaLanguageSourceSet jawaLanguageSourceSet) {
                    FileCollection classpath = jawaLanguageSourceSet.getCompileClasspath().getFiles();
                    compile.setClasspath(classpath);
                }

            };
        }

        @Override
        public boolean applyToBinary(BinarySpec binary) {
            return binary instanceof WithJvmAssembly
        }
    }
}
