/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.toolchain

import org.gradle.api.GradleException
import org.gradle.internal.text.TreeFormatter
import org.gradle.language.base.internal.compile.CompileSpec
import org.gradle.platform.base.internal.toolchain.ToolProvider
import org.gradle.util.TreeVisitor

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class NotFoundJawaToolProvider implements ToolProvider {
    private Exception exception

    public NotFoundJawaToolProvider(Exception moduleVersionNotFoundException) {
        this.exception = moduleVersionNotFoundException
    }

    @Override
    public <T extends CompileSpec> org.gradle.language.base.internal.compile.Compiler<T> newCompiler(Class<T> spec) {
        throw failure()
    }

    @Override
    public <T> T get(Class<T> toolType) {
        throw failure()
    }

    @Override
    public boolean isAvailable() {
        return false
    }

    private RuntimeException failure() {
        TreeFormatter formatter = new TreeFormatter()
        this.explain(formatter)
        return new GradleException(formatter.toString(), exception)
    }

    @Override
    public void explain(TreeVisitor<? super String> visitor) {
        visitor.node("Cannot provide Scala Compiler")
        visitor.startChildren()
        visitor.node(exception.getCause().getMessage())
        visitor.endChildren()
    }
}
