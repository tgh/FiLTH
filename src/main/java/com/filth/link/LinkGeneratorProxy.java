package com.filth.link;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;

/**
 * This class (created via {@link LinkGeneratorFactory}, which is configured in
 * filth-servlet.xml) proxies method calls of {@link LinkeGenerator} (which are
 * basically calls "links.xxx" from Freemarker templates); this will delegate
 * the method call to the appropriate Controller that implements the *LinkGenerator
 * that contains the method.
 *
 * @param <T> The interface extending all *LinkGenerator interfaces
 */
public class LinkGeneratorProxy<T> implements InvocationHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkGeneratorProxy.class);
    
    private Class<T> _clazz;
    private Map<Method,Object> _linkGeneratorMethodToControllerMap = new HashMap<Method, Object>();
    private Set<Class<?>> _allLinkGeneneratorInterfaces = new HashSet<Class<?>>();
    private Set<Class<?>> _remainingLinkGeneratorInterfacesToProcess = new HashSet<Class<?>>();
    private Map<Object,String> _interfaceToControllerMap = new HashMap<Object, String>();
    
    public LinkGeneratorProxy(Class<T> clazz, Map<String, Object> controllers) {
        _clazz = clazz;
        _allLinkGeneneratorInterfaces.addAll(Arrays.asList(_clazz.getInterfaces()));
        _remainingLinkGeneratorInterfacesToProcess.addAll(_allLinkGeneneratorInterfaces);
        
        LOGGER.info("***LinkGeneratorProxy***");
        
        //process all Controllers and their *LinkGenerator interfaces
        Set<Object> processedControllers = new HashSet<Object>();
        for (Entry<String, Object> entry: controllers.entrySet()) {
            Object controller = entry.getValue();
            if (!processedControllers.contains(controller)) {
                processController(controller, entry.getKey());
                processedControllers.add(controller);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <E> E createLinkGeneratorProxy(Class<E> clazz,
            Map<String, Object> controllers) {
        InvocationHandler proxy = new LinkGeneratorProxy<E>(clazz, controllers);
        return (E) Proxy.newProxyInstance(LinkGeneratorProxy.class.getClassLoader(),
                new Class[]{clazz}, proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //this case handles Object methods (notably toString())
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        
        //unknown method! (we didn't see this method during initialization)
        if (!_linkGeneratorMethodToControllerMap.containsKey(method)) {
            StringBuffer unmappedInterfacesStringBuffer = new StringBuffer();
            for (Class<?> linkGeneratorInterface : _remainingLinkGeneratorInterfacesToProcess) {
                unmappedInterfacesStringBuffer.append(linkGeneratorInterface.getName() + ", ");
            }

            throw new RuntimeException("LinkGeneratorProxy has no mapping info for method "
                    + method + "! Note that "
                    + "it does contain these methods: \n"
                    + StringUtils.join(_linkGeneratorMethodToControllerMap.keySet(), '\n') + "\n"
                    + "and also note it doesn't include these interfaces: \n"
                    + "'" + unmappedInterfacesStringBuffer.toString() + "'");
        }

        Object controller = _linkGeneratorMethodToControllerMap.get(method);
        
        //debug output
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Link generator proxy dispatching to controller with method " + method);
            
            if (args != null) {
                for (Object arg : args) {
                    String argStr;
                    if (arg == null) {
                        argStr = "null";
                    } else {
                        argStr = arg.getClass().getName() + ": " + arg.toString();
                    }
                    LOGGER.debug("   arg: " + argStr);
                }
            }
        }
        //end debug output
        
        try {
            return method.invoke(controller, args);
        } catch (InvocationTargetException e) {
            // explicitly unwrap so exception output is more readable
            throw e.getTargetException();
        }
    }
    
    /**
     * Finds all implementations of *LinkGenerator interfaces in all controllers.
     * 
     * @param controller -- the Controller to check
     * @param controllerName -- name of the Controller
     */
    private void processController(Object controller, String controllerName) throws BeansException {
        if (controller == null) {
            return;
        }

        Class<?> controllerClass = controller.getClass();
        if (AnnotationUtils.findAnnotation(controllerClass, Controller.class) == null) {
            return;
        }

        //iterate over all interfaces
        for (Class<?> linkGeneratorInterface : _allLinkGeneneratorInterfaces) {
            //does the controller implement this interface?
            if (linkGeneratorInterface.isAssignableFrom(controllerClass)) {
                LOGGER.debug("Controller " + controllerName + " has link generator interface "
                             + linkGeneratorInterface.getName() + "!");
                
                //*LinkGenerator interfaces should only be implemented by one controller each--
                //we should not have seen this interface from another controller
                if (!_remainingLinkGeneratorInterfacesToProcess.contains(linkGeneratorInterface)) {
                    throw new FatalBeanException("Interface "
                        + linkGeneratorInterface.getName() + " is "
                        + "implemented in two controllers; one is controller "
                        + controllerName + "; first was probably "
                        + _interfaceToControllerMap.get(linkGeneratorInterface));
                }
                //store the interface -> controller mapping (only used for error message above)
                _interfaceToControllerMap.put(linkGeneratorInterface, controllerName);
                //remove interface from list of remaining interfaces to find implementations for
                _remainingLinkGeneratorInterfacesToProcess.remove(linkGeneratorInterface);

                //iterate over all of the interface's methods
                for (Method interfaceMethod : linkGeneratorInterface.getMethods()) {
                    try {
                        LOGGER.debug("linkgenerator with id " + this.hashCode()
                            + " is mapping method " + interfaceMethod + " "
                            + interfaceMethod.getName() + " to controller");
                        
                        Method globalMethod = _clazz.getMethod(interfaceMethod.getName(),
                                interfaceMethod.getParameterTypes());
                        
                        //methods should be unique--we should not have already seen this method
                        if (_linkGeneratorMethodToControllerMap.containsKey(globalMethod)) {
                            throw new FatalBeanException(
                                "Method " + interfaceMethod.getName()
                                + " is double-mapped: first to controller "
                                + _linkGeneratorMethodToControllerMap.get(globalMethod)
                                + " and now to controller " + controllerName);
                        }
                        //validate that the method returns Link or String
                        if (globalMethod.getReturnType() != Link.class
                            && globalMethod.getReturnType() != String.class) {
                            throw new FatalBeanException(
                                "Interface method "+ interfaceMethod.getName()
                                + " does not return a Link or String instance." +
                            " They all must return Link or String.");
                        }
                        
                        //store the method -> controller mapping
                        _linkGeneratorMethodToControllerMap.put(globalMethod, controller);
                    } catch (NoSuchMethodException e) {
                        //don't see how this is possible.
                        throw new FatalBeanException(
                            "Could not find interface Method " + interfaceMethod.getName()
                            + "in global interface class " + _clazz
                            + " - unclear how this could happen.", e);
                    }
                }
            }
        }
    }

}
