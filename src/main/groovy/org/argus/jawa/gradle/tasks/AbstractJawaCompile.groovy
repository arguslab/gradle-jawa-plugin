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

import com.google.common.base.Predicate
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.compile.CompileOptions

/**
 * An abstract Jawa compile task sharing common functionality for compiling jawa.
 */
@Incubating
public abstract class AbstractJawaCompile extends AbstractCompile {
    protected static final Logger LOGGER = Logging.getLogger(AbstractJawaCompile.class);
//    private final BaseJawaCompileOptions scalaCompileOptions;
    private final CompileOptions compileOptions = new CompileOptions();

//    protected AbstractScalaCompile(BaseScalaCompileOptions scalaCompileOptions) {
//        this.scalaCompileOptions = scalaCompileOptions;
//    }

    /**
     * Returns the Java compilation options.
     */
    @Nested
    public CompileOptions getOptions() {
        return compileOptions;
    }

    abstract protected org.gradle.language.base.internal.compile.Compiler<ScalaJavaJointCompileSpec> getCompiler(ScalaJavaJointCompileSpec spec);

    @Override
    @TaskAction
    protected void compile() {
        ScalaJavaJointCompileSpec spec = createSpec();
        configureIncrementalCompilation(spec);
        getCompiler(spec).execute(spec);
    }

    protected ScalaJavaJointCompileSpec createSpec() {
        DefaultScalaJavaJointCompileSpec spec = new DefaultScalaJavaJointCompileSpecFactory(compileOptions).create();
        spec.setSource(getSource());
        spec.setDestinationDir(getDestinationDir());
        spec.setWorkingDir(getProject().getProjectDir());
        spec.setTempDir(getTemporaryDir());
        spec.setClasspath(getClasspath());
        spec.setSourceCompatibility(getSourceCompatibility());
        spec.setTargetCompatibility(getTargetCompatibility());
        spec.setCompileOptions(getOptions());
        return spec;
    }

    protected void configureIncrementalCompilation(ScalaCompileSpec spec) {

        Map<File, File> globalAnalysisMap = createOrGetGlobalAnalysisMap();
        HashMap<File, File> filteredMap = filterForClasspath(globalAnalysisMap, spec.getClasspath());
        spec.setAnalysisMap(filteredMap);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Analysis file: {}", scalaCompileOptions.getIncrementalOptions().getAnalysisFile());
            LOGGER.debug("Published code: {}", scalaCompileOptions.getIncrementalOptions().getPublishedCode());
            LOGGER.debug("Analysis map: {}", filteredMap);
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<File, File> createOrGetGlobalAnalysisMap() {
        ExtraPropertiesExtension extraProperties = getProject().getRootProject().getExtensions().getExtraProperties();
        Map<File, File> analysisMap;

        if (extraProperties.has("scalaCompileAnalysisMap")) {
            analysisMap = (Map) extraProperties.get("scalaCompileAnalysisMap");
        } else {
            analysisMap = Maps.newHashMap();
            for (Project project : getProject().getRootProject().getAllprojects()) {
                for (AbstractJawaCompile task : project.getTasks().withType(AbstractJawaCompile.class)) {
                    File publishedCode = task.getScalaCompileOptions().getIncrementalOptions().getPublishedCode();
                    File analysisFile = task.getScalaCompileOptions().getIncrementalOptions().getAnalysisFile();
                    analysisMap.put(publishedCode, analysisFile);
                }
            }
            extraProperties.set("scalaCompileAnalysisMap", Collections.unmodifiableMap(analysisMap));
        }
        return analysisMap;
    }


    protected HashMap<File, File> filterForClasspath(Map<File, File> analysisMap, Iterable<File> classpath) {
        final Set<File> classpathLookup = Sets.newHashSet(classpath);
        return Maps.newHashMap(Maps.filterEntries(analysisMap, new Predicate<Map.Entry<File, File>>() {
            public boolean apply(Map.Entry<File, File> entry) {
                return classpathLookup.contains(entry.getKey());
            }
        }));
    }
}