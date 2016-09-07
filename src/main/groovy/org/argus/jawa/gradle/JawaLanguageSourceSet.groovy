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

import org.gradle.api.Incubating
import org.gradle.jvm.Classpath
import org.gradle.language.base.LanguageSourceSet

/**
 * A set of sources passed to the Jawa compiler.
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
@Incubating
public interface JawaLanguageSourceSet extends LanguageSourceSet {
    Classpath getCompileClasspath()
}