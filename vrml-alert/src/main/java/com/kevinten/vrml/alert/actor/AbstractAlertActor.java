package com.kevinten.vrml.alert.actor;

import com.kevinten.vrml.alert.actor.crash.DefaultCrashAlertActor;
import lombok.extern.slf4j.Slf4j;

/**
 * The abstract alert actor.
 *
 * @param <T> the message type
 */
@Slf4j
public abstract class AbstractAlertActor<T extends AlertMessage> implements AlertActor<T> {

    /**
     * Tell message to actor.
     *
     * @param message the message
     */
    protected void tell(final AlertMessage message) {
        AlertActorSystem.tell(message);
    }

    /**
     * When an exception occurs, tell exception to {@link DefaultCrashAlertActor}.
     */
    @Override
    public void receive(final T message) {
        try {
            onReceive(message);
        } catch (Exception e) {
            tell(new DefaultCrashAlertActor.DefaultCrashAlertMessage(message, e));
        }
    }

    /**
     * Receive a message and process it.
     *
     * @param message the message
     */
    protected abstract void onReceive(final T message);
}
