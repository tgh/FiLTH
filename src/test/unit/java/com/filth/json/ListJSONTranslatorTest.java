package com.filth.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.filth.test.factory.ListFactory;

/**
 * Unit test for {@link ListJSONTranslator}.
 */
public class ListJSONTranslatorTest {
    
    private final ListJSONTranslator _jsonTranslator = new ListJSONTranslator();
    
    @Test
    public void toJSON_listOnly() {
        com.filth.model.List list = ListFactory.createSimple();
        
        JSONObject jsonObject = _jsonTranslator.toJSON(list);
        
        assertNotNull(jsonObject);
        assertTrue(jsonObject.containsKey(ListJSONTranslator.ListJSONKey.ID));
        assertTrue(jsonObject.containsKey(ListJSONTranslator.ListJSONKey.TITLE));
        assertTrue(jsonObject.containsKey(ListJSONTranslator.ListJSONKey.AUTHOR));
        assertFalse(jsonObject.containsKey(ListJSONTranslator.ListJSONKey.MOVIES));
        
        int idFromJson = jsonObject.getInt(ListJSONTranslator.ListJSONKey.ID);
        assertEquals("Unexpected list id", ListFactory.SIMPLE_ID, idFromJson);
        String titleFromJson = jsonObject.getString(ListJSONTranslator.ListJSONKey.TITLE);
        assertEquals("Unexpected list title", ListFactory.SIMPLE_TITLE, titleFromJson);
        String authorFromJson = jsonObject.getString(ListJSONTranslator.ListJSONKey.AUTHOR);
        assertEquals("Unexpected list author", ListFactory.SIMPLE_AUTHOR, authorFromJson);
    }
    
    @Test
    public void toJSON_listWithMovies() {
        //10 movies
        com.filth.model.List list = ListFactory.createWithRandomMoviesNoRankings(10);
        JSONObject jsonObject = _jsonTranslator.toJSON(list);
        assertNotNull(jsonObject);
        assertTrue(jsonObject.containsKey(ListJSONTranslator.ListJSONKey.MOVIES));
        JSONArray moviesJSONArray = jsonObject.getJSONArray(ListJSONTranslator.ListJSONKey.MOVIES);
        assertFalse(moviesJSONArray.isEmpty());
        assertEquals(10, moviesJSONArray.size());
        
        //1 movies
        list = ListFactory.createWithRandomMoviesNoRankings(1);
        jsonObject = _jsonTranslator.toJSON(list);
        assertNotNull(jsonObject);
        assertTrue(jsonObject.containsKey(ListJSONTranslator.ListJSONKey.MOVIES));
        moviesJSONArray = jsonObject.getJSONArray(ListJSONTranslator.ListJSONKey.MOVIES);
        assertFalse(moviesJSONArray.isEmpty());
        assertEquals(1, moviesJSONArray.size());
        
        //100 movies
        list = ListFactory.createWithRandomMoviesNoRankings(100);
        jsonObject = _jsonTranslator.toJSON(list);
        assertNotNull(jsonObject);
        assertTrue(jsonObject.containsKey(ListJSONTranslator.ListJSONKey.MOVIES));
        moviesJSONArray = jsonObject.getJSONArray(ListJSONTranslator.ListJSONKey.MOVIES);
        assertFalse(moviesJSONArray.isEmpty());
        assertEquals(100, moviesJSONArray.size());
    }

}
