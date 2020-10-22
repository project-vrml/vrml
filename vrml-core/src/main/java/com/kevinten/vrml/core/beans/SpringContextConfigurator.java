package com.kevinten.vrml.core.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * Spring application context static container.
 *
 * @apiNote Assemble context in your startup class
 */
public final class SpringContextConfigurator implements ApplicationContextAware, DisposableBean {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContext.setApplicationContext(applicationContext);
    }

    /**
     * Sets static application context.
     *
     * @param applicationContext the application context
     */
    public static void setStaticApplicationContext(ApplicationContext applicationContext) {
        AppContext.setApplicationContext(applicationContext);
    }

    /**
     * Gets the application Context stored in a static variable.
     *
     * @return the application context
     */
    public static ApplicationContext getApplicationContext() {
        return AppContext.getApplicationContext();
    }

    /**
     * Get the Bean from the static variable applicationContext and
     * automatically transform it to the type of the assigned object.
     *
     * @param <T>  the type parameter
     * @param name the name
     * @return the bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) AppContext.getApplicationContext().getBean(name);
    }

    /**
     * Get the Bean from the static variable applicationContext and
     * automatically transform it to the type of the assigned object.
     *
     * @param <T>         the type parameter
     * @param name        the name
     * @param defaultBean the default bean
     * @return the bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name, T defaultBean) {
        T bean = (T) AppContext.getApplicationContext().getBean(name);
        return bean == null ? defaultBean : bean;
    }

    /**
     * Load the Bean from the static variable applicationContext and
     * automatically transform it to the type of the assigned object.
     *
     * @param <T>          the type parameter
     * @param requiredType the required type
     */
    public static <T> void loadBean(Class<T> requiredType) {
        AppContext.getApplicationContext().getBean(requiredType);
    }

    /**
     * Get the Bean from the static variable applicationContext and
     * automatically transform it to the type of the assigned object.
     *
     * @param <T>          the type parameter
     * @param requiredType the required type
     * @return the bean
     */
    public static <T> T getBean(Class<T> requiredType) {
        return AppContext.getApplicationContext().getBean(requiredType);
    }

    /**
     * Get the Beans from the static variable applicationContext and
     * automatically transform them to the type of the assigned object.
     *
     * @param <T>          the type parameter
     * @param requiredType the required type
     * @return a Map with the matching beans, containing the bean names as keys and the corresponding bean instances as values
     */
    public static <T> Map<String, T> getBeans(Class<T> requiredType) {
        return AppContext.getApplicationContext().getBeansOfType(requiredType);
    }

    /**
     * Get the Bean from the static variable applicationContext and
     * automatically transform it to the type of the assigned object.
     *
     * @param <T>          the type parameter
     * @param requiredType the required type
     * @param defaultBean  the default bean
     * @return the bean
     */
    public static <T> T getBean(Class<T> requiredType, T defaultBean) {
        T bean = AppContext.getApplicationContext().getBean(requiredType);
        return bean == null ? defaultBean : bean;
    }

    /**
     * Clean up static variables when the Context is off.
     */
    @Override
    public void destroy() throws Exception {
        AppContext.clear();
    }
}

/**
 * Spring static application context container.
 */
class AppContext {

    private static ApplicationContext ctx;

    /**
     * Injected from the class "ApplicationContextProvider" which is
     * automatically loaded during Spring-Initialization.
     *
     * @param applicationContext the application context
     */
    static void setApplicationContext(ApplicationContext applicationContext) {
        ctx = applicationContext;
    }

    /**
     * Get access to the Spring ApplicationContext from everywhere in your Application.
     *
     * @return the application context
     */
    static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return ctx;
    }

    /**
     * Clean up static variables when the Context is off.
     */
    static void clear() {
        ctx = null;
    }

    private static void checkApplicationContext() {
        if (ctx == null) {
            throw new IllegalStateException("[vrml.SpringContextConfigurator.AppContext] applicationContext not injected.");
        }
    }
}
