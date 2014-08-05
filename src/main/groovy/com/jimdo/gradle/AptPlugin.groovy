package com.jimdo.gradle

import org.apache.commons.lang.StringUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.tooling.BuildException

class AptPlugin implements Plugin<Project> {

  @Override void apply(Project project) {
    if (isJavaProject(project)) {
      applyToJavaProject(project)
    } else {
      project.afterEvaluate {
        if (isAndroidProject(project)) {
          applyToAndroidProject(project)
        } else {
          throw new BuildException('The project isn\'t a java or android project', null)
        }
      }
    }
  }

  def applyToJavaProject(Project project) {
    def tasks = []
    project.tasks.each { Task t ->
      if (t instanceof JavaCompile) {
        tasks.add t
      }
    }
    tasks.each { Task t ->
      JavaCompile compileTask = (JavaCompile) t
      String taskName = compileTask.name

      String confName
      def pattern = taskName =~ '^compile(.*)Java$'
      if (pattern) {
        String taskNamePart = pattern.group(1)
        if (taskNamePart.isEmpty()) {
          confName = 'apt'
        } else {
          confName = StringUtils.uncapitalize(taskNamePart) + 'Apt'
        }
      } else {
        confName = taskName + 'Apt'
      }

      String confTaskName = 'configureAptFor' + StringUtils.capitalize(taskName)

      project.logger.info("Task $taskName has APT configuration $confName applied by task $confTaskName")

      project.configurations.create(confName)
      project.extensions.create(confName, AptPluginExtension, project.buildDir, confName)

      project.afterEvaluate {
        Configuration conf = project.configurations.getByName(confName)
        String dir = project.extensions.getByName(confName).outputDirName
        Task confTask = project.task(confTaskName)
        confTask.dependsOn conf
        confTask.doFirst {
          compileTask.configure {
            options.compilerArgs.addAll(['-processorpath', conf.asPath])
            options.compilerArgs.addAll(['-s', dir])
            source = source.filter { !it.path.startsWith(dir) }
            doFirst { new File(dir).mkdirs() }
          }
        }
        compileTask.dependsOn confTask
      }
    }
  }

  def applyToAndroidProject(project) {
    project.configurations.create 'apt'
    project.extensions.create 'apt', AptPluginExtension
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

  private static File getAptOutputDir(project) {
    return project.file(project.apt.outputDirName)
  }
}
