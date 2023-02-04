package me.mat.jni;

import me.mat.jni.util.ProcessStarter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name = "build", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class BuildMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/cmake", readonly = true)
    private File cmakeDirectory;

    @Parameter(property = "config", defaultValue = "debug")
    private String config;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            new ProcessStarter("cmake", "--build", ".", "--config", config).start(
                    "Failed to build the native library",
                    cmakeDirectory
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
