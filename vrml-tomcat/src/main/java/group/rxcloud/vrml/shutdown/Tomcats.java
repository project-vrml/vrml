package group.rxcloud.vrml.shutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tomcat mbean monitor.
 * <pre>
 * support:
 * sprint-boot-tomcat-embed server
 * original tomcat server
 * </pre>
 *
 * @link <a href="https://tomcat.apache.org/tomcat-8.5-doc/config/http.html">tomcat</a>
 */
public final class Tomcats {

    private static final Logger logger = LoggerFactory.getLogger(Tomcats.class);
    /**
     * sprint-boot-tomcat-embed server
     */
    private static final String tomcatEmbedDomain = "Tomcat";
    /**
     * tomcat server
     */
    private static final String tomcatDomain = "Catalina";

    private static final String NIO_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";

    /**
     * Get Tomcat mbean properties.
     */
    public Map<String, Object> monitor(List<String> externalAttributes) {
        Map<String, Object> map = new HashMap<>(4);

        List<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (!mBeanServers.isEmpty()) {
            MBeanServer mBeanServer = mBeanServers.get(0);
            String domain = this.getTomcatDomain(mBeanServer);
            try {
                ObjectName objectName = new ObjectName(domain, "type", "Service");
                ObjectName[] objNames = (ObjectName[]) mBeanServer.getAttribute(objectName, "connectorNames");
                for (ObjectName on : objNames) {
                    Map<String, Object> connector = new HashMap<>(16);

                    Object protocol = mBeanServer.getAttribute(on, "protocol");
                    connector.put("protocol", protocol);

                    Object acceptCount = mBeanServer.getAttribute(on, "acceptCount");
                    connector.put("acceptCount", acceptCount);

                    Object keepAliveTimeout = mBeanServer.getAttribute(on, "keepAliveTimeout");
                    connector.put("keepAliveTimeout", keepAliveTimeout);

                    Object maxKeepAliveRequests = mBeanServer.getAttribute(on, "maxKeepAliveRequests");
                    connector.put("maxKeepAliveRequests", maxKeepAliveRequests);

                    Object connectionTimeout = mBeanServer.getAttribute(on, "connectionTimeout");
                    connector.put("connectionTimeout", connectionTimeout);

                    Object protocolHandlerClassName = mBeanServer.getAttribute(on, "protocolHandlerClassName");
                    connector.put("protocolHandlerClassName", protocolHandlerClassName);

                    Object enableLookups = mBeanServer.getAttribute(on, "enableLookups");
                    connector.put("enableLookups", enableLookups);

                    Object uriEncoding = mBeanServer.getAttribute(on, "URIEncoding");
                    connector.put("URIEncoding", uriEncoding);

                    Object useBodyEncodingForURI = mBeanServer.getAttribute(on, "useBodyEncodingForURI");
                    connector.put("useBodyEncodingForURI", useBodyEncodingForURI);

                    // external
                    if (externalAttributes != null && externalAttributes.size() > 0) {
                        for (String externalAttribute : externalAttributes) {
                            Object attribute = mBeanServer.getAttribute(on, externalAttribute);
                            connector.put(externalAttribute, attribute);
                        }
                    }

                    Object localPort = mBeanServer.getAttribute(on, "localPort");
                    map.put("connector-" + localPort, connector);

                    if (NIO_PROTOCOL.equalsIgnoreCase(protocolHandlerClassName.toString())) {
                        String threadPoolONStr = domain + ":type=ThreadPool,name=\"http-nio-" + localPort + "\"";
                        ObjectName threadPoolON = new ObjectName(threadPoolONStr);

                        Map<String, Object> threadPoolMap = new HashMap<>(16);

                        Object backlog = mBeanServer.getAttribute(threadPoolON, "backlog");
                        threadPoolMap.put("backlog", backlog);

                        Object connectionCount = mBeanServer.getAttribute(threadPoolON, "connectionCount");
                        threadPoolMap.put("connectionCount", connectionCount);

                        Object currentThreadCount = mBeanServer.getAttribute(threadPoolON, "currentThreadCount");
                        threadPoolMap.put("currentThreadCount", currentThreadCount);

                        Object currentThreadsBusy = mBeanServer.getAttribute(threadPoolON, "currentThreadsBusy");
                        threadPoolMap.put("currentThreadsBusy", currentThreadsBusy);

                        Object keepAliveCount = mBeanServer.getAttribute(threadPoolON, "keepAliveCount");
                        threadPoolMap.put("keepAliveCount", keepAliveCount);

                        Object maxConnections = mBeanServer.getAttribute(threadPoolON, "maxConnections");
                        threadPoolMap.put("maxConnections", maxConnections);

                        Object maxThreads = mBeanServer.getAttribute(threadPoolON, "maxThreads");
                        threadPoolMap.put("maxThreads", maxThreads);

                        Object minSpareThreads = mBeanServer.getAttribute(threadPoolON, "minSpareThreads");
                        threadPoolMap.put("minSpareThreads", minSpareThreads);

                        // external
                        if (externalAttributes != null && externalAttributes.size() > 0) {
                            for (String externalAttribute : externalAttributes) {
                                Object attribute = mBeanServer.getAttribute(threadPoolON, externalAttribute);
                                threadPoolMap.put(externalAttribute, attribute);
                            }
                        }

                        connector.put("threadPool", threadPoolMap);
                    }
                }
            } catch (Exception e) {
                logger.warn("[Vrml.Tomcat] get tomcat mbean properties error.", e);

                map.put("exception", e.getMessage());
            }
        }
        return map;
    }

    private String getTomcatDomain(MBeanServer mBeanServer) {
        try {
            // try to get spring-boot-tomcat-embed server
            ObjectName objectName = new ObjectName(tomcatEmbedDomain, "type", "Service");
            mBeanServer.getAttribute(objectName, "connectorNames");
            return tomcatEmbedDomain;
        } catch (Exception e) {
            // try to get tomcat server
            return tomcatDomain;
        }
    }
}
