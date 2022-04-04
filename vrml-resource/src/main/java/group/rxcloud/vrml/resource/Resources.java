package group.rxcloud.vrml.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Java Resources API.
 */
@SuppressWarnings("all")
public final class Resources {

    private static final Map<String, Object> fileCache;
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        fileCache = new ConcurrentHashMap<>();

        OBJECT_MAPPER = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Load resources to .properties.
     *
     * @param fileName the file name
     * @return the try
     */
    public static Try<Properties> loadResourcesProperties(String fileName) {
        return Try.of(() -> {
            return (Properties) fileCache.computeIfAbsent(fileName,
                    s -> {
                        try {
                            InputStreamReader inputStreamReader = JavaFileLoader.loadJavaResources(fileName);
                            Properties properties = new Properties();
                            properties.load(inputStreamReader);
                            return properties;
                        } catch (IOException e) {
                            throw new IllegalArgumentException(fileName + " load to .properties error.", e);
                        }
                    });
        });
    }

    /**
     * Load resources.
     *
     * @param <T>      the file class type
     * @param fileName the file name
     * @param fileType the file type
     * @return the try
     */
    public static <T> Try<T> loadResources(String fileName, Class<T> fileType) {
        return Try.of(() -> {
            return (T) fileCache.computeIfAbsent(fileName,
                    s -> {
                        try {
                            InputStreamReader inputStreamReader = JavaFileLoader.loadJavaResources(fileName);
                            return OBJECT_MAPPER.readValue(inputStreamReader, fileType);
                        } catch (IOException e) {
                            throw new IllegalArgumentException(fileName + " load to ." + fileType.getSimpleName() + " error.", e);
                        }
                    });
        });
    }

    /**
     * Load file.
     *
     * @param <T>      the file class type
     * @param fileName the file name
     * @param fileType the file type
     * @return the try
     */
    public static <T> Try<T> loadFile(String fileName, Class<T> fileType) {
        return Try.of(() -> {
            return (T) fileCache.computeIfAbsent(fileName,
                    s -> {
                        try {
                            InputStreamReader inputStreamReader = JavaFileLoader.loadSystemFile(fileName);
                            return OBJECT_MAPPER.readValue(inputStreamReader, fileType);
                        } catch (IOException e) {
                            throw new IllegalArgumentException(fileName + " load to ." + fileType.getSimpleName() + " error.", e);
                        }
                    });
        });
    }
}
