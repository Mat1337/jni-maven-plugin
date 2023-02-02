package me.mat.jni;

import me.mat.jni.util.FileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN)
public class CleanMojo extends AbstractMojo {

    @Parameter(property = "generated", defaultValue = "${project.basedir}/src/generated")
    private File generated;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (!generated.exists())
                return;
            FileUtil.deleteDirectory(generated);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

}
