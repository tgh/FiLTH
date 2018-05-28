package com.filth.model;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieImages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.filth.util.CrewPersonLastNameComparator;
import com.filth.util.TmdbUtil;

@Entity
@Table(name="movie")
public class Movie {

    private static final Logger LOGGER = LoggerFactory.getLogger(Movie.class);

    private static final String TMDB_IMAGE_URL_FORMAT = "https://image.tmdb.org/t/p/w342/%s";

    private static final List<String> ACTING_POSITION_ORDER = Arrays.asList(new String[] {
        "Lead Actor",
        "Lead Actress",
        "Supporting Actor",
        "Supporting Actress",
        "Character Voice",
        "Small Part",
        "Narrator",
        "Cameo"
    });

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

    @Column(name="runtime")
    private Integer _runtime;

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

    @OneToMany(cascade=CascadeType.ALL, mappedBy="_movie")
    private Set<MovieSequenceMovie> _movieSequenceMovies;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "parent_mid")
    private Movie _parentMovie;

    @OneToMany(mappedBy="_parentMovie")
    private Set<Movie> _childMovies;

    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "remake_of_mid")
    private Movie _remakeOfMovie;

    @OneToMany(mappedBy="_remakeOfMovie")
    private Set<Movie> _remadeByMovies;

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

    public Integer getRuntime() {
    	return _runtime;
    }

    public void setRuntime(Integer runtime) {
    	_runtime = runtime;
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

    public void setMovieLinksFromThisMovie(Set<MovieLink> movieLinks) {
        _movieLinksFromThisMovie = movieLinks;
    }

    public void setMovieLinksToThisMovie(Set<MovieLink> movieLinks) {
        _movieLinksToThisMovie = movieLinks;
    }

    public Set<MovieSequenceMovie> getMovieSequenceMovies() {
        return _movieSequenceMovies;
    }

    public void setMovieSequenceMovies(Set<MovieSequenceMovie> movieSequenceMovies) {
        _movieSequenceMovies = movieSequenceMovies;
    }

    public Movie getParent() {
        return _parentMovie;
    }

    public void setParent(Movie movie) {
        _parentMovie = movie;
    }

    public Set<Movie> getChildren() {
        return _childMovies;
    }

    public void setChildren(Set<Movie> movies) {
        _childMovies = movies;
    }

    /**
     * @return the movie that this movie is a remake of, or null
     */
    public Movie getRemakeOfMovie() {
        return _remakeOfMovie;
    }

    public void setRemakeOfMovie(Movie movie) {
        _remakeOfMovie = movie;
    }

    /**
     * @return the set of movies that are a remake of this movie, or null
     */
    public Set<Movie> getRemadeByMovies() {
        return _remadeByMovies;
    }

    public void setRemadeByMovies(Set<Movie> movies) {
        _remadeByMovies = movies;
    }

    @Transient
    public String getStarRatingForDisplay() throws Exception {
        if (null == _starRating) {
            return null;
        }
        return StarRating.encodeToHTML(_starRating);
    }

    @Transient
    public Set<MovieLink> getMovieLinks() {
        Set<MovieLink> union = new HashSet<>(_movieLinksFromThisMovie);
        union.addAll(_movieLinksToThisMovie);
        return union;
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
     * if none found. The map is ordered as thus:<br>
     * <ol>
     *     <li>Lead Actor</li>
     *     <li>Lead Actress</li>
     *     <li>Supporting Actor</li>
     *     <li>Supporting Actress</li>
     *     <li>Character Voice</li>
     *     <li>Small Part</li>
     *     <li>Narrator</li>
     *     <li>Cameo</li>
     * </ol><br>
     * where each category is ordered by crew person last name.
     */
    @Transient
    public Map<String, String> getActors() {
        Map<String, String> actorToPositionMap = new LinkedHashMap<>();

        if (CollectionUtils.isNotEmpty(_movieCrewPersons)) {
            for (String position : ACTING_POSITION_ORDER) {
                List<CrewPerson> positionCrewPersons = new ArrayList<>();

                for (MovieCrewPerson movieCrewPerson : _movieCrewPersons) {
                    String positionOfCrewPerson = movieCrewPerson.getPosition();

                    if (position.equals(positionOfCrewPerson)) {
                        CrewPerson crewPerson = movieCrewPerson.getCrewPerson();
                        positionCrewPersons.add(crewPerson);
                    }
                }

                //order crew by last name
                Collections.sort(positionCrewPersons, new CrewPersonLastNameComparator());

                for (CrewPerson crewPerson : positionCrewPersons) {
                    actorToPositionMap.put(crewPerson.getFullName(), position);
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
            LOGGER.warn("No TMDB id for " + this.toString());
            return null;
        }

        String apiKey = TmdbUtil.getTmdbApiKey();
        TmdbApi api = new TmdbApi(apiKey);
        TmdbMovies movies = api.getMovies();
        MovieImages images = movies.getImages(Math.toIntExact(getTmdbId()), "en");
        List<Artwork> posters = images.getPosters();
        if (CollectionUtils.isEmpty(posters)) {
            LOGGER.warn("No TMDB image found for " + this.toString() + "--TMDB id: " + getTmdbId());
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

    /**
     * Gets a String (tyler category) -> Set<MovieTyler> map for all tyler awards
     * this movie was nominated for. Simply calling getMovieTylers would include
     * multiple MovieTyler objects for the same tyler nomination when the nomination
     * involves multiple recipients. Hence, this method maps the categories to their
     * corresponding sets of MovieTyler objects.
     */
    @Transient
    public Map<String, Set<MovieTyler>> getTylerToMovieTylersMap() {
        Map<String, Set<MovieTyler>> tylerMap = new HashMap<>();
        if (null != _movieTylers) {
            for (MovieTyler movieTyler : _movieTylers) {
                String category = movieTyler.getTyler().getCategory();
                Set<MovieTyler> movieTylers = tylerMap.get(category);
                if (null == movieTylers) {
                    movieTylers = new HashSet<>();
                    tylerMap.put(category, movieTylers);
                }
                movieTylers.add(movieTyler);
            }
        }
        return tylerMap;
    }

    /**
     * Return a string representing the color of the star rating.
     * (to be used in a css class name for color-coding the star ratings)
     */
    @Transient
    public String getColorStringForStarRating() {
    	if (null == getStarRating()) {
    		return "defaultColor";
    	}

        switch(getStarRating()) {
            case "****":
                return "green";
            case "***½":
            case "***":
                return "blue";
            case "**½":
            case "**":
                return "purple";
            case "*½":
            case "*":
                return "orange";
            case "½*":
            case "NO STARS":
                return "red";
            default:
                return "defaultColor";
        }
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
