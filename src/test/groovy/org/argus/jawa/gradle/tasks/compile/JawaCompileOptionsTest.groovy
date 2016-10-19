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

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*


/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
class JawaCompileOptionsTest {
    static final Map TEST_FORK_OPTION_MAP = [someForkOption: 'someForkOptionValue']

    JawaCompileOptions compileOptions

    @Before public void setUp()  {
        compileOptions = new JawaCompileOptions()
        compileOptions.forkOptions = [optionMap: {TEST_FORK_OPTION_MAP}] as JawaForkOptions
    }

    @Test public void testCompileOptions() {
        assertTrue(compileOptions.failOnError)
        assertFalse(compileOptions.listFiles)
        assertFalse(compileOptions.verbose)
        assertTrue(compileOptions.fork)
        assertEquals(['java', 'jawa'], compileOptions.fileExtensions)
        assertNotNull(compileOptions.forkOptions)
    }

    @Test public void testOptionMapForForkOptions() {
        Map optionMap = compileOptions.optionMap()
        assertEquals(optionMap.subMap(TEST_FORK_OPTION_MAP.keySet()), TEST_FORK_OPTION_MAP)
    }

    @Test public void testOptionMapWithTrueFalseValues() {
        Map booleans = [
                failOnError: 'failOnError',
                verbose: 'verbose',
                listFiles: 'listFiles',
                fork: 'fork'
        ]
        booleans.keySet().each {compileOptions."$it" = true}
        Map optionMap = compileOptions.optionMap()
        booleans.values().each {
            if (it == 'nowarn') {
                assertEquals(false, optionMap[it])
            } else {
                assertEquals(true, optionMap[it])
            }
        }
        booleans.keySet().each {compileOptions."$it" = false}
        optionMap = compileOptions.optionMap()
        booleans.values().each {
            if (it == 'nowarn') {
                assertEquals(true, optionMap[it])
            } else {
                assertEquals(false, optionMap[it])
            }
        }
    }

    @Test public void testFork() {
        compileOptions.fork = false
        boolean forkUseCalled = false
        compileOptions.forkOptions = [define: {Map args ->
            forkUseCalled = true
            assertEquals(TEST_FORK_OPTION_MAP, args)
        }] as JawaForkOptions
        assert compileOptions.fork(TEST_FORK_OPTION_MAP).is(compileOptions)
        assertTrue(compileOptions.fork)
        assertTrue(forkUseCalled)
    }

    @Test public void testDefine() {
        compileOptions.verbose = false
        compileOptions.fork = false
        assertFalse(compileOptions.verbose)
    }
}