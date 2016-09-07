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

import org.argus.jawa.compiler.compile.CompileProgress
import org.argus.jawa.core.MsgLevel$
import org.argus.jawa.core.PrintReporter
import org.argus.jawa.gradle.spec.JawaJavaJointCompileSpec
import org.gradle.api.internal.tasks.SimpleWorkResult
import org.gradle.api.internal.tasks.compile.CompilationFailedException
import org.gradle.api.internal.tasks.compile.JavaCompilerArgumentsBuilder
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.WorkResult
import org.gradle.cache.CacheRepository
import org.gradle.cache.PersistentCache
import org.gradle.cache.internal.CacheRepositoryServices
import org.gradle.cache.internal.FileLockManager
import org.gradle.internal.Factory
import org.gradle.internal.SystemProperties
import org.gradle.internal.nativeintegration.services.NativeServices
import org.gradle.internal.service.DefaultServiceRegistry
import org.gradle.internal.service.scopes.GlobalScopeServices
import org.gradle.util.GFileUtils

import static org.gradle.cache.internal.filelock.LockOptionsBuilder.mode

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaCompiler implements org.gradle.language.base.internal.compile.Compiler<JawaJavaJointCompileSpec>, Serializable {
    private static final Logger LOGGER = Logging.getLogger(JawaCompiler.class)
    private Iterable<File> jawaClasspath
    private File gradleUserHome

    private static final String JAWA_DIR_SYSTEM_PROPERTY = "jawa.dir"
    public static final String JAWA_DIR_IGNORED_MESSAGE = "In order to guarantee parallel safe Jawa compilation, Gradle does not support the '" + JAWA_DIR_SYSTEM_PROPERTY + "' system property and ignores any value provided."


    public JawaCompiler(Iterable<File> jawaClasspath, File gradleUserHome) {
        this.jawaClasspath = jawaClasspath
        this.gradleUserHome = gradleUserHome
    }

    @Override
    public WorkResult execute(JawaJavaJointCompileSpec spec) {
        return MyCompiler.execute(jawaClasspath, gradleUserHome, spec)
    }

    private static class MyCompiler {
        static WorkResult execute(final Iterable<File> jawaClasspath, File gradleUserHome, final JawaJavaJointCompileSpec spec) {
            LOGGER.info("Compiling with Jawa compiler.")

            final def logger = new LoggerAdapter()

            def compiler = createParallelSafeCompiler(gradleUserHome)

            def scalacOptions = new JawaCompilerArgumentsGenerator().generate(spec)
            List<String> javacOptions = new JavaCompilerArgumentsBuilder(spec).includeClasspath(false).build()
            Inputs inputs = Inputs.create(ImmutableList.copyOf(spec.getClasspath()), ImmutableList.copyOf(spec.getSource()), spec.getDestinationDir(),
                    scalacOptions, javacOptions, spec.getScalaCompileOptions().getIncrementalOptions().getAnalysisFile(), spec.getAnalysisMap(), "mixed", getIncOptions(), true)
            if (LOGGER.isDebugEnabled()) {
                Inputs.debug(inputs, logger)
            }

            if (spec.getCompileOptions().isForce()) {
                GFileUtils.deleteDirectory(spec.getDestinationDir())
            }

            try {
                compiler.compile(sources, outputs, new PrintReporter(MsgLevel$.MODULE$.INFO()), logger, new NoCompileProgress)
            } catch (Exception e) {
                throw new CompilationFailedException(e)
            }

            return new SimpleWorkResult(true)
        }

        static org.argus.jawa.compiler.compile.JawaCompiler createCompiler() {
            new org.argus.jawa.compiler.compile.JawaCompiler()
        }

        static org.argus.jawa.compiler.compile.JawaCompiler createParallelSafeCompiler(File gradleUserHome) {
            def cacheHomeDir = gradleUserHome
            def cacheRepository = JawaCompilerServices.getInstance(cacheHomeDir).get(CacheRepository.class)
            final PersistentCache cache = cacheRepository.cache("jawa")
                    .withDisplayName("Jawa compiler cache")
                    .withLockOptions(mode(FileLockManager.LockMode.Exclusive))
                    .open()
            final File cacheDir = cache.getBaseDir()

            final String userSuppliedJawaDir = System.getProperty(JAWA_DIR_SYSTEM_PROPERTY)
            if (userSuppliedJawaDir != null && userSuppliedJawaDir != cacheDir.getAbsolutePath()) {
                LOGGER.warn(JAWA_DIR_IGNORED_MESSAGE)
            }

            org.argus.jawa.compiler.compile.JawaCompiler compiler = SystemProperties.getInstance().withSystemProperty(JAWA_DIR_SYSTEM_PROPERTY, cacheDir.getAbsolutePath(), new Factory<org.argus.jawa.compiler.compile.JawaCompiler>() {
                @Override
                public org.argus.jawa.compiler.compile.JawaCompiler create() {
                    return cache.useCache("initialize", new Factory<org.argus.jawa.compiler.compile.JawaCompiler>() {
                        @Override
                        public org.argus.jawa.compiler.compile.JawaCompiler create() {
                            return createCompiler()
                        }
                    })
                }
            })
            cache.close()

            compiler
        }
    }

    private static class NoCompileProgress implements CompileProgress {

        @Override
        void startUnit(String unitPath) {

        }

        @Override
        boolean advance(int current, int total) {
            return true
        }
    }


    private static class LoggerAdapter implements org.argus.jawa.compiler.log.Logger {
        @Override
        public void error(String msg) {
            LOGGER.error(msg)
        }

        @Override
        public void warn(String msg) {
            LOGGER.warn(msg)
        }

        @Override
        public void info(String msg) {
            LOGGER.info(msg)
        }

        @Override
        public void debug(String msg) {
            LOGGER.debug(msg)
        }

        @Override
        public void trace(Throwable exception) {
            LOGGER.trace(exception.getMessage())
        }
    }

    private static class JawaCompilerServices extends DefaultServiceRegistry {
        private static JawaCompilerServices instance

        private JawaCompilerServices(File gradleUserHome) {
            super(NativeServices.getInstance())

            addProvider(new GlobalScopeServices(true))
            addProvider(new CacheRepositoryServices(gradleUserHome, null))
        }

        public static JawaCompilerServices getInstance(File gradleUserHome) {
            if (instance == null) {
                NativeServices.initialize(gradleUserHome)
                instance = new JawaCompilerServices(gradleUserHome)
            }
            return instance
        }
    }
}
