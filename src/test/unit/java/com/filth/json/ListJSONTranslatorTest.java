package com.filth.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.filth.model.ListMovie;
import com.filth.test.factory.ListFactory;
import com.filth.test.factory.ListMovieFactory;

/**
 * Unit test for {@link ListJSONTranslator}.
 */
public class ListJSONTranslatorTest {
    
    private final ListJSONTranslator _jsonTranslator = new ListJSONTranslator();
    
    private static final class JsonTestSamples {
        public static final String SIMPLE_EMPTY_LIST = "{\"id\":" + ListFactory.SIMPLE_ID + ","
                + "\"title\":\"" + ListFactory.SIMPLE_TITLE + "\","
                + "\"author\":\"" + ListFactory.SIMPLE_AUTHOR + "\","
                + "\"movies\":[]}";
        public static final String SIMPLE_EMPTY_LIST_NO_AUTHOR = "{\"id\":" + ListFactory.SIMPLE_ID + ","
                + "\"title\":\"" + ListFactory.SIMPLE_TITLE + "\",\"movies\":[]}";
    }
    
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
        assertMoviesArray(moviesJSONArray);
        
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
        assertMoviesArray(moviesJSONArray);
    }
    
    @Test
    public void fromJSON_listOnly() {
        com.filth.model.List list = _jsonTranslator.fromJSON(JsonTestSamples.SIMPLE_EMPTY_LIST);
        assertNotNull(list);
        assertEquals(ListFactory.SIMPLE_ID, list.getId().intValue());
        assertEquals(ListFactory.SIMPLE_TITLE, list.getTitle());
        assertEquals(ListFactory.SIMPLE_AUTHOR, list.getAuthor());
        assertNull(list.getListMovies());
    }
    
    @Test
    public void fromJSON_listOnly_noAuthor() {
        com.filth.model.List list = _jsonTranslator.fromJSON(JsonTestSamples.SIMPLE_EMPTY_LIST_NO_AUTHOR);
        assertNotNull(list);
        assertEquals(ListFactory.SIMPLE_ID, list.getId().intValue());
        assertEquals(ListFactory.SIMPLE_TITLE, list.getTitle());
        assertNull(list.getAuthor());
        assertNull(list.getListMovies());
    }
    
    private void assertMoviesArray(JSONArray moviesJSONArray) {
        for (int i=0; i < moviesJSONArray.size(); ++i) {
            JSONObject movieJsonObject = moviesJSONArray.getJSONObject(i);
            assertTrue(movieJsonObject.containsKey(ListJSONTranslator.ListJSONKey.LIST_MOVIE_ID));
            assertEquals(ListMovieFactory.SIMPLE_ID,
                    movieJsonObject.getInt(ListJSONTranslator.ListJSONKey.LIST_MOVIE_ID));
            assertTrue(movieJsonObject.containsKey(ListJSONTranslator.ListJSONKey.MOVIE_ID));
        }
    }

}
