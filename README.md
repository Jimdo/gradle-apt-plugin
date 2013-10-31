Gradle Apt Plugin
=================

A Gradle plugin for the Java annotation processor tool.

Usage (in progress)
-------------------

The plugin is in early development stage and it's still not available in Maven Central. 
To use it you have to build it locally first using `gradle build publishToMavenLocal`.
After you built it, add the plugin to your `buildscript`'s `dependencies` closure:

```groovy
dependencies {
    classpath 'com.jimdo.gradle:gradle-apt-plugin:0.1-SNAPSHOT'
    ...
}
```

and add your local Maven repository in the `repositories` closure:

```groovy
repositories {
    mavenLocal()
    ...
}
```

Apply the `apt` plugin AFTER the `java`, `android` or `android-library` plugin:

`apply plugin: 'apt'`

Add annotation processors dependencies using the `apt` configuration, i.e.:

`apt 'com.squareup.dagger:dagger-compiler:1.1.0'`

Run `gradle build` and find the generated files in the `build/sources/apt/` directory (for now not configurable, it will be in the future).

Credits
-------

This plugin is a slightly modified/cleaned-up version of [this Stackoverflow answer](http://stackoverflow.com/questions/16683944/androidannotations-nothing-generated-empty-activity)
