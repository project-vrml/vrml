package com.vavr.func.work.eventbus.event;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * The async event cloneable.
 *
 * @param <Type> the type of event
 */
@FunctionalInterface
public interface AsyncEventCloneable<Type extends AsyncEventCloneable> {

    /**
     * Clone new event.
     *
     * @return the event type
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
