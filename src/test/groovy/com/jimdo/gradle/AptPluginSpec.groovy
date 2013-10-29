package com.jimdo.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import spock.lang.Specification

class AptPluginSpec extends Specification {

  def project = Mock(Project)
  def configContainer = Mock(ConfigurationContainer)
  def extensionContainer = Mock(ExtensionContainer)
  def pluginContainer = Mock(PluginContainer)

  def "it should throw an illegal argument exception if project isn't java or android"() {
    when:
    new AptPlugin().apply(project)

    then:
    _ * project.configurations >> configContainer
    _ * project.extensions >> extensionContainer
    _ * project.plugins >> pluginContainer
    thrown(IllegalArgumentException)
  }
}