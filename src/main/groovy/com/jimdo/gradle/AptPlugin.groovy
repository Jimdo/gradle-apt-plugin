package com.jimdo.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

import org.gradle.tooling.BuildException

class AptPlugin implements Plugin<Project> {

  private static final String[] GRADLE_ANDROID_PLUGIN_SUPPORTED_VERSIONS = [ '0.7.0', '0.7.1', '0.7.2', '0.7.3', 
                                                                             '0.8.0', '0.8.1', '0.8.2', '0.8.3' ]

  @Override void apply(Project project) {
    project.configurations.create 'apt'
    project.extensions.create 'apt', AptPluginExtension

    project.afterEvaluate {
      if (isJavaProject(project)) {
        applyToJavaProject(project)
      } else if (isAndroidProject(project)) {
        applyToAndroidProject(project)
      } else  {
          throw new BuildException('The project isn\'t a java or android project', null)
      }
    }
  }

  def applyToJavaProject(project) {
    File aptOutputDir = getAptOutputDir(project)
    project.task('addAptCompilerArgs') << {
      project.compileJava.options.compilerArgs.addAll '-processorpath',
      project.configurations.apt.asPath, '-s', aptOutputDir.path

      project.compileJava.source = project.compileJava.source.filter {
        !it.path.startsWith(aptOutputDir.path)
      }

      project.compileJava.doFirst {
        logger.info "Generating sources using the annotation processing tool:"
        logger.info "  Output directory: ${aptOutputDir}"

        aptOutputDir.mkdirs()
      }
    }
    project.tasks.getByName('compileJava').dependsOn 'addAptCompilerArgs'
  }

  def applyToAndroidProject(project) {
    checkGradleAndroidPlugin(project)

    def androidExtension
    def variants

    if (project.plugins.hasPlugin('android')) {
      androidExtension = project.plugins.getPlugin('android').extension
      variants = androidExtension.applicationVariants
    } else if (project.plugins.hasPlugin('android-library')) {
      androidExtension = project.plugins.getPlugin('android-library').extension
      variants = androidExtension.libraryVariants
    }
    
    variants.all { variant ->
      File aptOutputDir = getAptOutputDir(project)
      File variantAptOutputDir = project.file("${aptOutputDir}/${dirName}")

      androidExtension.sourceSets[sourceSetName(variant)].java.srcDirs.addAll variantAptOutputDir.path

      javaCompile.options.compilerArgs.addAll '-processorpath',
        project.configurations.apt.asPath, '-s', variantAptOutputDir.path

      javaCompile.source = javaCompile.source.filter {
        !variant.variantData.extraGeneratedSourceFolders.each { folder ->
          folder.path.startsWith(aptOutputDir.path)
        }
      }

      javaCompile.doFirst {
        logger.info "Generating sources using the annotation processing tool:"
        logger.info "  Variant: ${variant.name}"
        logger.info "  Output directory: ${variantAptOutputDir}"

        variantAptOutputDir.mkdirs()
      }
    }
  }

  def isAndroidProject(project) {
    hasAndroidPlugin(project) || hasAndroidLibraryPlugin(project)
  }

  def hasAndroidPlugin(project) {
    project.plugins.hasPlugin('android')
  }

  def hasAndroidLibraryPlugin(project) {
    project.plugins.hasPlugin('android-library')
  }

  def isJavaProject(project) {
    project.plugins.hasPlugin('java')
  }

  def sourceSetName(variant) {
    variant.dirName.split('/').last()
  }

  def getAptOutputDir(project) {
    def aptOutputDirName = project.apt.outputDirName
    if (!aptOutputDirName) {
      aptOutputDirName = 'build/source/apt'
    }
    project.file aptOutputDirName
  }

  def checkGradleAndroidPlugin(project) {
    // as in: http://stackoverflow.com/a/18119304/389262
    def gradleAndroidPluginVersion = project.buildscript.configurations.classpath.resolvedConfiguration.firstLevelModuleDependencies.find { plugin ->
      plugin.moduleGroup == 'com.android.tools.build'
    }.moduleVersion
    if (!(gradleAndroidPluginVersion in GRADLE_ANDROID_PLUGIN_SUPPORTED_VERSIONS)) {
      throw new BuildException("Android Gradle plugin version for the current project is not supported [" + gradleAndroidPluginVersion + "]. Supported versions are: " 
        + GRADLE_ANDROID_PLUGIN_SUPPORTED_VERSIONS.join(' ').trim(), null);
    }
  }
}
