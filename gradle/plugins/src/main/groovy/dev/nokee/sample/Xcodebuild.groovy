package dev.nokee.sample

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

import javax.inject.Inject;

abstract class Xcodebuild extends DefaultTask {
    @InputDirectory
    abstract DirectoryProperty getProjectLocation()

    @Input
    abstract Property<String> getTarget()

    @Input
    abstract Property<String> getConfiguration()

    @OutputDirectory
    abstract DirectoryProperty getBuildDirectory()

    @Inject
    protected abstract ExecOperations getExecOperations()

    @TaskAction
    private void doBuild() {
        execOperations.exec {
            commandLine 'xcodebuild', '-project', projectLocation.asFile.get().name, '-target', target.get(), '-configuration', configuration.get()
            workingDir projectLocation.asFile.get().parentFile
        }
    }
}
