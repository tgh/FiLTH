<#import "/util.ftl" as util />

<div id="modalLoadingText" class="hidden"><h1>Loading...</h1></div>

<div id="modalBody">
    <div id="modalLeft">
        <#if movie.imageUrl??>
            <div id="modalMovieImageContainer">
                <img id="modalMovieImage" src="${movie.imageUrl}"/>
            </div>
        </#if>
        
        <#assign oscarMap = movie.oscarToMovieOscarsMap />
        <#if (oscarMap?? && oscarMap?size > 0)>
            <p>
                <span class="modalLabel">Oscars:</span>
            </p>
            <ul>
                <#list oscarMap?keys as category>
                    <li>${oscarMap[category][0].status.displayText} for ${category}
                    
                    <#-- Show recipients if applicable -->
                    <#if false == oscarMap[category][0].crewPerson.isDummy()>
                        <i>(<#list oscarMap[category] as movieOscar>${movieOscar.crewPerson.fullName}<#if movieOscar_has_next>,</#if></#list>)</i>
                    </#if>
                    
                    </li>
                </#list>
            </ul>
        </#if>
        
        <@renderCrew />
    </div>
    
    <div id="modalRight">
        <#if movie.year??>
            <div id="modalMovieTitle">${movie.title} (${movie.year})</div>
        <#else>
            <div id="modalMovieTitle">${movie.title}</div>
        </#if>
        
        <#if movie.starRatingForDisplay??>
            <div id="modalMovieStarRating">${movie.starRatingForDisplay}</div>
        </#if>
        
        <#if movie.mpaaRating??>
            <p id="modalMovieMpaa"><span class="modalLabel">MPAA Rating:</span> ${movie.mpaaRating}</p>
        </#if>
        
        <#if movie.country??>
            <p id="modalMovieCountry"><span class="modalLabel">Country:</span> ${movie.country}</p>
        </#if>
        
        <#if movie.theaterViewings??>
            <p id="modalMovieTheaterViewings"><span class="modalLabel">How many times seen in the theater:</span> ${movie.theaterViewings}<p>
        </#if>
        
        <#if movie.comments??>
            <p id="modalMovieComments"><span class="modalLabel">Comments:</span> ${movie.comments}</p>
        </#if>
        
        <#if movie.tags??>
            <div id="modalMovieTags">
                <span class="modalLabel">Tags:</span>
                
                <#list movie.tags as tag>
                    ${tag.name}<#if tag_has_next>,</#if>
                </#list>
            </div>
        </#if>
        
        <#if movie.listMovies??>
            <#list movie.listMovies as listMovie>
                <p>
                <#if listMovie.rank??>
                    #${listMovie.rank} in 
                <#else>
                    In 
                </#if>
                <i>"${listMovie.list.title}"</i>
                <#if listMovie.list.author??>
                    by ${listMovie.list.author}
                </#if>
                </p>
            </#list>
        </#if>
        
        <#assign tylerMap = movie.tylerToMovieTylersMap />
        <#if (tylerMap?? && tylerMap?size > 0)>
            <p>
                <span class="modalLabel">Tyler awards:</span>
            </p>
            <ul>
                <#list tylerMap?keys as category>
                    <li>${tylerMap[category][0].status.displayText} for ${category}
                    
                    <#-- Show recipients if applicable -->
                    <#if false == tylerMap[category][0].crewPerson.isDummy()>
                        <i>(<#list tylerMap[category] as movieTyler>${movieTyler.crewPerson.fullName}<#if movieTyler_has_next>,</#if></#list>)</i>
                    <#elseif category == 'Best Scene'>
                        <i>(<#list tylerMap[category] as movieTyler>"${movieTyler.sceneTitle}"<#if movieTyler_has_next>,</#if></#list>)</i>
                    </#if>
                    
                    </li>
                </#list>
            </ul>
        </#if>
        
        <#if (movie.movieLinks?size > 0)>
            <div class="horizontalDivider"></div>
        
            <div id="modalRelatedMovies">
                <span class="modalLabel">Movies related to this movie:</span>
                <#list movie.movieLinks as movieLink>
                    <table id="modalRelatedMoviesTable">
                        <#if movie.id == movieLink.baseMovie.id>
                            <tr>
                                <#if movieLink.linkedMovie.imageUrl??>
                                    <td><img class="modalMovieTableImage" src="${movieLink.linkedMovie.imageUrl}"/></td>
                                <#else>
                                    <td><@util.noImage "modalMovieTableImage" "${movieLink.linkedMovie.title} (${movieLink.linkedMovie.year})" /></td>
                                </#if>
                                <td class="modalRelatedMovieDiscription">
                                    <a class="movieLink" data-remodal-target="movieModal" data-movie-id="${movieLink.linkedMovie.id}">"${movieLink.linkedMovie.title}" (${movieLink.linkedMovie.year})</a>: ${movieLink.description}
                                </td>
                            </tr>
                        <#else>
                            <tr>
                                <#if movieLink.baseMovie.imageUrl??>
                                    <td><img class="modalMovieTableImage" src="${movieLink.baseMovie.imageUrl}"/></td>
                                <#else>
                                    <td><@util.noImage "modalMovieTableImage" "${movieLink.baseMovie.title} (${movieLink.baseMovie.year})" /></td>
                                </#if>
                                <td class="modalRelatedMovieDiscription">
                                    <a class="movieLink" data-remodal-target="movieModal" data-movie-id="${movieLink.baseMovie.id}">"${movieLink.baseMovie.title}" (${movieLink.baseMovie.year})</a>: ${movieLink.description}
                                </td>
                            </tr>
                        </#if>
                    </table>
                </#list>
            </div>
        </#if>
        
        <#assign sequenceMovies = movie.movieSequenceMovies />
        <#if (sequenceMovies?? && sequenceMovies?size > 0)>
            <div class="horizontalDivider"></div>
            
            <#list sequenceMovies as sequenceMovie>
                <p>
                    <span class="modalLabel">The ${util.number_suffix(sequenceMovie.orderIndex)} film in ${sequenceMovie.sequence.name}:</span>
                </p>
                <p class="modalSequence">
                    <#list sequenceMovie.sequence.getMoviesInOrder() as sm>
                        <#if sm.imageUrl??>
                            <#if sm.id == movie.id>
                                <img class="modalMovieImageSmall imageHighlight" src="${sm.imageUrl}"/>
                            <#else>
                                <img class="modalMovieImageSmall" src="${sm.imageUrl}"/>
                            </#if>
                        <#else>
                            <@util.noImage "modalMovieImageSmall" "${sm.title} (${sm.year})" />
                        </#if>
                    </#list>
                </p>
            </#list>
        </#if>
    </div>
</div>

<div id="modalFooter">
    <#if movie.imdbId??>
        <div id="modalMovieImdb">
            <a id="modalMovieImdbLink" href="http://www.imdb.com/title/${movie.imdbId}/?ref_=fn_al_tt_1" target="_blank">View in IMDB.com</a>
        </div>
    </#if>
</div>

<#-- macros -->

<#macro renderCrew>
    <#if movie.movieCrewPersons??>
        <div id="modalMovieCrew">
            <#local directors = movie.director />
            <#if (directors?size > 0)>
                <p>
                <#if directors?size == 1>
                    <span class="modalLabel">Director:</span> ${directors[0].fullName}
                <#else>
                    <span class="modalLabel">Directors:</span>
                    <#list directors as director>
                        ${director.fullName}<#if director_has_next>,</#if>
                    </#list>
                </#if>
                </p>
            </#if>
            
            <#local screenWriters = movie.screenWriters />
            <#if (screenWriters?size > 0)>
                <p>
                <#if screenWriters?size == 1>
                    <span class="modalLabel">Screen Writer:</span> ${screenWriters[0].fullName}
                <#else>
                    <span class="modalLabel">Screen Writers:</span>
                    <#list screenWriters as screenWriter>
                        ${screenWriter.fullName}<#if screenWriter_has_next>,</#if>
                    </#list>
                </#if>
                </p>
            </#if>
            
            <#local cinematographers = movie.cinematographer />
            <#if (cinematographers?size > 0)>
                <p>
                <#if cinematographers?size == 1>
                    <span class="modalLabel">Cinematographer:</span> ${cinematographers[0].fullName}
                <#else>
                    <span class="modalLabel">Cinematographers:</span>
                    <#list cinematographers as cinematographer>
                        ${cinematographer.fullName}<#if cinematographer_has_next>,</#if>
                    </#list>
                </#if>
                </p>
            </#if>
            
            <#local actors = movie.actors />
            <#if (actors?size > 0)>
                <div id="modalMovieActingCrew">
                    <p>
                        <span class="modalLabel">Acting crew:</span>
                    </p>
                    <ul>
                        <#list actors?keys as fullName>
                            <li>${fullName} (${actors[fullName]})</li>
                        </#list>
                    </ul>
                </div>
            </#if>
        </div>
    </#if>
</#macro>