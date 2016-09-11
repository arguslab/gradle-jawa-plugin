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

import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.util.WrapUtil

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
abstract class AbstractCompileTest extends AbstractConventionTaskTest {
    public static final String TEST_PATTERN_1 = "pattern1"
    public static final String TEST_PATTERN_2 = "pattern2"
    public static final String TEST_PATTERN_3 = "pattern3"

    public static final List<File> TEST_DEPENDENCY_MANAGER_CLASSPATH = WrapUtil.toList(new File("jar1"))
    public static final List<String> TEST_INCLUDES = WrapUtil.toList("incl/*")
    public static final List<String> TEST_EXCLUDES = WrapUtil.toList("excl/*")

    protected File srcDir
    protected File destDir
    protected File depCacheDir

    protected abstract AbstractCompile getCompile()

    def setup() {
        destDir = project.file("destDir")
        depCacheDir = project.file("depCache")
        srcDir = project.file("src")
        srcDir.mkdirs()
    }

    def "default values"() {
        given:
        def compile = getCompile()

        expect:
        compile.getDestinationDir() == null
        compile.getSourceCompatibility() == null
        compile.getTargetCompatibility() == null
        compile.source.isEmpty()
    }

    def "test includes"() {
        given:
        AbstractCompile compile = getCompile()

        expect:
        compile.is(compile.include(TEST_PATTERN_1, TEST_PATTERN_2))
        compile.getIncludes() == WrapUtil.toLinkedSet(TEST_PATTERN_1, TEST_PATTERN_2)

        and:
        compile.is(compile.include(TEST_PATTERN_3))
        compile.getIncludes() == WrapUtil.toLinkedSet(TEST_PATTERN_1, TEST_PATTERN_2, TEST_PATTERN_3)
    }

    def "test excludes"() {
        given:
        AbstractCompile compile = getCompile()

        expect:
        compile.is(compile.exclude(TEST_PATTERN_1, TEST_PATTERN_2))
        compile.getExcludes() == WrapUtil.toLinkedSet(TEST_PATTERN_1, TEST_PATTERN_2)

        and:
        compile.is(compile.exclude(TEST_PATTERN_3))
        compile.getExcludes() == WrapUtil.toLinkedSet(TEST_PATTERN_1, TEST_PATTERN_2, TEST_PATTERN_3)
    }

    protected void setUpMocksAndAttributes(final AbstractCompile compile) {
        compile.source(srcDir)
        compile.setIncludes(TEST_INCLUDES)
        compile.setExcludes(TEST_EXCLUDES)
        compile.setSourceCompatibility("1.5")
        compile.setTargetCompatibility("1.5")
        compile.setDestinationDir(destDir)

        compile.setClasspath(new SimpleFileCollection(TEST_DEPENDENCY_MANAGER_CLASSPATH))
    }
}