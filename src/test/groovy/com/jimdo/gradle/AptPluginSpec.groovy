package com.jimdo.gradle

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.api.Project
import org.gradle.api.Plugin
import spock.lang.Specification

class AptPluginSpec extends Specification {

  def "it should generate sources for java project"() {
    def projectPath = 'src/test/build-tests/java-project'
    def aptBasePath = "$projectPath/build/source/apt"

    given:
    ProjectConnection connection = GradleConnector.newConnector()
      .forProjectDirectory(new File(projectPath)).connect()

    when:
    try {
      connection.newBuild().forTasks("clean", "build").run()
    } finally {
      connection.close()
    }

    then:
    new File(aptBasePath).exists()
    new File("$aptBasePath/coffee").listFiles().length > 0
  }

  def "it should generate sources for android project"() {
    def projectPath = 'src/test/build-tests/android-project'
    def aptBasePath = "$projectPath/build/source/apt"

    given:
    ProjectConnection connection = GradleConnector.newConnector()
      .forProjectDirectory(new File(projectPath)).connect()

    when:
    try {
      connection.newBuild().forTasks("clean", "build").run()
    } finally {
      connection.close()
    }

    then:
    new File(aptBasePath).exists()
    new File("$aptBasePath/debug/com/example/dagger/simple").listFiles().length > 0
    new File("$aptBasePath/debug/com/example/dagger/simple/ui").listFiles().length > 0
    new File("$aptBasePath/release/com/example/dagger/simple").listFiles().length > 0
    new File("$aptBasePath/release/com/example/dagger/simple/ui").listFiles().length > 0
  }

  def "it should generate sources for android-library project"() {
    // TODO: does this even make sense?!
  }
}
