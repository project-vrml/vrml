package group.rxcloud.vrml.core.serialization;

import com.google.gson.Gson;
import io.vavr.control.Try;

/**
 * The Serialization Tool.
 */
public abstract class Serialization {

    /**
     * The Gson Tool.
     */
    public static Gson GSON = new Gson();

    /**
     * Sets gson.
     *
     * @param gson the gson
     */
    public static void setGson(Gson gson) {
        Serialization.GSON = gson;
    }

    /**
     * Serialize object by Gson safely.
     *
     * @param src the object for which Json representation is to be created setting for Gson
     * @return Json representation of {@code src} or {@code src.toString()} when parse error
     */
    public static String toJsonSafe(Object src) {
        return Try.of(() -> GSON.toJson(src))
                .recover(throwable -> src.toString())
                .get();
    }
}
