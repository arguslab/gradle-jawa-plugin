/*
 * Copyright (c) 2016. Fengguo Wei and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Detailed contributors are listed in the CONTRIBUTOR.md
 */

package org.argus.jawa.gradle.tasks.compile;

import org.argus.jawa.compiler.compile.CompileProgress;
import org.argus.jawa.compiler.compile.JawaCompiler;
import org.argus.jawa.core.MsgLevel;
import org.argus.jawa.core.PrintReporter;
import org.argus.jawa.gradle.tasks.compile.spec.JawaJavaJointCompileSpec;
import org.gradle.api.internal.tasks.SimpleWorkResult;
import org.gradle.api.internal.tasks.compile.CompilationFailedException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.WorkResult;
import org.gradle.util.GFileUtils;
import org.gradle.language.base.internal.compile.Compiler;

import java.io.File;
import java.io.Serializable;

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaGradleCompiler implements Compiler<JawaJavaJointCompileSpec>, Serializable {
    private static final Logger LOGGER = Logging.getLogger(JawaGradleCompiler.class);

    @Override
    public WorkResult execute(JawaJavaJointCompileSpec spec) {
        LOGGER.info("Compiling with Jawa compiler.");

        final LoggerAdapter logger = new LoggerAdapter();

        JawaCompiler compiler = new JawaCompiler();

        if (spec.getJawaCompileOptions().isForce()) {
            GFileUtils.deleteDirectory(spec.getDestinationDir());
        }

        File[] sources = spec.getSource().getFiles().toArray(new File[spec.getSource().getFiles().size()]);
        File[] outputDirs = new File[]{spec.getDestinationDir()};
        PrintReporter reporter = new PrintReporter(MsgLevel.WARNING());
        if(spec.getCompileOptions().isVerbose())
            reporter = new PrintReporter(MsgLevel.INFO());
        try {
            compiler.compile(sources, outputDirs, reporter, logger, new NoCompileProgress());
        } catch (Exception e) {
            throw new CompilationFailedException(e);
        }

        return new SimpleWorkResult(true);
    }

    private static class NoCompileProgress implements CompileProgress {

        @Override
        public void startUnit(String unitPath) {
            // This method is intentionally empty
        }

        @Override
        public boolean advance(int current, int total) {
            return true;
        }
    }


    private static class LoggerAdapter implements org.argus.jawa.compiler.log.Logger {
        @Override
        public void error(String msg) {
            LOGGER.error(msg);
        }

        @Override
        public void warn(String msg) {
            LOGGER.warn(msg);
        }

        @Override
        public void info(String msg) {
            LOGGER.info(msg);
        }

        @Override
        public void debug(String msg) {
            LOGGER.debug(msg);
        }

        @Override
        public void trace(Throwable exception) {
            LOGGER.trace(exception.getMessage());
        }
    }
}
