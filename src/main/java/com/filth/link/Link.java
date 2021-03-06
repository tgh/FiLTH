package com.filth.link;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Represents a link to a resource provided by the application.
 * Typically, this resource is resolved by application controllers.
 * This link can be turned into a string URL pointing to the specified resource.
 * 
 * Parts of a URL generated by this Link (to clear up variable/method names):
 * 
 *    /oib/rubric/view?id=1234&v=5#standards
 *     [-] [---------] [---------] [-------]
 *      |       |           |          |
 *    context   path      params      anchor
 * 
 * This link only produces links relative to the local spring application context.
 */
public class Link {
    
    private final String _path;
    private final String _anchor;
    private final List<BasicNameValuePair> _params;

    public Link(String urlBase, String anchor) {
        _path = urlBase;
        _anchor = anchor;
        _params = new ArrayList<BasicNameValuePair>();
    }

    public Link(String urlBase) {
        this(urlBase, null);
    }

    /**
     * Set the specified URL parameter to the specified value.
     * 
     * @return a pointer to this link object solely for chaining convenience:
     * 
     *   Link link = new Link(url).setParam("a","1").setParam("b","2");
     */
    public Link setParam(String paramKey, String paramValue) {
        _params.add(new BasicNameValuePair(paramKey, paramValue));
        return this;
    }

    /**
     * See {@link #setParam(String, String)}
     * @param paramKey parameter key
     * @param paramValue an object to stringify
     * @return a pointer to this link object solely for chaining convenience
     */
    public Link setParam(String paramKey, Object paramValue) {
        String val = null == paramValue ? "" : paramValue.toString();
        return setParam(paramKey, val);
    }

    /**
     * See {@link #setParam(String, String)}
     * @param paramKey parameter key
     * @param paramValue a long value, which will be stringified
     * @returna pointer to this link object solely for chaining convenience
     */
    public Link setParam(String paramKey, long paramValue) {
        return setParam(paramKey, Long.toString(paramValue));
    }

    /**
     * @return the URL this object represents
     */
    @Override
    public String toString() {
        return getURL();
    }

    /**
     * @return a ModelAndView that redirects to the URL owned by this object.
     */
    public ModelAndView createRedirect() {
        return new ModelAndView(new RedirectView(toString()));
    }

    /**
     * Creates the full representation of this Link, including the base URI,
     * query string, and anchor. 
     * @return the full url string, ready for public use
     */
    private String getURL() {
        return getURLContext() + _path + getEncodedParams() + getAnchor();
    }
    
    private String getEncodedParams() {
        if (_params != null && !_params.isEmpty()) {
            return "?" + URLEncodedUtils.format(_params, "UTF-8");
        } else {
            return "";
        }
    }
    
    private String getAnchor() {
        if (_anchor != null && !_anchor.isEmpty()) {
            return "#" + _anchor;
        } else {
            return "";
        }
    }
    
    /**
     * Return the application context path from Spring.
     */
    protected String getURLContext() {
        
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attr == null) {
            throw new RuntimeException("HttpServletRequest not initialized, can't get app context " +
                                       "- probably in a test context?");
        }
        
        HttpServletRequest req = attr.getRequest();
        
        return req.getContextPath();
    }
}