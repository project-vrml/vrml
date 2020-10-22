package com.kevinten.vrml.alert;

import com.kevinten.vrml.alert.actor.AlertActorSystem;
import com.kevinten.vrml.alert.actor.AlertMessage;
import com.kevinten.vrml.core.serialization.Serialization;
import lombok.extern.slf4j.Slf4j;

/**
 * The Alerts API.
 */
@Slf4j
public final class Alerts {

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
                log.warn("[Alerts.tell] tell message[{}] failure!", Serialization.GSON.toJson(message), exception);
            }
        }
    }
}
