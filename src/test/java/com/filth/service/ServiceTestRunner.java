package com.filth.service;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Custom test-runner so we can perform any necessary logic prior to test
 * execution (such as db setup, for example).
 */
public class ServiceTestRunner extends SpringJUnit4ClassRunner {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceTestRunner.class);
    
    private ServiceTestAbstract _test;

    public ServiceTestRunner(Class<?> clazz) throws Exception {
        super(clazz);
        _test = (ServiceTestAbstract) createTest();
    }
    
    private boolean isTestRunnable(Method method) {
        String methodToRun = System.getProperty("net.wgen.spring.common.test.method");
        LOGGER.debug("Method to run: " + methodToRun);
        if(StringUtils.isBlank(methodToRun)) {
            // if a specific method isn't requested, every method is runnable.
            return true;    
        }
        // if a specific method is requested, run only the requested method
        return method.getName().equals(methodToRun);
    }
    
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (isTestRunnable(method.getMethod())) {
            _test.setUpBeforeTestMethod(method.getMethod());
            super.runChild(new FrameworkMethod(method.getMethod()), notifier);
        } else {
            notifier.fireTestIgnored(Description.createTestDescription(
                    getTestClass().getJavaClass(), method.getName()));
        }
    }

}
