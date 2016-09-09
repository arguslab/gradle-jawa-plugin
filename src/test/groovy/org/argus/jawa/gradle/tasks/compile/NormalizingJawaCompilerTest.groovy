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

import groovy.transform.InheritConstructors
import org.argus.jawa.gradle.tasks.compile.spec.DefaultJawaJavaJointCompileSpec
import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.tasks.compile.CompileOptions
import spock.lang.Specification

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
class NormalizingJawaCompilerTest extends Specification {
    org.gradle.language.base.internal.compile.Compiler<JawaJavaJointCompileSpec> target = Mock()
    DefaultJawaJavaJointCompileSpec spec = new DefaultJawaJavaJointCompileSpec()
    NormalizingJawaCompiler compiler = new NormalizingJawaCompiler(target)

    def setup() {
        spec.classpath = files('Dep1.jar', 'Dep2.jar', 'Dep3.jar')
        spec.source = files('House.scala', 'Person1.java', 'package.html', 'Person2.pilar')
        spec.destinationDir = new File("destinationDir")
        spec.compileOptions = new CompileOptions()
        spec.jawaCompileOptions = new JawaCompileOptions()
    }

    def "silently excludes source files not ending in .java or .pilar by default"() {
        when:
        compiler.execute(spec)

        then:
        1 * target.execute(spec) >> {
            assert spec.source.files == files('Person1.java', 'Person2.pilar').files
        }
    }

    def "excludes source files that have extension different from specified by fileExtensions option"() {
        spec.jawaCompileOptions.fileExtensions = ['html']

        when:
        compiler.execute(spec)

        then:
        1 * target.execute(spec) >> {
            assert spec.source.files == files('package.html').files
        }
    }

    private static files(String... paths) {
        new TestFileCollection(paths.collect { new File(it) })
    }

    // file collection whose type is distinguishable from SimpleFileCollection
    @InheritConstructors
    static class TestFileCollection extends SimpleFileCollection {}
}