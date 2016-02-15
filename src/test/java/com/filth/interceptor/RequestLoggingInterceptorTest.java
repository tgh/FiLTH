package com.filth.interceptor;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Test;

/**
 * Unit Tests for {@link RequestLoggingInterceptor}
 */
public class RequestLoggingInterceptorTest {

    @Test
    public void getFormattedMessage_hourAndMinutePassed_formattedCorrectly() {
        DateTime time0 = new DateTime(2012, 11, 05, 1, 0, 0, 0);
        DateTime time1 = new DateTime(2012, 11, 05, 2, 1, 0, 0);
        Period period = new Period(time0, time1);

        String message = RequestLoggingInterceptor.getCompletionMessage(period);

        assertEquals("Processed request in 3660.000 seconds", message);
    }

    @Test
    public void getFormattedMessage_hourMinuteSecondsAndMillisecondsPassed_formattedCorrectly() {
        DateTime time0 = new DateTime(2012, 11, 05, 1, 0, 0, 0);
        DateTime time1 = new DateTime(2012, 11, 05, 2, 1, 1, 59);
        Period period = new Period(time0, time1);

        String message = RequestLoggingInterceptor.getCompletionMessage(period);

        assertEquals("Processed request in 3661.059 seconds", message);
    }

    @Test
    public void getFormattedMessage_secondsAndMillisecondsPassed_formattedCorrectly() {
        DateTime time0 = new DateTime(2012, 11, 05, 1, 0, 0, 0);
        DateTime time1 = new DateTime(2012, 11, 05, 1, 0, 1, 59);
        Period period = new Period(time0, time1);

        String message = RequestLoggingInterceptor.getCompletionMessage(period);

        assertEquals("Processed request in 1.059 seconds", message);
    }

    @Test
    public void getFormattedMessage_millisecondsPassed_formattedCorrectly() {
        DateTime time0 = new DateTime(2012, 11, 05, 1, 0, 0, 0);
        DateTime time1 = new DateTime(2012, 11, 05, 1, 0, 0, 59);
        Period period = new Period(time0, time1);

        String message = RequestLoggingInterceptor.getCompletionMessage(period);

        assertEquals("Processed request in 0.059 seconds", message);
    }
}
