/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.spec

import org.gradle.api.internal.tasks.compile.JvmLanguageCompileSpec

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public interface JawaCompileSpec extends JvmLanguageCompileSpec {
    Map<File, File> getAnalysisMap()

    void setAnalysisMap(Map<File, File> analysisMap)
}