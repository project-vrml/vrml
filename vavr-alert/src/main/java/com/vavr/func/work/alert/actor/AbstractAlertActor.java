package com.vavr.func.work.alert.actor;

import com.google.gson.Gson;
import com.vavr.func.work.alert.actor.crash.CrashAlertActor;
import lombok.extern.slf4j.Slf4j;

/**
 * The abstract alert actor.
 *
 * @param <T> the message type
 */
@Slf4j
public abstract class AbstractAlertActor<T extends AlertMessage> implements AlertActor<T> {

    /**
     * Formatting tool
     */
    protected static final Gson GSON = new Gson();

    /**
     * Tell message.
     *
     * @param message the message
     */
    protected void tell(AlertMessage message) {
        AlertActorSystem.tell(message);
    }

    @Override
    public void receive(T message) {
        try {
            onReceive(message);
        } catch (Exception e) {
            tell(new CrashAlertActor.CrashAlertMessage(message, e));
        }
    }

    /**
     * Receive message and process it.
     *
     * @param message the message
     */
    protected abstract void onReceive(T message);
}
