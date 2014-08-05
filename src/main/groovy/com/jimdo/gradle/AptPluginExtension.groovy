package com.jimdo.gradle

class AptPluginExtension {
  String outputDirName
  AptPluginExtension(File projectBuildDir, String confName) {
    outputDirName = [projectBuildDir, 'generated-sources', confName].join(File.separator)
  }
}
