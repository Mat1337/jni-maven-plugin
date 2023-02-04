package me.mat.jni;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;

@Mojo(name = "setup")
public class SetupProjectMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}")
    private File base;

    @Parameter(property = "source", defaultValue = "${project.basedir}/src/main/c++")
    private File source;

    @Parameter(property = "generated", defaultValue = "${project.basedir}/src/generated")
    private File generated;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File buildDirectory;

    @Parameter(defaultValue = "${project.version}")
    private String projectVersion;

    @Parameter(property = "native.project.name", defaultValue = "${project.artifactId}")
    private String projectName;

    @Parameter(property = "cmake.version", defaultValue = "3.24")
    private String cmakeVersion;

    @Parameter(property = "native.project.version", defaultValue = "17")
    private String languageVersion;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try (PrintWriter printWriter = new PrintWriter(new PrintStream("CMakeLists.txt"))) {
            printWriter.print(readCMakeLists());
        } catch (FileNotFoundException | StreamCorruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String readCMakeLists() throws StreamCorruptedException {
        try (final InputStream inputStream = SetupProjectMojo.class.getResourceAsStream("/CMakeLists.txt")) {
            if (inputStream == null)
                throw new StreamCorruptedException("Failed to read the InputStream");

            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.replaceAll("%CMAKE_MINIMUM_VERSION%", cmakeVersion);
                    line = line.replaceAll("%PROJECT_NAME%", projectName);
                    line = line.replaceAll("%LANGUAGE_VERSION%", languageVersion);
                    line = line.replaceAll("%SOURCE_DIRECTORY%", getRelativeSourcePath(base, source) + "/**.cpp");
                    line = line.replaceAll("%INCLUDE_DIRECTORY%", getRelativeSourcePath(base, generated));
                    line = line.replaceAll("%BUILD_DIRECTORY%", getRelativeSourcePath(base, buildDirectory));
                    line = line.replaceAll("%VERSION%", projectVersion);

                    builder.append(line).append("\n");
                }
            }
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getRelativeSourcePath(File directory, File subDirectory) {
        return subDirectory.getPath().substring(directory.getAbsolutePath().length() + 1).replaceAll("\\\\", "/");
    }

}
