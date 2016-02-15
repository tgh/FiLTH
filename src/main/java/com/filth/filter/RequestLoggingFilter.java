package com.filth.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.filth.util.RequestLoggingUtil;

/**
 * This filter generates a tracekey for all requests (because the servlet
 * container doesn't generate one for us); it's a UUID because it's pretty
 * close to certain that it'll be unique per-request. We use this to tag every
 * log message the application generates with a request id so that we
 * can see what messages were generated during the process of a given
 * request.
 */
public class RequestLoggingFilter implements Filter {
    
    @Override
    public void init(FilterConfig config) throws ServletException {}

    @Override
    public void destroy(){}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        // skip non-http requests
        if (!(request instanceof HttpServletRequest)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String tracekey = UUID.randomUUID().toString();
            RequestLoggingUtil.setTracekey(tracekey);
            filterChain.doFilter(request,response);
        } finally {
            RequestLoggingUtil.clearTracekey();
        }
    }

}
