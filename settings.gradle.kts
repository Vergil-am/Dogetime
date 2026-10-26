pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // The WebView library's desktop engine (JCEF) depends on JOGL, which is published only here.
        maven("https://jogamp.org/deployment/maven")
    }
}

rootProject.name = "DogeTime"
include(":composeApp", ":androidApp")
