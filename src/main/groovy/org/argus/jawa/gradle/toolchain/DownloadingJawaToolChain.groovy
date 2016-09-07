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
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolveException
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.tasks.compile.daemon.CompilerDaemonManager
import org.gradle.platform.base.internal.toolchain.ToolProvider

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class DownloadingJawaToolChain implements JawaToolChain {
    private File gradleUserHomeDir
    private File rootProjectDir
    private CompilerDaemonManager compilerDaemonManager
    private ConfigurationContainer configurationContainer
    private DependencyHandler dependencyHandler
    private JavaVersion javaVersion

    public DownloadingJawaToolChain(File gradleUserHomeDir, File rootProjectDir, CompilerDaemonManager compilerDaemonManager, ConfigurationContainer configurationContainer, DependencyHandler dependencyHandler) {
        this.gradleUserHomeDir = gradleUserHomeDir
        this.rootProjectDir = rootProjectDir
        this.compilerDaemonManager = compilerDaemonManager
        this.configurationContainer = configurationContainer
        this.dependencyHandler = dependencyHandler
        this.javaVersion = JavaVersion.current()
    }

    @Override
    public String getName() {
        return String.format("Jawa Toolchain")
    }

    @Override
    public String getDisplayName() {
        return String.format("Jawa Toolchain (JDK %s (%s))", javaVersion.getMajorVersion(), javaVersion)
    }

    @Override
    public ToolProvider select(JawaPlatform targetPlatform) {
        try {
            def jawaClasspath = resolveDependency(String.format("com.github.arguslab:jawa-compiler:%s", targetPlatform.getJawaVersion()))
            def resolvedScalaClasspath = jawaClasspath.resolve()
            return new DefaultJawaToolProvider(gradleUserHomeDir, rootProjectDir, compilerDaemonManager, resolvedScalaClasspath, resolvedZincClasspath)

        } catch(ResolveException resolveException) {
            return new NotFoundJawaToolProvider(resolveException)
        }
    }

    private Configuration resolveDependency(Object dependencyNotation) {
        Dependency dependency = dependencyHandler.create(dependencyNotation)
        return configurationContainer.detachedConfiguration(dependency)
    }
}
