package dev.nokee.sample

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

import javax.inject.Inject

abstract class SwiftPMGenerateXcodeProject extends DefaultTask {
    @InputFile
    abstract RegularFileProperty getSwiftPmBuildFile()

    @OutputDirectory
    abstract DirectoryProperty getProjectLocation()

    @Inject
    protected abstract ExecOperations getExecOperations()

    @TaskAction
    private void build() {
        getExecOperations().exec {
            commandLine 'swift', 'package', 'generate-xcodeproj'
            workingDir swiftPmBuildFile.asFile.get().parentFile
        }
    }
}
