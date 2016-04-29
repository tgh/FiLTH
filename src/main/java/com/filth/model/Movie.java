package com.filth.model;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieImages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.filth.util.TmdbUtil;

@Entity
@Table(name="movie")
public class Movie {
    
    private static final String TMDB_IMAGE_URL_FORMAT = "https://image.tmdb.org/t/p/w396/%s";

    @Id
    @Column(name="mid")
    @SequenceGenerator(name="movie_seq", sequenceName="movie_mid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="movie_seq")
    private int _id;
  
    @Column(name="title")
    private String _title;
  
    @Column(name="year")
    private Integer _year;
  
    @Column(name="star_rating")
    private String _starRating;
  
    @Column(name="mpaa")
    private String _mpaa;
  
    @Column(name="country")
    private String _country;
  
    @Column(name="comments")
    private String _comments;
  
    @Column(name="imdb_id")
    private String _imdbId;
  
    @Column(name="theater_viewings")
    private Integer _theaterViewings;
  
    @Column(name="tmdb_id")
    private Long _tmdbId;
    
    //do not cascade on REMOVE (don't want to delete all of the tags if the movie is deleted)
    @ManyToMany(cascade={CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "tag_given_to",
            joinColumns = {@JoinColumn(name = "mid")},
            inverseJoinColumns = {@JoinColumn(name = "tid")}
    )
    private Set<Tag> _tags;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="_movie")
    private Set<MovieCrewPerson> _movieCrewPersons;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="_movie")
    private Set<MovieOscar> _movieOscars;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="_movie")
    private Set<MovieTyler> _movieTylers;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="_movie")
    private Set<ListMovie> _listMovies;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="_baseMovie")
    private Set<MovieLink> _movieLinksFromThisMovie;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="_linkedMovie")
    private Set<MovieLink> _movieLinksToThisMovie;

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }
  
    public Integer getYear() {
        return _year;
    }
  
    public void setYear(Integer year) {
        _year = year;
    }
  
    public String getStarRating() {
        return _starRating;
    }
  
    public void setStarRating(String starRating) {
        _starRating = starRating;
    }
  
    public String getMpaaRating() {
        return _mpaa;
    }
  
    public void setMpaaRating(String mpaa) {
        _mpaa = mpaa;
    }
  
    public String getCountry() {
        return _country;
    }
  
    public void setCountry(String country) {
        _country = country;
    }
  
    public String getComments() {
        return _comments;
    }
  
    public void setComments(String comments) {
        _comments = comments;
    }
  
    public String getImdbId() {
        return _imdbId;
    }
  
    public void setImdbId(String imdbId) {
        _imdbId = imdbId;
    }
  
    public Integer getTheaterViewings() {
        return _theaterViewings;
    }
    
    public void setTheaterViewings(Integer viewings) {
        _theaterViewings = viewings;
    }
  
    public Long getTmdbId() {
        return _tmdbId;
    }
  
    public void setTmdbId(Long tmdbId) {
        _tmdbId = tmdbId;
    }
    
    public Set<Tag> getTags() {
        return _tags;
    }
    
    public void setTags(Set<Tag> tags) {
        _tags = tags;
    }
    
    public Set<MovieCrewPerson> getMovieCrewPersons() {
        return _movieCrewPersons;
    }
    
    public void setMovieCrewPersons(Set<MovieCrewPerson> movieCrewPersons) {
        _movieCrewPersons = movieCrewPersons;
    }
    
    public Set<MovieOscar> getMovieOscars() {
        return _movieOscars;
    }
    
    public void setMovieOscars(Set<MovieOscar> movieOscars) {
        _movieOscars = movieOscars;
    }
    
    public Set<MovieTyler> getMovieTylers() {
        return _movieTylers;
    }
    
    public void setMovieTylers(Set<MovieTyler> movieTylers) {
        _movieTylers = movieTylers;
    }
    
    public Set<ListMovie> getListMovies() {
        return _listMovies;
    }
    
    public void setListMovies(Set<ListMovie> listMovies) {
        _listMovies = listMovies;
    }
    
    public Set<MovieLink> getMovieLinksFromThisMovie() {
        return _movieLinksFromThisMovie;
    }
    
    public void setMovieLinksFromThisMovie(Set<MovieLink> movieLinks) {
        _movieLinksFromThisMovie = movieLinks;
    }
    
    public Set<MovieLink> getMovieLinksToThisMovie() {
        return _movieLinksToThisMovie;
    }
    
    public void setMovieLinksToThisMovie(Set<MovieLink> movieLinks) {
        _movieLinksToThisMovie = movieLinks;
    }
    
    public String getStarRatingForDisplay() throws Exception {
        if (null == _starRating) {
            return null;
        }
        return StarRating.encodeToHTML(_starRating);
    }
    
    /**
     * Gets the director of this movie. However, a Set is returned due to there
     * being movies with multiple directors. It is the caller's responsibility
     * to handle the single-director case.
     * 
     * @return Set of {@link CrewPerson} objects (usually a set of one), or an empty
     * set if none found.
     */
    @Transient
    public Set<CrewPerson> getDirector() {
        return getCrewForPosition("Director");
    }
    
    /**
     * Gets the screen-writers of this movie.
     * 
     * @return Set of {@link CrewPerson} objects, or an empty set if none found.
     */
    @Transient
    public Set<CrewPerson> getScreenWriters() {
        return getCrewForPosition("Screenwriter");
    }
    
    /**
     * Gets the cinematographer of this movie. However, a Set is returned due the
     * rare cases of movies with multiple cinematographers. It is the caller's
     * responsibility to handle the single-cinematographer case.
     * 
     * @return Set of {@link CrewPerson} objects (usually a set of one), or an empty
     * set if none found.
     */
    @Transient
    public Set<CrewPerson> getCinematographer() {
        return getCrewForPosition("Cinematographer");
    }
    
    /**
     * Gets the acting crew members of this movie.
     * 
     * @return A String (full name) -> String (position) map, or an empty map
     * if none found.
     */
    @Transient
    public Map<String, String> getActors() {
        Map<String, String> actorToPositionMap = new HashMap<>();
        
        if (CollectionUtils.isNotEmpty(_movieCrewPersons)) {
            for (MovieCrewPerson movieCrewPerson : _movieCrewPersons) {
                String position = movieCrewPerson.getPosition();
                CrewPerson crewPerson = movieCrewPerson.getCrewPerson();
                
                switch(position) {
                    case "Lead Actor":
                    case "Supporting Actor":
                    case "Lead Actress":
                    case "Supporting Actress":
                    case "Character Voice":
                    case "Small Part":
                    case "Cameo":
                        actorToPositionMap.put(crewPerson.getFullName(), position);
                    default:
                        continue;
                }
            }
        }
        
        return actorToPositionMap;
    }
    
    /**
     * Gets TMDB url for a poster image of this movie. If this movie does not have a TMDB
     * id, then null is returned. If TMDB does not return any images for this movie,
     * null is also returned.
     * <p>
     * TMDB api returns a list of poster images, so we are arbitrarily taking the first one.
     */
    @Transient
    public String getImageUrl() {
        if (StringUtils.isEmpty(getTmdbId())) {
            return null;
        }
        
        String apiKey = TmdbUtil.getTmdbApiKey();
        TmdbApi api = new TmdbApi(apiKey);
        TmdbMovies movies = api.getMovies();
        MovieImages images = movies.getImages(Math.toIntExact(getTmdbId()), "en");
        List<Artwork> posters = images.getPosters();
        if (CollectionUtils.isEmpty(posters)) {
            return null;
        }
        
        //arbitrarily take the first one
        String filePath = posters.get(0).getFilePath();
        
        return String.format(TMDB_IMAGE_URL_FORMAT, filePath);
    }
    
    /**
     * Gets a String (oscar category) -> Set<MovieOscar> map for all oscars
     * this movie was nominated for. Simply calling getMovieOscars would include
     * multiple MovieOscar objects for the same oscar nomination when the nomination
     * involves multiple recipients. Hence, this method maps the categories to their
     * corresponding sets of MovieOscar objects.
     */
    @Transient
    public Map<String, Set<MovieOscar>> getOscarToMovieOscarsMap() {
        Map<String, Set<MovieOscar>> oscarMap = new HashMap<>();
        if (null != _movieOscars) {
            for (MovieOscar movieOscar : _movieOscars) {
                String category = movieOscar.getOscar().getCategory();
                Set<MovieOscar> movieOscars = oscarMap.get(category);
                if (null == movieOscars) {
                    movieOscars = new HashSet<>();
                    oscarMap.put(category, movieOscars);
                }
                movieOscars.add(movieOscar);
            }
        }
        return oscarMap;
    }
  
    @Override
    public String toString() {
        return "Movie (" + _id + ", " + _title + " (" + _year + "))";
    }
    
    private Set<CrewPerson> getCrewForPosition(String position) {
        Set<CrewPerson> crew = new HashSet<>();
        
        if (CollectionUtils.isEmpty(_movieCrewPersons)) {
            return crew;
        }
        
        for (MovieCrewPerson movieCrewPerson : _movieCrewPersons) {
            if (movieCrewPerson.getPosition().equals(position)) {
                CrewPerson crewPerson = movieCrewPerson.getCrewPerson();
                crew.add(crewPerson);
            }
        }
        
        return crew;
    }
}