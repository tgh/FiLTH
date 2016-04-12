package com.filth.service;

import java.lang.reflect.Method;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

/**
 * Abstract class for all *ServiceTests
 */
@RunWith(ServiceTestRunner.class)
@ContextConfiguration(locations = {"file:build/test/com/filth/service/service-test-context.xml"})
public abstract class ServiceTestAbstract {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceTestAbstract.class);

    /**
     * Gets called by ServiceTestRunner before every test method.
     */
    public final void setUpBeforeTestMethod(Method method) {
        LOGGER.info("Setting up prior to running test: " + method.getName());
        
//        // Set up session and request scoping
//        _applicationContextService.setUpMockSessionAndScoping();
//        if (!method.isAnnotationPresent(KeepNonStaticTables.class)) {
//            _dbTruncator.truncateNonStaticTables();
//        }
//        _dbDataLoader.runDataMakers(method);
//        _mockUserService.setCurrentSecureUser(null);
//        _mockHttpSession = new MockHttpSession();
//        getNewRequest();
    }
}
