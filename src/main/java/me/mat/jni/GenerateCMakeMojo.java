package me.mat.jni;

import me.mat.jni.util.ProcessStarter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name = "generate-cmake")
public class GenerateCMakeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File base;

    @Parameter(defaultValue = "${project.build.directory}/cmake", readonly = true)
    private File cmakeDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (!cmakeDirectory.exists()) {
                if (!cmakeDirectory.mkdirs()) {
                    throw new MojoExecutionException("Failed to create the CMake directory");
                }
            }
            new ProcessStarter("cmake", base.getAbsolutePath()).start(
                    "Failed to generate CMake project files",
                    cmakeDirectory
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
