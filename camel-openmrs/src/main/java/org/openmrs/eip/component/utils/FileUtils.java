package org.openmrs.eip.component.utils;

import org.openmrs.eip.component.exception.EIPException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class FileUtils {

    private static final String PRIVATE_KEY_SUFFIX = "-sec.asc";
    private static final String PUBLIC_KEY_SUFFIX = "-pub.asc";

    private FileUtils() {}

    /**
     * Get the private key from the folder with the path in parameter
     * returns an exception if the number of private keys is diferrent than 1
     *
     * @param folderPath the path to the folder
     * @return a file as byte array
     * @throws IOException
     */
    public static byte[] getPrivateKeysFromFolder(final String folderPath) throws IOException {
        List<Path> paths = listKeysFromFolder(folderPath, PRIVATE_KEY_SUFFIX);

        if (paths.size() > 1) {
            throw new EIPException("There should be only one private key. " + paths.size() + " found");
        }

        return paths.stream().findFirst()
                .map(FileUtils::extractKeyFromFile)
                .orElseThrow(() -> new EIPException("No private key found"));
    }

    /**
     * Get all the public keys from the folder with the path in parameter
     *
     * @param folderPath the path to the folder
     * @return a list of Files as byte arrays
     * @throws IOException
     */
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
            throw new EIPException("Impossible to read file", e);
        }
    }

    private static List<Path> listKeysFromFolder(final String folderPath,
                                                 final String suffix) throws IOException {
        String path = System.getProperty("user.dir") + folderPath;
        try (Stream<Path> paths =
                     Files.find(new File(path).toPath(), 1,
                             (p, attr) -> attr.isRegularFile() && p.getFileName().toString().endsWith(suffix))
        ) {
            return paths.collect(Collectors.toList());
        }
    }
}
