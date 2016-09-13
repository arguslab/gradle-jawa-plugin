# Gradle support for Jawa
[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0) 
[![Build Status](https://travis-ci.org/arguslab/gradle-jawa-plugin.svg?branch=master)](https://travis-ci.org/arguslab/gradle-jawa-plugin)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/50865cacec8f43289a071cf06424dd2d)](https://www.codacy.com/app/fgwei521/gradle-jawa-plugin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=arguslab/gradle-jawa-plugin&amp;utm_campaign=Badge_Grade)

A Gradle plugin, similar to the groovy and scala plugins for Gradle.

Specifically, this adds compileJawa and compileTestJawa tasks. compileJava and compileTestJava tasks are dependent on these tasks in the mixin case, respectively.

Java 8 is required.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Installation](#installation)
  - [1. Add buildscript's dependency](#1-add-buildscripts-dependency)
  - [2. Apply plugin](#2-apply-plugin)
  - [3. Put Jawa source files](#3-put-jawa-source-files)
- [Changelog](#changelog)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Installation

### 1. Add buildscript's dependency

`build.gradle`
```groovy
buildscript {
    dependencies {
        classpath "com.github.arguslab:gradle-jawa-plugin:1.0.1"
    }
}
```

### 2. Apply plugin

`build.gradle`
```groovy
apply plugin: "org.argus.jawa"
```

### 3. Put jawa source files

Default locations are src/main/jawa.
You can customize those directories similar to java.

`build.gradle`
```groovy
android {
    sourceSets {
        main {
            jawa {
                srcDir "path/to/main/jawa" // default: "src/main/jawa"
            }
        }
    }
}
```

## Changelog
- 1.0 First release
