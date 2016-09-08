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

import org.argus.jawa.gradle.platform.JawaPlatform
import org.gradle.platform.base.ToolChain
import org.gradle.platform.base.internal.toolchain.ToolChainInternal

/**
 * A set of tools for building Jawa applications
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public interface JawaToolChain extends ToolChain, ToolChainInternal<JawaPlatform> {
}