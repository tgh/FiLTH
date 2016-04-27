<button data-remodal-action="close" class="remodal-close"></button>

<#if movie.imageUrl??>
    <img id="modalMovieImage" src="${movie.imageUrl}"/>
</#if>

<#if movie.year??>
    <p id="modalMovieTitle">${movie.title} (${movie.year})</p>
<#else>
    <p id="modalMovieTitle">${movie.title}</p>
</#if>

<#if movie.starRatingForDisplay??>
    <p id="modalMovieStarRating">${movie.starRatingForDisplay}</p>
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
    <p><span class="modalLabel">Tags:</span>
    <#list movie.tags as tag>
        ${tag.name}<#if tag_has_next>,</#if>
    </#list>
    </p>
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

<#if (movie.movieOscars?? && movie.movieOscars?size > 0)>
    <p>
        <span class="modalLabel">Oscars:</span>
        <#list movie.movieOscars as movieOscar>
            ${movieOscar.status.displayText} for ${movieOscar.oscar.category}</br>
        </#list>
    </p>
</#if>

<#if (movie.movieTylers?? && movie.movieTylers?size > 0)>
    <p>
        <span class="modalLabel">Tyler awards:</span>
        <#list movie.movieTylers as movieTyler>
            ${movieTyler.status.displayText} for ${movieTyler.tyler.category}</br>
        </#list>
    </p>
</#if>

<#if (movie.movieLinksToThisMovie?? && movie.movieLinksToThisMovie?size > 0)>
    <p>
        <span class="modalLabel">Movies linked to this movie:</span>
        <#list movie.movieLinksToThisMovie as movieLink>
            ${movieLink.baseMovie.title} (${movieLink.baseMovie.year})<#if movieLink_has_next>,</#if>
        </#list>
    </p>
</#if>

<#if (movie.movieLinksFromThisMovie?? && movie.movieLinksFromThisMovie?size > 0)>
    <p>
        <span class="modalLabel">Movies linked from this movie:</span>
        <#list movie.movieLinksFromThisMovie as movieLink>
            ${movieLink.linkedMovie.title} (${movieLink.linkedMovie.year})<#if movieLink_has_next>,</#if>
        </#list>
    </p>
</#if>

<@renderCrew />

<#if movie.imdbId??>
    <div id="modalMovieImdb">
        <a id="modalMovieImdbLink" href="http://www.imdb.com/title/${movie.imdbId}/?ref_=fn_al_tt_1" target="_blank">View in IMDB.com</a>
    </div>
</#if>

<#-- macros -->

<#macro renderCrew>
    <#if movie.movieCrewPersons??>
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
            <p>
                <span class="modalLabel">Acting crew:</span>
                <#list actors?keys as key>
                    <p>${key} (${actors[key]})</p>
                </#list>
            </p>
        </#if>
    </#if>
</#macro>