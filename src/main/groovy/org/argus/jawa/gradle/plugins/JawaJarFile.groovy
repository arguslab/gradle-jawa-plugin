/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.plugins

import org.gradle.api.Nullable
import org.gradle.util.VersionNumber

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
class JawaJarFile {
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("(jawa-compiler_2\\.11)-(\\d.*?).jar")

    private File file
    private Matcher matcher

    private JawaJarFile(File file, Matcher matcher) {
        this.file = file
        this.matcher = matcher
    }

    public File getFile() {
        return file
    }

    public String getBaseName() {
        return matcher.group(1)
    }

    public VersionNumber getVersion() {
        return VersionNumber.parse(matcher.group(2))
    }

    public String getDependencyNotation() {
        "com.github.arguslab:" + getBaseName() + ":" + getVersion()
    }

    @Nullable
    public static JawaJarFile parse(File file) {
        Matcher matcher = FILE_NAME_PATTERN.matcher(file.name)
        return matcher.matches() ? new JawaJarFile(file, matcher) : null
    }
}
