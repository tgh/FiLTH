package com.filth.controller.admin;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.filth.annotation.SkipInterceptor;
import com.filth.interceptor.BackgroundImageInterceptor;
import com.filth.json.ListJSONTranslator;
import com.filth.link.Link;
import com.filth.link.ManageListsLinkGenerator;
import com.filth.model.ListMovie;
import com.filth.model.Movie;
import com.filth.service.ListMovieService;
import com.filth.service.ListService;
import com.filth.service.MovieService;
import com.filth.util.ModelAndViewUtil;

@Controller
public class ManageListsController extends ManageEntityController implements ManageListsLinkGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageListsController.class);
    
    private static final String ENTITY_NAME = "List";
    private static final String NEW_LIST_DEFAULT_TITLE = "Untitled List";
    
    @Resource
    private ListService _listService;
    @Resource
    private ListMovieService _listMovieService;
    @Resource
    private ModelAndViewUtil _modelAndViewUtil;
    @Resource
    private ListJSONTranslator _listJSONTranslator;
    @Resource
    private MovieService _movieService;
    
    private static final class URL {
        public static final String LISTS = ADMIN_URL_PREFIX + "/lists";
        public static final String LIST = ADMIN_URL_PREFIX + "/list";
        public static final String DELETE = LIST + "/delete";
        public static final String SAVE = LIST + "/save";
        public static final String REMOVE_MOVIE = LIST + "/removeMovie";
    }
    
    private static final class URLParam {
        //TECH-DEBT: make all ids explicit (e.g. LIST_ID, not ID),
        //and perhaps move to a common location for all Controllers to use
        //Also: if ID here is changed, viewList.js will need updating
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String AUTHOR = "author";
        public static final String LIST_JSON = "listJSON";
        public static final String MOVIE_ID = "movieId";
    }
    
    private static final class ModelKey {
        public static final String LISTS = "lists";
        public static final String LIST = "list";
        public static final String LIST_JSON = "listJSON";
        public static final String MOVIES = "movies";
    }

    @Override
    public Link getLinkToManageLists() {
        return new Link(URL.LISTS);
    }

    @Override
    public Link getLinkToDeleteList() {
        return new Link(URL.DELETE);
    }

    @Override
    public Link getLinkToDeleteList(int id) {
        return new Link(URL.DELETE).setParam(URLParam.ID, String.valueOf(id));
    }

    @Override
    public Link getLinkToSaveList() {
        return new Link(URL.SAVE);
    }
    
    @Override
    public Link getLinkToNewList() {
        return new Link(URL.LIST);
    }
    
    @Override
    public Link getLinkToList(int id) {
        return new Link(URL.LIST).setParam(URLParam.ID, String.valueOf(id));
    }
    
    @Override
    public Link getLinkToRemoveMovieFromList() {
        return new Link(URL.REMOVE_MOVIE);
    }
    
    @Override
    public Link getLinkToRemoveMovieFromList(int listId, int movieId) {
        return new Link(URL.REMOVE_MOVIE).setParam(URLParam.ID, String.valueOf(listId))
                                         .setParam(URLParam.MOVIE_ID, movieId);
    }
    
    @RequestMapping(value=URL.LISTS, method=RequestMethod.GET)
    public ModelAndView manageLists() {
        List<com.filth.model.List> lists = _listService.getAllLists();
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.LISTS, lists);

        return new ModelAndView(ADMIN_VIEW_PREFIX + "/manage_lists", mm);
    }
    
    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST, params=URLParam.TITLE)
    public ModelAndView saveList(
            @RequestParam(value=URLParam.TITLE) String title,
            @RequestParam(value=URLParam.AUTHOR, required=false) String author,
            @RequestParam(value=URLParam.ID, required=false) Integer id) throws Exception {
        com.filth.model.List list = null;
        
        try {
            if (null != id) {
                list = _listService.getListById(id);
            } else {
                list = new com.filth.model.List();
            }
            
            list.setTitle(title);
            
            if (null != author) {
                list.setAuthor(author);
            }
            
            _listService.saveList(list);
        } catch (Exception e) {
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, title), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, title), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.LIST, list);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, title), mm);
    }
    
    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.SAVE, method=RequestMethod.POST, params=URLParam.LIST_JSON)
    public ModelAndView saveListJSON(
            @RequestParam(value=URLParam.LIST_JSON) String listJSON) throws Exception {
        String title = "[unknown title]";
        com.filth.model.List listToSave = null;
        
        try {
            com.filth.model.List listFromJSON = _listJSONTranslator.fromJSON(listJSON);
            title = listFromJSON.getTitle();
            
            //existing list
            if (null != listFromJSON.getId()) {
                listToSave = _listService.getListById(listFromJSON.getId());
                listToSave.copyContent(listFromJSON);
            }
            //new list
            else {
                listToSave = listFromJSON;
            }
            
            _listService.saveList(listToSave);
        } catch (Exception e) {
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, title), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, title), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.LIST, listToSave);
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, title), mm);
    }
    
    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.DELETE, method=RequestMethod.POST)
    public ModelAndView deleteList(
            @RequestParam(value=URLParam.ID) Integer id) throws Exception {
        ModelMap mm = new ModelMap();
        com.filth.model.List list = _listService.getListById(id);
        
        if (null == list) {
            LOGGER.error(String.format(UNKNOWN_ENTITY_LOG_MESSAGE_FORMAT, ENTITY_NAME, id));
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, id), mm);
        }
        
        try {
            _listService.deleteListById(id);
        } catch (Exception e) {
            LOGGER.error(String.format(DELETE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, list.getTitle()), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(DELETE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, list.getTitle()), mm);
        }
        
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(DELETE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, list.getTitle()), mm);
    }
    
    @RequestMapping(value=URL.LIST, method=RequestMethod.GET)
    public ModelAndView viewList(
            @RequestParam(value=URLParam.ID, required=false) Integer id) {
        ModelMap mm = new ModelMap();
        com.filth.model.List list = null;
        
        //existing list
        if (null != id) {
            list =  _listService.getListById(id);
        }
        //new list
        else {
            list = new com.filth.model.List();
            list.setTitle(NEW_LIST_DEFAULT_TITLE);
            _listService.saveList(list);
        }
        
        JSONObject listJSON = _listJSONTranslator.toJSON(list);
        
        //get all movies to show in the editing panel
        List<Movie> movies = _movieService.getAllMovies();
        
        mm.put(ModelKey.LIST, list);
        mm.put(ModelKey.LIST_JSON, listJSON.toString());
        mm.put(ModelKey.MOVIES, movies);
        
        return new ModelAndView(ADMIN_VIEW_PREFIX + "/view_list", mm);
    }

    @SkipInterceptor({BackgroundImageInterceptor.class})
    @RequestMapping(value=URL.REMOVE_MOVIE, method=RequestMethod.POST)
    public ModelAndView removeMovieFromList(
            @RequestParam(value=URLParam.ID) Integer listId,
            @RequestParam(value=URLParam.MOVIE_ID) Integer movieId) {
        String title = "[unknown title]";
        com.filth.model.List list = null;
        
        try {
            list = _listService.getListById(listId);
            title = list.getTitle();
            
            //remove the movie from the list
            Iterator<ListMovie> listMovieIterator = list.getListMovies().iterator();
            while (listMovieIterator.hasNext()) {
                ListMovie listMovie = listMovieIterator.next();
                if (listMovie.getMovie().getId() == movieId) {
                    listMovieIterator.remove();
                    _listMovieService.deleteListMovie(listMovie);
                    break;
                }
            }
            
            _listService.saveList(list);
        } catch (Exception e) {
            LOGGER.error(String.format(SAVE_ERROR_LOG_MESSAGE_FORMAT, ENTITY_NAME, title), e);
            return _modelAndViewUtil.createErrorJsonModelAndView(
                    String.format(SAVE_ERROR_MESSAGE_FORMAT, ENTITY_NAME, title), new ModelMap());
        }
        
        ModelMap mm = new ModelMap();
        mm.put(ModelKey.LIST, list);
        return _modelAndViewUtil.createSuccessJsonModelAndView(
                String.format(SAVE_SUCCESS_MESSAGE_FORMAT, ENTITY_NAME, title), mm);
    }

}
