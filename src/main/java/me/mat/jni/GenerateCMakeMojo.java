package me.mat.jni;

import me.mat.jni.util.ProcessStarter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mojo(name = "generate-cmake")
public class GenerateCMakeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File base;

    @Parameter(defaultValue = "${project.basedir}/cmake", readonly = true)
    private File cmakeDirectory;

    @Parameter(property = "flags")
    private String[] flags;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (!cmakeDirectory.exists()) {
                if (!cmakeDirectory.mkdirs()) {
                    throw new MojoExecutionException("Failed to create the CMake directory");
                }
            }

            final List<String> args = new ArrayList<>(Arrays.asList(flags));
            args.add(base.getAbsolutePath());

            new ProcessStarter("cmake", args.toArray(new String[0])).start(
                    "Failed to generate CMake project files",
                    cmakeDirectory
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
