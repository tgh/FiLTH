package com.filth.link;

import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

/**
 * Factory class to create a proxy for {@link LinkGenerator} methods.
 * (see filth-servlet.xml for its configuration)
 *
 * @param <T> The interface extending all *LinkGenerator interfaces
 */
public class LinkGeneratorFactory<T> implements FactoryBean<T> {
    
    @Autowired
    private ApplicationContext _appContext;
    
    private Class<T> _linkGeneratorInterface;
    
    public LinkGeneratorFactory(Class<T> clazz) {
        _linkGeneratorInterface = clazz;
    }

    @Override
    public T getObject() throws Exception {
        Map<String, Object> controllers = _appContext.getBeansWithAnnotation(Controller.class);
        return (T) LinkGeneratorProxy.createLinkGeneratorProxy(_linkGeneratorInterface, controllers);
    }

    @Override
    public Class<?> getObjectType() {
        return _linkGeneratorInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
