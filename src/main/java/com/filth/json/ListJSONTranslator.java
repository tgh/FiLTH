package com.filth.json;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.filth.model.ListMovie;
import com.filth.model.Movie;
import com.filth.service.MovieService;

/**
 * JSON translator for {@link com.filth.model.List}.
 */
public class ListJSONTranslator implements JSONTranslator<com.filth.model.List> {
    
    public static final class ListJSONKey extends JSONKey {
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String RANK = "rank";
        public static final String COMMENTS = "comments";
        public static final String MOVIES = "movies";
    }
    
    private MovieService _movieService;

    @Override
    public com.filth.model.List fromJSON(String jsonString) {
        com.filth.model.List list = new com.filth.model.List();
        JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonString);
        
        String title = jsonObject.getString(ListJSONKey.TITLE);
        String author = jsonObject.optString(ListJSONKey.AUTHOR, null);
        list.setTitle(title);
        list.setAuthor(author);
        
        JSONArray moviesJsonArray = jsonObject.getJSONArray(ListJSONKey.MOVIES);
        for (int i=0; i < moviesJsonArray.size(); ++i) {
            JSONObject movieJsonObject = moviesJsonArray.getJSONObject(i);
            ListMovie listMovie = createListMovieFromJSON(movieJsonObject);
            list.addListMovie(listMovie);
        }
        
        return list;
    }

    @Override
    public JSONObject toJSON(com.filth.model.List list) {
        JSONObject listJsonObject = new JSONObject();
        int listId = list.getId();
        
        listJsonObject.put(ListJSONKey.ID, listId);
        listJsonObject.put(ListJSONKey.TITLE, list.getTitle());
        if (list.getAuthor() != null) {
            listJsonObject.put(ListJSONKey.AUTHOR, list.getAuthor());
        }
        
        if (list.getListMovies() != null) {
            JSONArray listMovieJSONArray = new JSONArray();
            for (ListMovie listMovie : list.getListMovies()) {
                JSONObject listMovieJSONObject = createListMovieJSONObject(listMovie, listId);
                listMovieJSONArray.add(listMovieJSONObject);
            }
            listJsonObject.put(ListJSONKey.MOVIES, listMovieJSONArray);
        }
        
        return listJsonObject;
    }
    
    private JSONObject createListMovieJSONObject(ListMovie listMovie, int listId) {
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put(ListJSONKey.ID, listMovie.getMovie().getId());
        if (listMovie.getRank() != null) {
            jsonObject.put(ListJSONKey.RANK, listMovie.getRank());
        }
        if (listMovie.getComments() != null) {
            jsonObject.put(ListJSONKey.COMMENTS, listMovie.getComments());
        }
        
        return jsonObject;
    }
    
    private ListMovie createListMovieFromJSON(JSONObject jsonObject) {
        ListMovie listMovie = new ListMovie();
        
        int movieId = jsonObject.getInt(ListJSONKey.ID);
        Movie movie = _movieService.getMovieByIdUninitialized(movieId);
        Integer rank = null;
        if (jsonObject.containsKey(ListJSONKey.RANK)) {
            rank = jsonObject.getInt(ListJSONKey.RANK);
        }
        String comments = jsonObject.optString(ListJSONKey.COMMENTS, null);
        
        listMovie.setMovie(movie);
        listMovie.setRank(Integer.valueOf(rank));
        listMovie.setComments(comments);
        
        return listMovie;
    }

}
