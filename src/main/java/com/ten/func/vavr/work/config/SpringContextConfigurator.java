package com.ten.func.vavr.work.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Spring静态容器
 *
 * @author shihaowang
 * @date 2019/9/11
 */
@Service
@Lazy(false)
public class SpringContextConfigurator implements ApplicationContextAware, DisposableBean {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContext.setApplicationContext(applicationContext);
    }

    /**
     * 取得存储在静态变量中的ApplicationContext.
     */
    public static ApplicationContext getApplicationContext() {
        return AppContext.getApplicationContext();
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) AppContext.getApplicationContext().getBean(name);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name, T defaultBean) {
        T bean = (T) AppContext.getApplicationContext().getBean(name);
        return bean == null ? defaultBean : bean;
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        return AppContext.getApplicationContext().getBean(requiredType);
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType, T defaultBean) {
        T bean = AppContext.getApplicationContext().getBean(requiredType);
        return bean == null ? defaultBean : bean;
    }

    /**
     * 实现DisposableBean接口, 在Context关闭时清理静态变量.
     */
    @Override
    public void destroy() throws Exception {
        AppContext.clear();
    }

    private static class AppContext {

        private static ApplicationContext ctx;

        /**
         * Injected from the class "ApplicationContextProvider" which is automatically
         * loaded during Spring-Initialization.
         */
        public static void setApplicationContext(ApplicationContext applicationContext) {
            ctx = applicationContext;
        }


        /**
         * Get access to the Spring ApplicationContext from everywhere in your Application.
         *
         * @return
         */
        public static ApplicationContext getApplicationContext() {
            checkApplicationContext();
            return ctx;
        }

        public static void clear() {
            ctx = null;
        }

        private static void checkApplicationContext() {
            if (ctx == null) {
                throw new IllegalStateException("applicaitonContext not injected.");
            }
        }
    }

}