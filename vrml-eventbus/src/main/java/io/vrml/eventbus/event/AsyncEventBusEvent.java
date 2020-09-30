package io.vrml.eventbus.event;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * The Async event ability.
 *
 * @param <Type> the async event type
 */
public interface AsyncEventBusEvent<Type extends AsyncEventBusEvent<Type>> extends EventBusEvent {

    /**
     * Clone a new event.
     *
     * @return the async event type
     */
    Type cloneEvent();

    /**
     * Copy a map.
     *
     * @param source the source map to copy.
     * @return the target map
     */
    default Map<String, String> mapClone(Map<String, String> source) {
        return CollectionUtils.isEmpty(source) ? new HashMap<>(2) : new HashMap<>(source);
    }
}
