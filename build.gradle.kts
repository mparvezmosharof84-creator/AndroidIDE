/*
 *  This file is part of AndroidIDE.
 *  Complete Root Build Fix by Parvez Mosharof
 */

@file:Suppress("UnstableApiUsage")

import com.itsaky.androidide.plugins.AndroidIDEPlugin
import com.itsaky.androidide.plugins.conf.configureAndroidModule
import com.itsaky.androidide.plugins.conf.configureJavaModule
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("build-logic.root-project")
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.protobuf) apply false
  alias(libs.plugins.benchmark) apply false
  id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10" apply false
}

buildscript {
  dependencies {
    classpath(libs.kotlin.gradle.plugin)
    classpath(libs.nav.safe.args.gradle.plugin)
  }
}

// সেমভার এরর চিরতরে বন্ধ করার জন্য গ্লোবাল ভার্সন নির্ধারণ
allprojects {
  version = "v2.7.0-beta"
}

subprojects {
  afterEvaluate {
    apply { plugin(AndroidIDEPlugin::class.java) }
  }

  project.version = "v2.7.0-beta"

  plugins.withId("com.android.application") {
    configureAndroidModule(libs.androidx.libDesugaring)
  }
  plugins.withId("com.android.library") {
    configureAndroidModule(libs.androidx.libDesugaring)
  }
  plugins.withId("java-library") { configureJavaModule() }

  tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
      freeCompilerArgs.addAll("-Xstring-concat=inline")
    }
  }
}

tasks.register<Delete>("clean") { delete(rootProject.layout.buildDirectory) }
