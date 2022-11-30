package group.rxcloud.vrml.alert.actor;

import group.rxcloud.vrml.alert.actor.crash.DefaultCrashAlertActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract alert actor.
 *
 * @param <T> the message type
 */
public abstract class AbstractAlertActor<T extends AlertMessage> implements AlertActor<T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractAlertActor.class);

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
