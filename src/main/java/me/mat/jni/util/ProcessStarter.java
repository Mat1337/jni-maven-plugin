package me.mat.jni.util;

import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessStarter {

    private final List<String> arguments = new ArrayList<>();

    public ProcessStarter(String process, String... flags) {
        arguments.add(process);
        arguments.addAll(Arrays.asList(flags));
    }

    public void set(String flag, String value) {
        arguments.add(flag);
        arguments.add(value);
    }

    public void add(String argument) {
        arguments.add(argument);
    }

    public void start(String errorMessage, File baseDirectory) throws IOException, InterruptedException, MojoExecutionException {
        final ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        final Process process = processBuilder.directory(baseDirectory).inheritIO().start();
        process.waitFor();

        if (process.exitValue() != 0) {
            throw new MojoExecutionException(errorMessage);
        }
    }

}
