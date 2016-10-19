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

import com.google.common.collect.ImmutableList
import org.gradle.api.Incubating
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.compile.AbstractOptions

/**
 * @author <a href="mailto:fgwei521@gmail.com">Fengguo Wei</a>
 */
public class JawaCompileOptions extends AbstractOptions {
    private boolean force

    private boolean failOnError = true

    private boolean verbose

    private boolean listFiles

    private boolean fork = true

    private List<String> fileExtensions = ImmutableList.of("java", "jawa")

    private JawaForkOptions forkOptions = new JawaForkOptions()

    /**
     * Whether to force the compilation of all files.
     * Legal values:
     * - false (only compile modified files)
     * - true (always recompile all files)
     */
    public boolean isForce() {
        force
    }

    public void setForce(boolean force) {
        this.force = force
    }

    /**
     * Tells whether the compilation task should fail if compile errors occurred. Defaults to {@code true}.
     */
    public boolean isFailOnError() {
        return failOnError
    }

    /**
     * Sets whether the compilation task should fail if compile errors occurred. Defaults to {@code true}.
     */
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError
    }

    /**
     * Tells whether to turn on verbose output. Defaults to {@code false}.
     */
    public boolean isVerbose() {
        return verbose
    }

    /**
     * Sets whether to turn on verbose output. Defaults to {@code false}.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose
    }

    /**
     * Tells whether to print which source files are to be compiled. Defaults to {@code false}.
     */
    public boolean isListFiles() {
        return listFiles
    }

    /**
     * Sets whether to print which source files are to be compiled. Defaults to {@code false}.
     */
    public void setListFiles(boolean listFiles) {
        this.listFiles = listFiles
    }

    /**
     * Tells whether to run the Jawa compiler in a separate process. Defaults to {@code true}.
     */
    public boolean isFork() {
        return fork
    }

    /**
     * Sets whether to run the Jawa compiler in a separate process. Defaults to {@code true}.
     */
    public void setFork(boolean fork) {
        this.fork = fork
    }

    /**
     * Returns the list of acceptable source file extensions.
     */
    @Input
    @Incubating
    public List<String> getFileExtensions() {
        return fileExtensions
    }

    /**
     * Sets the list of acceptable source file extensions.
     */
    @Incubating
    public void setFileExtensions(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions
    }

    /**
     * Returns options for running the Ja compiler in a separate process. These options only take effect
     * if {@code fork} is set to {@code true}.
     */
    @Nested
    public JawaForkOptions getForkOptions() {
        return forkOptions
    }

    /**
     * Sets options for running the Groovy compiler in a separate process. These options only take effect
     * if {@code fork} is set to {@code true}.
     */
    public void setForkOptions(JawaForkOptions forkOptions) {
        this.forkOptions = forkOptions
    }

    /**
     * Convenience method to set {@link JawaForkOptions} with named parameter syntax.
     * Calling this method will set {@code fork} to {@code true}.
     */
    public JawaCompileOptions fork(Map<String, Object> forkArgs) {
        fork = true
        forkOptions.define(forkArgs)
        return this
    }

    /**
     * Internal method.
     */
    @Override
    public Map<String, Object> optionMap() {
        def map = super.optionMap()
        map.putAll(forkOptions.optionMap())
        return map
    }
}
