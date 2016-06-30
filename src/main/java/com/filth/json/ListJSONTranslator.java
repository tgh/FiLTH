package com.filth.json;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.filth.model.ListMovie;
import com.filth.model.Movie;
import com.filth.service.MovieService;

/**
 * JSON translator for {@link com.filth.model.List}.
 */
@Component
public class ListJSONTranslator implements JSONTranslator<com.filth.model.List> {
    
    public static final class ListJSONKey extends JSONKey {
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String RANK = "rank";
        public static final String COMMENTS = "comments";
        public static final String MOVIES = "movies";
        public static final String MOVIE_ID = "mid";
        public static final String LIST_MOVIE_ID = "lmid";
    }
    
    @Resource
    private MovieService _movieService;

    @Override
    public com.filth.model.List fromJSON(String jsonString) {
        com.filth.model.List list = new com.filth.model.List();
        JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonString);

        Integer id = (Integer) jsonObject.opt(ListJSONKey.ID);
        String title = jsonObject.getString(ListJSONKey.TITLE);
        String author = jsonObject.optString(ListJSONKey.AUTHOR, null);
        
        //force author to be null if author is the empty string
        if (StringUtils.isEmpty(author)) {
            author = null;
        }
        
        list.setId(id);
        list.setTitle(title);
        list.setAuthor(author);
        
        JSONArray moviesJsonArray = jsonObject.getJSONArray(ListJSONKey.MOVIES);
        for (int i=0; i < moviesJsonArray.size(); ++i) {
            JSONObject movieJsonObject = moviesJsonArray.getJSONObject(i);
            ListMovie listMovie = createListMovieFromJSON(movieJsonObject, list);
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
        
        jsonObject.put(ListJSONKey.LIST_MOVIE_ID, listMovie.getId());
        jsonObject.put(ListJSONKey.MOVIE_ID, listMovie.getMovie().getId());
        if (listMovie.getRank() != null) {
            jsonObject.put(ListJSONKey.RANK, listMovie.getRank());
        }
        if (listMovie.getComments() != null) {
            jsonObject.put(ListJSONKey.COMMENTS, listMovie.getComments());
        }
        
        return jsonObject;
    }
    
    private ListMovie createListMovieFromJSON(JSONObject jsonObject, com.filth.model.List list) {
        ListMovie listMovie = new ListMovie();
        
        Integer listMovieId = (Integer) jsonObject.opt(ListJSONKey.LIST_MOVIE_ID);
        int movieId = jsonObject.getInt(ListJSONKey.MOVIE_ID);
        Movie movie = _movieService.getMovieByIdUninitialized(movieId);
        Integer rank = null;
        if (jsonObject.containsKey(ListJSONKey.RANK)) {
            rank = jsonObject.getInt(ListJSONKey.RANK);
        }
        String comments = jsonObject.optString(ListJSONKey.COMMENTS, null);
        
        listMovie.setId(listMovieId);
        listMovie.setMovie(movie);
        listMovie.setRank(Integer.valueOf(rank));
        listMovie.setComments(comments);
        listMovie.setList(list);
        
        return listMovie;
    }

}
