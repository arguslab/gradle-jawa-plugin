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

import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec
import org.gradle.api.internal.ConventionTask
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.tasks.WorkResult
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.language.base.internal.compile.Compiler
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.util.GFileUtils
import spock.lang.Unroll

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
class JawaCompileTest extends AbstractCompileTest {
    private JawaCompile testObj

    Compiler<JawaJavaJointCompileSpec> jawaCompilerMock = Mock()

    @Override
    public AbstractCompile getCompile() {
        return testObj
    }

    @Override
    public ConventionTask getTask() {
        return testObj
    }

    def setup() {
        testObj = createTask(JawaCompile)
        testObj.setCompiler(jawaCompilerMock)

        GFileUtils.touch(new File(srcDir, "incl/file.groovy"))
    }

    @Unroll
    def "execute: doing work == #doingWork"() {
        given:
        setUpMocksAndAttributes(testObj)

        when:
        testObj.compile()

        then:
        1 * jawaCompilerMock.execute(_ as JawaJavaJointCompileSpec) >> new ExpectedWorkResult(doingWork)
        testObj.didWork == doingWork

        where:
        doingWork << [true, false]
    }

    private void setUpMocksAndAttributes(JawaCompile compile) {
        super.setUpMocksAndAttributes(compile)
        compile.source(srcDir)
    }

    private class ExpectedWorkResult implements WorkResult {
        private boolean didWork

        ExpectedWorkResult(boolean didWork) {
            this.didWork = didWork
        }

        @Override
        boolean getDidWork() {
            return didWork
        }
    }
}