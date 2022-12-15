package group.rxcloud.vrml.maven.version;

/**
 * The Maven Version.
 */
public class Version {

    private String groupId;
    private String artifactId;
    private String version;

    /**
     * Gets group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets group id.
     *
     * @param groupId the group id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets artifact id.
     *
     * @return the artifact id
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Sets artifact id.
     *
     * @param artifactId the artifact id
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets version.
     *
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
