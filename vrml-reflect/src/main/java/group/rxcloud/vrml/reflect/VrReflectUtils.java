package group.rxcloud.vrml.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The Vrml reflect utils.
 */
public class VrReflectUtils {

    /**
     * Create obj by class object.
     *
     * @param clazz the clazz
     * @return the object
     * @throws NoSuchMethodException     the no such method exception
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException    the instantiation exception
     * @throws IllegalAccessException    the illegal access exception
     */
    public static <T> T createObjByClass(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> constructor = clazz.getConstructor();
        return constructor.newInstance();
    }
}
