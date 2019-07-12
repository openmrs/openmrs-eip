package org.openmrs.sync.core.utils;

import org.openmrs.sync.core.exception.OpenMrsSyncException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class FileUtils {

    private static final String PRIVATE_KEY_SUFFIX = "-sec.asc";
    private static final String PUBLIC_KEY_SUFFIX = "-pub.asc";

    private FileUtils() {}

    public static byte[] getPrivateKeysFromFolder(final String folderPath) throws IOException {
        List<Path> paths = listKeysFromFolder(folderPath, PRIVATE_KEY_SUFFIX);

        if (paths.size() > 1) {
            throw new OpenMrsSyncException("There should be only one private key. " + paths.size() + " found");
        }

        return paths.stream().findFirst()
                .map(FileUtils::extractKeyFromFile)
                .orElseThrow(() -> new OpenMrsSyncException("No private key found"));
    }

    public static List<byte[]> getPublicKeysFromFolder(final String folderPath) throws IOException {
        return listKeysFromFolder(folderPath, PUBLIC_KEY_SUFFIX).stream()
                .map(FileUtils::extractKeyFromFile)
                .collect(Collectors.toList());
    }

    private static byte[] extractKeyFromFile(final Path path) {
        try {
            InputStream is = new FileInputStream(path.toFile());
            StringBuilder sb;
            try (BufferedReader buf = new BufferedReader(new InputStreamReader(is))) {
                String line = buf.readLine();
                sb = new StringBuilder();

                while (line != null) {
                    sb.append(line).append("\n");
                    line = buf.readLine();
                }
            }

            return sb.toString().getBytes(UTF_8);
        } catch (IOException e) {
            throw new OpenMrsSyncException("Impossible to read file", e);
        }
    }

    private static List<Path> listKeysFromFolder(final String folderPath,
                                                 final String suffix) throws IOException {
        try(Stream<Path> paths =
                    Files.find(new File(System.getProperty("user.dir") + folderPath).toPath(), 1,
                            (path, attr) -> attr.isRegularFile() && path.getFileName().toString().endsWith(suffix))
        ) {
            return paths.collect(Collectors.toList());
        }
    }
}
