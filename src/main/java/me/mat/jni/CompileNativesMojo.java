package me.mat.jni;

import me.mat.jni.util.FileUtil;
import me.mat.jni.util.ProcessStarter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class CompileNativesMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    @Parameter(property = "java.home", defaultValue = "${java.home}")
    private File javaHome;

    @Parameter(property = "source", defaultValue = "${project.basedir}/src/main/c++")
    private File source;

    @Parameter(property = "output", defaultValue = "target/library.so")
    private File output;

    @Parameter(property = "include.directory", defaultValue = "${project.basedir}/include")
    private File includeDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final File[] files = source.listFiles();
        if (files == null || files.length == 0)
            return;

        final List<File> sourceFiles = new ArrayList<>();
        FileUtil.findFiles(files[0], sourceFiles, ".cpp");

        final ProcessStarter processStarter = new ProcessStarter("g++", "-fPIC", "-shared");
        processStarter.set("-o", output.getAbsolutePath());
        processStarter.add("-I" + includeDirectory.getAbsolutePath());

        List<File> javaIncludeDirectories = new ArrayList<>();
        FileUtil.findDirectories(new File(javaHome.getParentFile(), "include"), javaIncludeDirectories);
        javaIncludeDirectories.forEach(file -> processStarter.add("-I" + file.getAbsolutePath()));

        sourceFiles.forEach(file -> processStarter.add(file.getAbsolutePath()));
        processStarter.start(project.getBasedir());
    }

}
