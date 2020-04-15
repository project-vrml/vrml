package com.vavr.func.work.alert.actor;

/**
 * The interface Alert actor.
 *
 * @param <T> the message type
 */
public interface AlertActor<T extends AlertMessage> {

    /**
     * Receive message.
     *
     * @param message the message
     */
    void receive(T message);
}
