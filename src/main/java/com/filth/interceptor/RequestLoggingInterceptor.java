package com.filth.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.filth.util.RequestLoggingUtil;

/**
 * Interceptor to log incoming requests, their parameters, and the corresponding handler.
 */
@Component
public class RequestLoggingInterceptor extends HandlerInterceptorAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    private static final PeriodFormatter FORMATTER = new PeriodFormatterBuilder().appendSecondsWithMillis()
                                                                                 .appendSuffix(" seconds")
                                                                                 .toFormatter();
    
    private static final DurationFieldType[] DURATION_FIELD_TYPES = {
        DurationFieldType.seconds(),
        DurationFieldType.millis(),
    };
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        JSON params = JSONSerializer.toJSON(request.getParameterMap());
        String message = String.format(
                "%1$s %2$s; Routed to: %3$s#%4$s; Session-id: %5$s; Parameters: %6$s",
                request.getMethod(),
                request.getRequestURL(),
                handlerMethod.getMethod().getDeclaringClass().getName(),
                handlerMethod.getMethod().getName(),
                request.getRequestedSessionId(),
                params);
        
        LOGGER.info(message);
        
        RequestLoggingUtil.setRequestTimestamp(new DateTime());
        
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        DateTime completionTime = new DateTime();
        DateTime startTime = RequestLoggingUtil.getRequestTimestamp();
        Period processingTime = new Period(startTime,completionTime);

        LOGGER.info(getCompletionMessage(processingTime));

        RequestLoggingUtil.clearRequestTimestamp();
    }

    /**
     * Get a 'processed request in x.xxx seconds' message.
     */
    public static String getCompletionMessage(Period period) {
        period = getNormalizedPeriod(period);
        String formattedInterval = period.toString(FORMATTER);
        return "Processed request in " + formattedInterval;
    }

    /**
     * Returns a normalized period, using the static set of duration field types
     * specified (in this case: seconds and millis)
     */
    private static Period getNormalizedPeriod(Period period) {
        PeriodType periodType = PeriodType.forFields(DURATION_FIELD_TYPES);
        Period normalizedPeriod = period.normalizedStandard(periodType);

        return normalizedPeriod;
    }

}
