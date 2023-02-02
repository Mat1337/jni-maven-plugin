package me.mat.jni;

import me.mat.jni.util.FileUtil;
import me.mat.jni.util.OperatingSystem;
import me.mat.jni.util.ProcessStarter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class CompileNativesMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File buildDirectory;

    @Parameter(defaultValue = "${java.home}", readonly = true)
    private File javaHome;

    @Parameter(property = "source", defaultValue = "${project.basedir}/src/main/c++")
    private File source;

    @Parameter(property = "final.name", defaultValue = "${project.name}-${project.version}-native")
    private String finalName;

    @Parameter(property = "generated", defaultValue = "${project.basedir}/src/generated")
    private File generated;

    @Parameter(property = "includes")
    private File[] includes;

    @Parameter(property = "linker")
    private Linker linker;

    @Parameter(property = "flags")
    private String[] flags;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final File[] files = source.listFiles();
        if (files == null || files.length == 0)
            return;

        final List<File> sourceFiles = new ArrayList<>();
        FileUtil.findFiles(files[0], sourceFiles, ".cpp");

        final String[] flags = new String[this.flags.length + 1];
        flags[0] = "-shared";
        System.arraycopy(this.flags, 0, flags, 1, this.flags.length);

        final ProcessStarter processStarter = new ProcessStarter("g++", flags);
        processStarter.set("-o", OperatingSystem.getSystem().getDynamicLibrary(buildDirectory, finalName).getAbsolutePath());
        processStarter.add("-I" + generated.getAbsolutePath());

        if (linker != null) {
            getLog().info("Linker");

            File[] linkerDirectories = linker.directories;
            if (linkerDirectories != null && linkerDirectories.length > 0) {
                getLog().info("\tDirectories");
                Stream.of(linkerDirectories).forEach(directory -> {
                    String path = directory.getAbsolutePath();
                    processStarter.add("-L" + path);
                    getLog().info("\t\t" + path);
                });
            }

            String[] linkerLibraries = linker.libraries;
            if (linkerLibraries != null && linkerLibraries.length > 0) {
                getLog().info("\tLibraries");
                Stream.of(linkerLibraries).forEach(library -> {
                    processStarter.add("-l" + library);
                    getLog().info("\t\t" + library);
                });
            }
        }

        List<File> javaIncludeDirectories = new ArrayList<>();
        FileUtil.findDirectories(new File(javaHome.getParentFile(), "include"), javaIncludeDirectories);
        javaIncludeDirectories.forEach(file -> processStarter.add("-I" + file.getAbsolutePath()));

        if (includes != null && includes.length > 0) {
            Stream.of(includes).forEach(file -> processStarter.add("-I" + file.getAbsolutePath()));
        }

        sourceFiles.forEach(file -> processStarter.add(file.getAbsolutePath()));

        try {
            processStarter.start("Compilation failed", project.getBasedir());
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    public static final class Linker {

        @Parameter(property = "linker.directories")
        private File[] directories;

        @Parameter(property = "linker.libraries")
        private String[] libraries;

    }

}
