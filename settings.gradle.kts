/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

pluginManagement {
  includeBuild("composite-builds/build-logic")
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
  repositories {
    google()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
  }
}

includeBuild("composite-builds/build-deps") {
  dependencySubstitution {
    substitute(module("com.itsaky.androidide.build:appintro")).using(project(":appintro"))
    substitute(module("com.itsaky.androidide.build:desugaring-core")).using(project(":desugaring-core"))
    substitute(module("com.itsaky.androidide.build:google-java-format")).using(project(":google-java-format"))
    substitute(module("com.itsaky.androidide.build:javac")).using(project(":javac"))
    substitute(module("com.itsaky.androidide.build:javapoet")).using(project(":javapoet"))
    substitute(module("com.itsaky.androidide.build:jaxp")).using(project(":jaxp"))
    substitute(module("com.itsaky.androidide.build:layoutlib-api")).using(project(":layoutlib-api"))
    substitute(module("com.itsaky.androidide.build:logback-core")).using(project(":logback-core"))
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
