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

import org.argus.jawa.compiler.compile.CompileProgress
import org.argus.jawa.compiler.compile.JawaCompiler
import org.argus.jawa.compiler.log.Level
import org.argus.jawa.core.MsgLevel
import org.argus.jawa.core.PrintReporter
import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec
import org.gradle.api.internal.tasks.SimpleWorkResult
import org.gradle.api.internal.tasks.compile.CompilationFailedException
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.WorkResult
import org.gradle.util.GFileUtils
import scala.collection.mutable.ListBuffer
import org.gradle.language.base.internal.compile.Compiler

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaGradleCompiler implements Compiler<JawaJavaJointCompileSpec>, Serializable {
    private static final Logger LOGGER = Logging.getLogger(JawaGradleCompiler)

    @Override
    public WorkResult execute(JawaJavaJointCompileSpec spec) {
        return MyCompiler.execute(spec)
    }

    private static class MyCompiler {
        static WorkResult execute(final JawaJavaJointCompileSpec spec) {
            LOGGER.info("Compiling with Jawa compiler.")

            final def logger = new LoggerAdapter()

            def compiler = new JawaCompiler()

            if (spec.jawaCompileOptions.force) {
                GFileUtils.deleteDirectory(spec.destinationDir)
            }

            def sources = spec.source.files
            def source_scala = new ListBuffer<File>()
            for(File src: sources) {
                source_scala.$plus$eq(src)
            }
            def outputDirs_scala = new ListBuffer<File>()
            outputDirs_scala.$plus$eq(spec.getDestinationDir())
            def reporter = new PrintReporter(MsgLevel.WARNING())
            if(spec.compileOptions.verbose)
                reporter = new PrintReporter(MsgLevel.INFO())
            try {
                compiler.compile(source_scala.toList(), outputDirs_scala.toList(), reporter, logger, new NoCompileProgress())
            } catch (Exception e) {
                throw new CompilationFailedException(e)
            }

            return new SimpleWorkResult(true)
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

        @Override
        void log(scala.Enumeration.Value level, String msg) {
            if(level == Level.Debug())
                LOGGER.log(LogLevel.DEBUG, msg)
            else if(level == Level.Error())
                LOGGER.log(LogLevel.ERROR, msg)
            else if(level == Level.Info())
                LOGGER.log(LogLevel.INFO, msg)
            else if(level == Level.Warn())
                LOGGER.log(LogLevel.WARN, msg)
            else LOGGER.log(LogLevel.QUIET, msg)
        }
    }
}
