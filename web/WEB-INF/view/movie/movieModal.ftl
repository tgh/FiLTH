<button data-remodal-action="close" class="remodal-close"></button>

<h3>${movie.title} (${movie.year})</h3>

<p>${movie.starRatingForDisplay}</p>

<p>MPAA Rating: ${movie.mpaaRating}</p>

<p>Country: ${movie.country}</p>

<p>How many times seen in the theater: ${movie.theaterViewings}<p>

<#if movie.comments??>
    <p>Comments: ${movie.comments}</p>
</#if>

<@renderCrew />

<a href="http://www.imdb.com/title/${movie.imdbId}/?ref_=fn_al_tt_1" target="_blank">View in IMDB.com</a>

<#macro renderCrew>
    <#if movie.movieCrewPersons??>
        <#local directors = movie.director />
        <#if (directors?size > 0)>
            <#if directors?size == 1>
                <p>Director: ${directors[0].fullName}</p>
            <#else>
                <p>Directors:
                <#list directors as director>
                    ${director.fullName}&nbsp;&nbsp;
                </#list>
            </#if>
        </#if>
        
        <#local screenWriters = movie.screenWriters />
        <#if (screenWriters?size > 0)>
            <#if screenWriters?size == 1>
                <p>Screen Writer: ${screenWriters[0].fullName}</p>
            <#else>
                <p>Screen Writers:
                <#list screenWriters as screenWriter>
                    ${screenWriter.fullName}&nbsp;&nbsp;
                </#list>
            </#if>
        </#if>
        
        <#local cinematographers = movie.cinematographer />
        <#if (cinematographers?size > 0)>
            <#if cinematographers?size == 1>
                <p>Cinematographer: ${cinematographers[0].fullName}</p>
            <#else>
                <p>Cinematographers:
                <#list cinematographers as cinematographer>
                    ${cinematographer.fullName}&nbsp;&nbsp;
                </#list>
            </#if>
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