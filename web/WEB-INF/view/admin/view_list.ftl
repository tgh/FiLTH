<#-- define the header to be passed into the layout -->
<#assign headerContent>
    <div id="listHeader" class="header">
        <div id="listTitle">
            <#assign title="[title]"/>
            <#if list.title??>
                <#assign title="${list.title}"/>
            </#if>
            
            <span id="listTitleDisplay" class="contentDisplay">${title}</span>
            <input id="listTitleEdit" class="hidden contentInput" type="text" value="${title}">
        </div>
        
        <div id="by">
            <span <#if ! list.author??>class="hidden"</#if>>by</span>
        </div>
        
        <div id="listAuthor">
            <#if list.author??>
                <span id="listAuthorDisplay" class="contentDisplay">${list.author}</span>
            <#else>
                <span id="listAuthorDisplay" class="noAuthor contentDisplay">[no author]</span>
            </#if>
            <input id="listAuthorEdit" class="hidden contentInput" type="text" <#if list.author??>value="${list.author}"</#if>>
        </div>
        
        <div id="loadingText"><h1>Loading...</h1></div>
        
        <div id="backToListsLink" class="button linkBlueButton">
            <a href="${links.getLinkToManageLists()}">Back to Lists</a>
        </div>
        
        <#-- Add movie button -- NOT YET FUNCTIONAL -->
        <div id="addMovieLink">
            <a data-remodal-target="addMovieModal" class="addButton button">Add Movie</a>
        </div>
    </div>
</#assign>


<@layout.standard "FiLTH Admin: View List" headerContent>
    <#-- css for datatables AND its Select extension-->
    <@util.external_css "https://cdn.datatables.net/t/dt/dt-1.10.11,se-1.1.2/datatables.min.css" />
    <@util.css "admin" />
    <@util.css "list/list" />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    
    <table id="moviesTable" class="hidden">
        <thead>
            <tr>
                <th></th>
                <th>Movie</th>
                <th>Rank</th>
                <th>Comments</th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <#if list.listMovies??>
                <#list list.listMovies as listMovie>
                    <#assign movie = listMovie.movie />
                    
                    <#assign rowCssClass = "odd" />
                    <#if listMovie?index % 2 == 0>
                        <#assign rowCssClass = "even" />
                    </#if>
                    
                    <tr data-movie-id="${movie.id}" class="${rowCssClass}">
                        <td>
                            <#-- check mark for movies seen -->
                            <#if movie.starRating?? && movie.starRating != "not seen">
                                &#x2714;
                            </#if>
                        </td>
                        <td>
                            <a class="movieTitle movieLink" data-remodal-target="movieModal" data-movie-id="${movie.id}">${movie.title} <#if movie.year??>(${movie.year})</#if></a>
                        </td>
                        <td class="rankColumn">
                            <#if listMovie.rank??>
                                <div class="listRankDisplay contentDisplay">${listMovie.rank}</div>
                            </#if>
                            <input class="hidden listRankEdit contentInput" type="text" <#if listMovie.rank??>value="${listMovie.rank}"</#if>>
                        </td>
                        <td class="commentsColumn">
                            <div class="listCommentsDisplay contentDisplay"><#if listMovie.comments??>${listMovie.comments}</#if></div>
                            <input class="hidden listCommentsEdit contentInput" type="text" <#if listMovie.comments??>value="${listMovie.comments}"</#if>>
                        </td>
                        <td><a class="button redButton removeButton"
                               href="javascript: removeFromList('${links.getLinkToRemoveMovieFromList(list.id, movie.id)}', ${movie.id})">Remove</a>
                        </td>
                    </tr>
                </#list>
            </#if>
        </tbody>
    </table>
    
    <#-- hidden form for async saving -->
    <form id="saveListForm" action="${links.getLinkToSaveList()}" method="POST">
        <input id="listJSONInput" type="hidden" name="listJSON">
    </form>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- js for datatables AND its Select extension-->
    <@util.external_js "https://cdn.datatables.net/t/dt/dt-1.10.11,se-1.1.2/datatables.min.js" />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "admin/viewList" />
    <@util.js "admin/MovieList" />
    
    <#-- pass along the JSON representation of the list to javascript for manipulation during editing -->
    <script type="text/javascript">
        var movieList = new MovieList('${listJSON}');
    </script>
</@layout.standard>