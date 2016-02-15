package com.filth.util;

import org.joda.time.DateTime;
import org.slf4j.MDC;

/**
 * Utilities for managing the diagnostic context for request logging
 * (e.g. getting/setting tracekeys for requests).
 */
public class RequestLoggingUtil {

    private static final String TRACEKEY = "tracekey";
    private static final String TIMESTAMP_KEY = "start_processing_time";

    private RequestLoggingUtil() {/* this class should not be instantiated */}

    /** Sets tracekey on MDC for this request **/
    public static void setTracekey(String tracekey) {
        MDC.put(TRACEKEY, tracekey);
    }

    /** Gets tracekey from MDC for this request **/
    public static String getTracekey() {
        return (String) MDC.get(TRACEKEY);
    }

    /** Removes tracekey from MDC for this request **/
    public static void clearTracekey() {
        MDC.remove(TRACEKEY);
    }

    /** Sets timestamp on MDC for this request to the current time **/
    public static void setRequestTimestamp() {
        setRequestTimestamp(new DateTime());
    }

    /** Sets timestamp on MDC for this request to the provided time **/
    public static void setRequestTimestamp(DateTime timestamp) {
        MDC.put(TIMESTAMP_KEY, timestamp.toString());
    }

    /** Gets timestamp from MDC for this request **/
    public static DateTime getRequestTimestamp() {
        return DateTime.parse(MDC.get(TIMESTAMP_KEY.toString()));
    }

    /** Removes timestamp from MDC for this request **/
    public static void clearRequestTimestamp() {
        MDC.remove(TIMESTAMP_KEY);
    }
}
