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

import org.gradle.api.tasks.compile.BaseForkOptions

/**
 * Fork options for Jawa compilation. Only take effect if {@code JawaCompileOptions.fork}
 * is {@code true}.
 *
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaForkOptions extends BaseForkOptions {
    private static final long serialVersionUID = 0;
}