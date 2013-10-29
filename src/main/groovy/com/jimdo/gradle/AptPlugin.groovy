package com.jimdo.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin

class AptPlugin implements Plugin<Project> {

  void apply(Project project) {
    project.configurations.create 'apt'
    project.extensions.create 'apt', AptPluginExtension
    
    if (isAndroidProject(project)) {
      applyToAndroidProject(project)
    } else if (isJavaProject(project)) {
      // TODO
    } else {
      throw new IllegalArgumentException('The project misses the java, android or android-library plugin')
    }
  }

  def applyToAndroidProject(androidProject) {
    def androidExtension = androidProject.plugins.hasPlugin('android')? 
      androidProject.plugins.getPlugin('android').extension : 
      androidProject.plugins.getPlugin('android-library').extension
    androidExtension.applicationVariants.all {
      File aptOutputDir
      if (!androidProject.apt.outputDirName) {
        aptOutputDir = androidProject.file 'build/source/apt'
      } else {
        aptOutputDir = androidProject.file outputDirName
      }
      File variantAptOutputDir = androidProject.file("$aptOutputDir/$variant.dirName")  
      
      androidExtension.sourceSets[sourceSetName(variant)].java.srcDirs.addAll variantAptOutputDir.path

      javaCompile.options.compilerArgs.addAll '-processorpath',
        androidProject.configurations.apt.asPath, '-s', variantAptOutputDir.path

      javaCompile.source = javaCompile.source.filter {
        !path.startsWith(aptOutputDir.path)
      }

      def variant = it
      javaCompile.doFirst {
        logger.info "Generating sources using the annotation processing tool:"
        logger.info "  Variant: ${variant.name}"
        logger.info "  Output directory: ${variantAptOutputDir}"
        
        variantAptOutputDir.mkdirs()
      }
    }
  }

  def isAndroidProject(project) {
    project.plugins.hasPlugin('android') || project.plugins.hasPlugin('android-library')
  }

  def isJavaProject(project) {
    project.plugins.hasPlugin('java')
  }

  def sourceSetName(variant) {
    variant.dirName.split('/').last()
  }
}
