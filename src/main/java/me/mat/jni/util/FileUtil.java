package me.mat.jni.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {

    /**
     * Searches through the {@link File} for files
     * that have a matching suffix that is provided
     *
     * @param file   {@link File} that you want to search through
     * @param files  {@link List} where you want to store all those files
     * @param suffix {@link String} suffix that you want to match against
     */

    public static void findFiles(File file, List<File> files, String suffix) {
        if (!file.isDirectory()) {
            if (!file.getPath().endsWith(suffix))
                return;

            files.add(file);
            return;
        }

        File[] entries = file.listFiles();
        if (entries == null || entries.length == 0)
            return;

        Stream.of(entries).forEach(f -> findFiles(f, files, suffix));
    }

    /**
     * Searches through the provided {@link File}
     * for directories and adds them to the provided {@link List}
     *
     * @param file        {@link File} that you want to search through
     * @param directories {@link List} where you want to store all those directories
     */

    public static void findDirectories(File file, List<File> directories) {
        if (!file.isDirectory())
            return;

        directories.add(file);

        File[] entries = file.listFiles();
        if (entries == null || entries.length == 0)
            return;

        Stream.of(entries).forEach(f -> findDirectories(f, directories));
    }

    /**
     * Deletes everything from the provided {@link File}
     *
     * @param directory {@link File} that you want to delete everything from
     * @throws FileNotFoundException occurs when deleting a {@link File} fails
     */

    public static void deleteDirectory(File directory) throws FileNotFoundException {
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            if (!directory.delete()) {
                throw new FileNotFoundException("Failed to delete the directory");
            }
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file);
                continue;
            }
            if (!file.delete()) {
                throw new FileNotFoundException("Failed to delete the file");
            }
        }

        if (!directory.delete()) {
            throw new FileNotFoundException("Failed to delete the directory");
        }
    }

}
