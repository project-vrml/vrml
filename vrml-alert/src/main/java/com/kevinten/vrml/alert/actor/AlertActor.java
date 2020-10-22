package com.kevinten.vrml.alert.actor;

/**
 * The Alert actor.
 *
 * @param <T> the alert message type
 */
public interface AlertActor<T extends AlertMessage> {

    /**
     * Receive the alert message.
     *
     * @param message the message
     */
    void receive(final T message);
}
