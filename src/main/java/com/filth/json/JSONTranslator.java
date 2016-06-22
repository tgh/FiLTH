package com.filth.json;

import net.sf.json.JSONObject;

/**
 * Translates an object of type <T> to and from a JSON representation.
 */
public interface JSONTranslator<T> {
    
    /** Common JSON keys */
    public static class JSONKey {
        public static final String ID = "id";
    }

    public T fromJSON(String jsonString);
    public JSONObject toJSON(T object);
    
}
