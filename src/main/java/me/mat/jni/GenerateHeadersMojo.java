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
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "generate-headers", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class GenerateHeadersMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    @Parameter(property = "generated", defaultValue = "${project.basedir}/src/generated")
    private File generated;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final File outputDirectory = new File(project.getBuild().getOutputDirectory());
        if (!outputDirectory.exists()) {
            throw new MojoExecutionException("Class output directory does not exist");
        }

        final File[] files = outputDirectory.listFiles();
        if (files == null || files.length == 0)
            return;

        final List<File> classFiles = new ArrayList<>();
        FileUtil.findFiles(files[0], classFiles, ".class");

        final List<String> classNames = new ArrayList<>();
        for (File classFile : classFiles) {
            try (FileInputStream inputStream = new FileInputStream(classFile)) {
                final ClassReader classReader = new ClassReader(inputStream);

                final ClassNode classNode = new ClassNode();
                classReader.accept(classNode, ClassReader.SKIP_DEBUG);
                if (!hasNativeMethods(classNode))
                    continue;

                classNames.add(classNode.name.replaceAll("/", "."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        getLog().info("Classes");
        classNames.forEach(className -> getLog().info("\t" + className));

        final ProcessStarter processStarter = new ProcessStarter("javah", "-force");
        processStarter.set("-d", generated.getPath());
        processStarter.set("-classpath", outputDirectory.getAbsolutePath());
        classNames.forEach(processStarter::add);

        try {
            processStarter.start("Generation failed", project.getBasedir());
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    /**
     * Checks if the provided {@link ClassNode}
     * has any native methods
     *
     * @param classNode {@link ClassNode} that you want to check for natives
     * @return {@link Boolean}
     */

    private static boolean hasNativeMethods(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            if (Modifier.isNative(method.access))
                return true;
        }
        return false;
    }

}
