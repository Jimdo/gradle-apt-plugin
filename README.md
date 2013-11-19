Gradle Apt Plugin
=================

A Gradle plugin for the Java annotation processor tool.

Usage
-------------------

Add the plugin to your `buildscript`'s `dependencies` and add Sonatype's snapshot repository to be able to download it:

```groovy
buildscript {
  dependencies {
      repositories {
        maven {
          url "https://oss.sonatype.org/content/repositories/snapshots/"
        }
        ...
      }
      
      classpath 'com.jimdo.gradle:gradle-apt-plugin:0.2-SNAPSHOT'
      ...
  }
}
```

Apply the `apt` plugin:

`apply plugin: 'apt'`

Add annotation processors dependencies using the `apt` configuration, i.e.:

`apt 'com.squareup.dagger:dagger-compiler:1.1.0'`

Run `gradle build` and find the generated files in the `build/sources/apt/` directory (for now not configurable, it will be in the future).

Credits
-------

This plugin is a slightly modified/cleaned-up version of [this Stackoverflow answer](http://stackoverflow.com/questions/16683944/androidannotations-nothing-generated-empty-activity)
