package group.rxcloud.vrml.maven.version;

import java.io.InputStream;
import java.util.Properties;

/**
 * The Maven Versions.
 */
public final class Versions {

    /**
     * Maven dependencies path.
     */
    private static final String POM_PROPERTIES = "/META-INF/maven/%s/%s/pom.properties";

    /**
     * Gets version.
     *
     * @param version the version
     * @return the version
     */
    public static Version getVersion(Version version) {
        if (version == null) {
            return new Version();
        }
        if (version.getGroupId() == null) {
            return version;
        }
        if (version.getArtifactId() == null) {
            return version;
        }
        try {
            String pom = String.format(POM_PROPERTIES, version.getGroupId(), version.getArtifactId());
            InputStream resource = Versions.class.getResourceAsStream(pom);
            Properties properties = new Properties();
            properties.load(resource);
            String property = properties.getProperty("version");
            version.setVersion(property);
        } catch (Exception e) {
            version.setVersion(e.getMessage());
        }
        return version;
    }
}
