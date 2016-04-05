package com.filth.util;

import org.apache.commons.lang.StringUtils;

/**
 * Miscellaneous utility methods.
 */
public class GeneralUtil {

    public static String buildFullName(String first, String middle, String last) {
        StringBuilder sb = new StringBuilder();
        
        if (StringUtils.isNotEmpty(first)) {
            sb.append(first);
            sb.append(' ');
        }
        if (StringUtils.isNotEmpty(middle)) {
            sb.append(middle);
            sb.append(' ');
        }
        sb.append(last);
        
        return sb.toString();
    }
}
