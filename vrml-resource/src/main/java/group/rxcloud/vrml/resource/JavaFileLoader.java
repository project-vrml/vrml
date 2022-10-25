package group.rxcloud.vrml.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * The Java file loader.
 */
final class JavaFileLoader {

    /**
     * Load java resources file.
     *
     * @param fileName the file name with path
     * @return the file steam
     * @throws NullPointerException     the null pointer exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    static InputStreamReader loadJavaResources(String fileName) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(fileName, "fileName is null.");
        InputStream in = JavaFileLoader.class.getResourceAsStream(fileName);
        return new InputStreamReader(in, StandardCharsets.UTF_8);
    }

    /**
     * Load system file.
     *
     * @param fileName the file name with path
     * @return the file steam
     * @throws NullPointerException     the null pointer exception
     * @throws IllegalArgumentException the illegal argument exception
     */
    static InputStreamReader loadSystemFile(String fileName) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(fileName, "fileName is null.");
        try {
            File file = new File(fileName);
            if (file.exists() && file.canRead()) {
                FileInputStream fis = new FileInputStream(file);
                return new InputStreamReader(fis, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(fileName + " file not found.", e);
        }
        throw new IllegalArgumentException(fileName + " file not found.");
    }
}
