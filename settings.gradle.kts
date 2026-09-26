/*
 *  This file is part of AndroidIDE.
 *  Official Composite Build & Sora Editor Fix by Parvez Mosharof
 */

pluginManagement {
  includeBuild("composite-builds/build-logic")
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://repo.rosemoe.com/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
  }
}

dependencyResolutionManagement {
  val dependencySubstitutions = mapOf(
    "build-deps" to arrayOf(
      "appintro",
      "fuzzysearch",
      "google-java-format",
      "java-compiler",
      "javac",
      "javapoet",
      "jaxp",
      "jdk-compiler",
      "jdk-jdeps",
      "jdt",
      "layoutlib-api",
      "logback-core"
    ),
    "build-deps-common" to arrayOf(
      "desugaring-core"
    )
  )

  for ((build, modules) in dependencySubstitutions) {
    includeBuild("composite-builds/${build}") {
      this.name = build
      dependencySubstitution {
        for (module in modules) {
          substitute(module("com.itsaky.androidide.build:${module}"))
            .using(project(":${module}"))
        }
      }
    }
  }

  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  repositories {
    google()
    mavenCentral()
    maven("https://repo.rosemoe.com/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
  }
}

rootProject.name = "AndroidIDE"

include(
  ":annotation:annotations",
  ":annotation:processors",
  ":annotation:processors-ksp",
  ":core:actions",
  ":core:app",
  ":core:common",
  ":core:indexing-api",
  ":core:indexing-core",
  ":core:lsp-api",
  ":core:lsp-models",
  ":core:projectdata",
  ":core:projects",
  ":core:resources",
  ":editor:api",
  ":editor:impl",
  ":editor:lexers",
  ":editor:treesitter",
  ":event:eventbus",
  ":event:eventbus-android",
  ":event:eventbus-events",
  ":external:acsprovider",
  ":external:atc",
  ":external:logwire",
  ":java:javac-services",
  ":java:lsp",
  ":java:lsp-setup",
  ":logging:idestats",
  ":logging:logger",
  ":logging:logsender",
  ":termux:application",
  ":termux:emulator",
  ":termux:shared",
  ":termux:view",
  ":testing:android-test",
  ":testing:common-test",
  ":testing:gradle-tooling-test",
  ":testing:lspTest",
  ":testing:unitTest",
  ":tooling:api",
  ":tooling:builder-model-impl",
  ":tooling:events",
  ":tooling:impl",
  ":tooling:model",
  ":tooling:plugin-config",
  ":utilities:build-info",
  ":utilities:flashbar",
  ":utilities:framework-stubs",
  ":utilities:lookup",
  ":utilities:preferences",
  ":utilities:shared",
  ":utilities:templates-api",
  ":utilities:templates-impl",
  ":utilities:treeview",
  ":utilities:uidesigner",
  ":utilities:xml-inflater",
  ":xml:aaptcompiler",
  ":xml:dom",
  ":xml:lsp",
  ":xml:resources-api",
  ":xml:utils"
)
