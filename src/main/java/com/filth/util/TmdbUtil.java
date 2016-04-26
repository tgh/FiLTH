package com.filth.util;

import org.springframework.stereotype.Component;

/**
 * Spring-managed util class (see base-context.xml) that basically just stores
 * the TMDB API key.
 * <p>
 * The TMDB api key is found in conf/tmdb.properties as "tmdb.api.key" (that
 * file is not managed by git--you'll have to create it and insert the property
 * appropriately).
 */
@Component
public class TmdbUtil {
    
    private static String _tmdbApiKey;
    
    public static String getTmdbApiKey() {
        return _tmdbApiKey;
    }
    
    public void setTmdbApiKey(String apiKey) {
        _tmdbApiKey = apiKey;
    }

}
