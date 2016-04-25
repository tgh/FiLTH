<button data-remodal-action="close" class="remodal-close"></button>

<#if movie.year??>
    <h3>${movie.title} (${movie.year})</h3>
<#else>
    <h3>${movie.title}</h3>
</#if>

<#if movie.starRatingForDisplay??>
    <p>${movie.starRatingForDisplay}</p>
</#if>

<#if movie.mpaaRating??>
    <p>MPAA Rating: ${movie.mpaaRating}</p>
</#if>

<#if movie.country??>
    <p>Country: ${movie.country}</p>
</#if>

<#if movie.theaterViewings??>
    <p>How many times seen in the theater: ${movie.theaterViewings}<p>
</#if>

<#if movie.comments??>
    <p>Comments: ${movie.comments}</p>
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
    <p>Oscars:
    <#list movie.movieOscars as movieOscar>
        ${movieOscar.status.displayText} for ${movieOscar.oscar.category}</br>
    </#list>
    </p>
</#if>

<#if (movie.movieTylers?? && movie.movieTylers?size > 0)>
    <p>Tyler awards:
    <#list movie.movieTylers as movieTyler>
        ${movieTyler.status.displayText} for ${movieTyler.tyler.category}</br>
    </#list>
    </p>
</#if>

<@renderCrew />

<#if movie.imdbId??>
    <p>
        <a href="http://www.imdb.com/title/${movie.imdbId}/?ref_=fn_al_tt_1" target="_blank">View in IMDB.com</a>
    </p>
</#if>

<#macro renderCrew>
    <#if movie.movieCrewPersons??>
        <#local directors = movie.director />
        <#if (directors?size > 0)>
            <p>
            <#if directors?size == 1>
                Director: ${directors[0].fullName}
            <#else>
                Directors:
                <#list directors as director>
                    ${director.fullName}&nbsp;&nbsp;
                </#list>
            </#if>
            </p>
        </#if>
        
        <#local screenWriters = movie.screenWriters />
        <#if (screenWriters?size > 0)>
            <p>
            <#if screenWriters?size == 1>
                Screen Writer: ${screenWriters[0].fullName}
            <#else>
                Screen Writers:
                <#list screenWriters as screenWriter>
                    ${screenWriter.fullName}&nbsp;&nbsp;
                </#list>
            </#if>
            </p>
        </#if>
        
        <#local cinematographers = movie.cinematographer />
        <#if (cinematographers?size > 0)>
            <p>
            <#if cinematographers?size == 1>
                Cinematographer: ${cinematographers[0].fullName}
            <#else>
                Cinematographers:
                <#list cinematographers as cinematographer>
                    ${cinematographer.fullName}&nbsp;&nbsp;
                </#list>
            </#if>
            </p>
        </#if>
        
        <#local actors = movie.actors />
        <#if (actors?size > 0)>
            <p>Acting crew:
            <#list actors?keys as key>
                <p>${key} (${actors[key]})</p>
            </#list>
            </p>
        </#if>
    </#if>
</#macro>