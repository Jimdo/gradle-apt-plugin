# Not actively maintained

This project is not actively maintained anymore, thanks to everyone who used it/gave feedback/submitted PR. 

If you're using it in an Android project, have a look at [this plugin](https://bitbucket.org/hvisser/android-apt) instead. If you're using it in a java project, changes are that it's working but it won't be updated anymore. Feel free to fork it and add more features to it!

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
      
      classpath 'com.jimdo.gradle:gradle-apt-plugin:{latest-version}'
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
