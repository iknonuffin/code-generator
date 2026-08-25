package org.codegen.zip;

import org.codegen.exception.GenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {
    private final static Logger log = LoggerFactory.getLogger(ZipExtractor.class);

    public void extract(Path zipFile, Path destination) {
        Path target = destination.toAbsolutePath().normalize();

        log.debug("Extracting '{}' into '{}'", zipFile, destination);

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            Files.createDirectories(target);
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                Path output = target
                        .resolve(entry.getName())
                        .normalize();

                if (!output.startsWith(target)) {
                    throw new IOException("Entry is outside of the target dir: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(output);
                } else {
                    Files.createDirectories(output.getParent());

                    Files.copy(zipInputStream, output, StandardCopyOption.REPLACE_EXISTING);
                }

                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new GenerationException(
                    "Failed to extract '" + zipFile
                            + "' to '" + destination + "'",
                    e
            );
        }
    }
}
