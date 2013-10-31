package com.jimdo.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import org.gradle.api.Plugin
import spock.lang.Specification

class AptPluginSpec extends Specification {

  def "it should throw an illegal argument exception if project isn't java or android"() {
    given:
    def project = ProjectBuilder.builder().build()

    when:
    project.apply plugin: 'apt'

    then:
    thrown(IllegalArgumentException)
  }
}