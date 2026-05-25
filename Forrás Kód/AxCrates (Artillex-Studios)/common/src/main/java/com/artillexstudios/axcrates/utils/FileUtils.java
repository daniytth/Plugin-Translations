package com.artillexstudios.axcrates.utils;

import com.artillexstudios.axcrates.AxCrates;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    public static final Path PLUGIN_DIRECTORY = AxCrates.getInstance().getDataFolder().toPath();

    public static void copyFromResource(@NotNull String path) {
        try (ZipFile zip = new ZipFile(AxCrates.getInstance().getClass().getProtectionDomain().getCodeSource().getLocation().getPath())) {
            for (Iterator<? extends ZipEntry> it = zip.entries().asIterator(); it.hasNext(); ) {
                ZipEntry entry = it.next();
                if (entry.getName().startsWith(path + "/")) {
                    if (!entry.getName().endsWith(".yml")) continue;
                    InputStream resource = AxCrates.getInstance().getResource(entry.getName());
                    if (resource == null) {
                        continue;
                    }

                    Files.copy(resource, PLUGIN_DIRECTORY.resolve(entry.getName()));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
