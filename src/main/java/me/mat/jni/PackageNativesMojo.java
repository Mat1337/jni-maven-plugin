package me.mat.jni;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import me.mat.jni.util.OperatingSystem;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class PackageNativesMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File buildDirectory;

    @Parameter(property = "final.name", defaultValue = "${project.name}-${project.version}-native")
    private String finalName;

    @Parameter(property = "jar.path", defaultValue = "natives")
    private String jarPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Build build = project.getBuild();

        final File jarFile = new File(build.getDirectory(), build.getFinalName() + ".jar");
        if (!jarFile.exists()) {
            throw new MojoExecutionException("Failed to locate the output jar file");
        }

        final List<JarElement> elements = new ArrayList<>();
        readJar(jarFile, elements);

        final File dynamicLibraryFile = OperatingSystem.getSystem().getDynamicLibrary(buildDirectory, finalName);

        String outputPath = dynamicLibraryFile.getAbsolutePath();
        outputPath = outputPath.substring(outputPath.lastIndexOf(File.separator));

        try {
            final JarEntry entry = new JarEntry(jarPath + outputPath);
            final byte[] bytes = read(Files.newInputStream(dynamicLibraryFile.toPath()));
            if (bytes.length == 0)
                throw new StreamCorruptedException(entry.getName() + " has failed to read");

            elements.add(new JarElement(
                    entry,
                    bytes
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(jarFile.toPath()))) {
            for (JarElement element : elements) {
                try {
                    jarOutputStream.putNextEntry(element.entry);
                    jarOutputStream.write(element.bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads all the contents of the jar file
     * from the provided {@link File} and appends
     * it into the provided {@link List} of {@link JarElement}
     *
     * @param file     {@link File} that you want to read
     * @param elements {@link List} that will hold all the {@link JarElement}
     */

    private static void readJar(File file, List<JarElement> elements) {
        try (JarFile jarFile = new JarFile(file)) {
            final Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                final JarEntry entry = enumeration.nextElement();
                if (entry.isDirectory())
                    continue;

                final InputStream inputStream = jarFile.getInputStream(entry);
                if (inputStream == null)
                    throw new StreamCorruptedException(entry.getName() + " does not have a valid InputStream");

                final byte[] bytes = read(inputStream);
                if (bytes.length == 0)
                    throw new StreamCorruptedException(entry.getName() + " has failed to read");

                elements.add(new JarElement(
                        entry,
                        bytes
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a {@link Byte}[] from an input stream
     *
     * @param inputStream {@link InputStream} that you want to read from
     * @return Array of {@link Byte} that are read from the {@link InputStream}
     * @throws IOException occurs when something goes wrong with reading the {@link InputStream}
     */

    private static byte[] read(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[0x1000];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    @AllArgsConstructor
    private static class JarElement {

        @NonNull
        private final JarEntry entry;

        private final byte[] bytes;

    }

}
