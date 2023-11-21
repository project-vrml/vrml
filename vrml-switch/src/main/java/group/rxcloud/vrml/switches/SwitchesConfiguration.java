package group.rxcloud.vrml.switches;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import group.rxcloud.vrml.core.serialization.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Switches configuration.
 */
public interface SwitchesConfiguration {

    /**
     * Logger
     */
    Logger log = LoggerFactory.getLogger(SwitchesConfiguration.class);

    /**
     * Static default GSON parser
     */
    Gson GSON = new Gson();

    /**
     * Same string, return cache parsed json object
     */
    Map<String, JsonObject> STRING_JSON_OBJECT_MAP = new ConcurrentHashMap<>();

    /**
     * Gets params as {@code Json} obj.
     *
     * @return the params
     */
    JsonObject getParams();

    /**
     * Convert JsonObject From string with cache
     */
    default JsonObject convertToParamsFromString(String params) {
        if (!StringUtils.hasLength(params)) {
            return null;
        }
        return STRING_JSON_OBJECT_MAP.computeIfAbsent(params,
                str -> GSON.fromJson(str, JsonObject.class));
    }

    /**
     * Check switches.
     *
     * @param switchKeys the switch keys
     * @return the result
     */
    default Optional<Boolean> checkSwitches(List<String> switchKeys) {
        if (CollectionUtils.isEmpty(switchKeys)) {
            return Optional.of(false);
        }

        JsonObject currentObj = this.getParams();
        if (currentObj == null) {
            return Optional.empty();
        }

        for (int i = 0; i < switchKeys.size(); i++) {
            String key = switchKeys.get(i);

            // final key must be boolean
            boolean finalKey = (i == switchKeys.size() - 1);

            if (currentObj.has(key)) {
                JsonElement value = currentObj.get(key);
                // iterator
                if (value.isJsonObject()) {
                    currentObj = value.getAsJsonObject();
                } else {
                    // final key must be boolean
                    if (finalKey) {
                        try {
                            return Optional.of(value.getAsBoolean());
                        } catch (Exception e) {
                            log.error("[SwitchesConfiguration.checkSwitches] get switchKeys[{}] from params[{}] error",
                                    Serialization.toJsonSafe(switchKeys), Serialization.toJsonSafe(this.getParams()), e);
                        }
                    }

                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
