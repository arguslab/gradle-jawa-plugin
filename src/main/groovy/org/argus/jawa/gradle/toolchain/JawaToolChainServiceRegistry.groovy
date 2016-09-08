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

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.GradleInternal
import org.gradle.api.internal.tasks.compile.daemon.CompilerDaemonManager
import org.gradle.internal.service.ServiceRegistration
import org.gradle.internal.service.scopes.PluginServiceRegistry

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaToolChainServiceRegistry implements PluginServiceRegistry {

    @Override
    public void registerGlobalServices(ServiceRegistration registration) {
    }

    @Override
    public void registerBuildSessionServices(ServiceRegistration registration) {
    }

    @Override
    public void registerBuildServices(ServiceRegistration registration) {
    }

    @Override
    public void registerGradleServices(ServiceRegistration registration) {
    }

    @Override
    public void registerProjectServices(ServiceRegistration registration) {
        registration.addProvider(new ProjectScopeCompileServices());
    }


    private static class ProjectScopeCompileServices {
        JawaToolChain createJawaToolChain(GradleInternal gradle, CompilerDaemonManager compilerDaemonManager, ConfigurationContainer configurationContainer, DependencyHandler dependencyHandler) {
            return new DownloadingJawaToolChain(gradle.getGradleUserHomeDir(), gradle.getRootProject().getProjectDir(), compilerDaemonManager, configurationContainer, dependencyHandler);
        }
    }
}