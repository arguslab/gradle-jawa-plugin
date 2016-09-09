/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.tasks.compile.spec

import org.gradle.api.internal.tasks.compile.CommandLineJavaCompileSpec
import org.gradle.api.internal.tasks.compile.ForkingJavaCompileSpec
import org.gradle.api.tasks.compile.CompileOptions
import spock.lang.Specification

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
class DefaultJawaJavaJointCompileSpecFactoryTest extends Specification {
    def "produces correct spec type" () {
        CompileOptions options = new CompileOptions()
        options.fork = fork
        options.forkOptions.executable = executable
        DefaultJawaJavaJointCompileSpecFactory factory = new DefaultJawaJavaJointCompileSpecFactory(options)

        when:
        def spec = factory.create()

        then:
        spec instanceof DefaultJawaJavaJointCompileSpec
        ForkingJavaCompileSpec.isAssignableFrom(spec.getClass()) == implementsForking
        CommandLineJavaCompileSpec.isAssignableFrom(spec.getClass()) == implementsCommandLine

        where:
        fork  | executable | implementsForking | implementsCommandLine
        false | null       | false             | false
        true  | null       | true              | false
        true  | "X"        | false             | true
    }
}