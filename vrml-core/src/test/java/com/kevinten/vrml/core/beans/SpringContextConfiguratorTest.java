package com.kevinten.vrml.core.beans;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Spring context configurator test.
 */
public class SpringContextConfiguratorTest {

    /**
     * This is a spring app bootstrap class demo
     */
    public static class SpringAppBootstrap implements ApplicationContextAware {

        /**
         * The entry point of application.
         *
         * @param args the input arguments
         */
        public static void main(String[] args) {
            // run spring
        }

        /**
         * Inject {@code applicationContext} to static spring context container {@link SpringContextConfigurator}
         */
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            SpringContextConfigurator.setStaticApplicationContext(applicationContext);
        }
    }

    /**
     * You can get bean from spring context by {@code SpringContextConfigurator}.
     */
    public void getBeans(){
        SpringContextConfigurator.getBean("name");
    }
}