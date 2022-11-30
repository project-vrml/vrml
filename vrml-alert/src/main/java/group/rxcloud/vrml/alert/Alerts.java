package group.rxcloud.vrml.alert;

import group.rxcloud.vrml.alert.actor.AlertActorSystem;
import group.rxcloud.vrml.alert.actor.AlertMessage;
import group.rxcloud.vrml.core.serialization.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Alerts API.
 */
public final class Alerts {

    private static final Logger log = LoggerFactory.getLogger(Alerts.class);

    /**
     * Tell alert messages.
     *
     * @param messages the immutable messages
     */
    public static void tell(final AlertMessage... messages) {
        for (final AlertMessage message : messages) {
            try {
                AlertActorSystem.tell(message);
            } catch (Exception exception) {
                log.warn("[Vrml][Alerts.tell] tell message[{}] failure!", Serialization.GSON.toJson(message), exception);
            }
        }
    }
}
