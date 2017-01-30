<#-- define the header to be passed into the layout -->
<#assign headerContent>
    <#-- for retrieval of list id by jquery if needed -->
    <div id="listId" class="hidden" data-list-id="${list.id}"></div>
    
    <div id="listHeader" class="header">
        <div id="loadingText"><h1>Loading...</h1></div>
        
        <div id="listHeaderLeft" class="left">
            <div id="listTitle">
                <#assign title="[title]"/>
                <#if list.title??>
                    <#assign title="${list.title}"/>
                </#if>
                
                <span id="listTitleDisplay" class="contentDisplay">${title}</span>
                <input id="listTitleEdit" class="contentInput" type="text" value="${title}">
            </div>
            
            <div id="listAuthor">
                <#if list.author??>
                    <span id="listAuthorDisplay" class="contentDisplay">${list.author}</span>
                <#else>
                    <span id="listAuthorDisplay" class="noAuthor contentDisplay">[no author]</span>
                </#if>
                <input id="listAuthorEdit" class="contentInput" type="text" <#if list.author??>value="${list.author}"</#if>>
            </div>
        </div>
        
        <div id="listHeaderRight" class="right">
            <div id="editButtonContainer" class="button editButton">
                <a id="editButtonLink">Edit</a>
            </div>
            <div id="backToListsButtonContainer" class="button linkBlueButton">
                <a href="${links.getLinkToManageLists()}">Back to Lists</a>
            </div>
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
    
    <main class="cd-main-content">
        <table id="listMoviesTable" class="hidden">
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
                                <#-- hidden field in order to sort the table by rank -->
                                <div class="rankValue hidden"><#if listMovie.rank??>${listMovie.rank}</#if></div>
                                <input class="rankInput" type="text" <#if listMovie.rank??>value="${listMovie.rank}"</#if>>
                            </td>
                            <td class="commentsColumn">
                                <#-- hidden field in order to revert comment back to original value if aborting edit -->
                                <div class="commentsValue hidden"><#if listMovie.comments??>${listMovie.comments}</#if></div>
                                <input class="commentsInput" type="text" <#if listMovie.comments??>value="${listMovie.comments}"</#if>>
                            </td>
                            <td>
                                <a class="button redButton circleButton arialBlack white" title="Remove from list"
                                   href="javascript: removeFromList('${links.getLinkToRemoveMovieFromList(list.id, movie.id)}', ${movie.id})">X</a>
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
    </main>
    
    <div id="editPanel" class="mCustomScrollbar hidden" data-mcs-theme="minimal-dark">
        <header>
            <h1>Movies</h1>
        </header>
        
        <div id="editPanelContainer">
            <div id="editPanelContent">
                <table id="editListMoviesTable">
                    <thead>
                        <tr>
                            <th></th>
                            <th>Title</th>
                            <th>Year</th>
                            <th>Star Rating</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#foreach movie in movies>
                            <#assign rowCssClass = "odd" />
                            <#if movie_index % 2 == 0>
                                <#assign rowCssClass = "even" />
                            </#if>
                            
                            <tr data-movie-id="${movie.id}" class="${rowCssClass}">
                                <td class="movieCheckboxContainer">
                                    <input type="checkbox" class="movieCheckbox">
                                </td>
                                <td><a class="movieTitle movieLink" data-remodal-target="movieModal" data-movie-id="${movie.id}">${movie.title}</a></td>
                                <td class="movieYear">
                                    <#if movie.year??>
                                        ${movie.year}
                                    </#if>
                                </td>
                                <td class="movieStarRating">
                                    <#if movie.starRating??>
                                        ${movie.starRatingForDisplay}
                                    </#if>
                                </td>
                            </tr>
                        </#foreach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <#-- js for datatables AND its Select extension-->
    <@util.external_js "https://cdn.datatables.net/t/dt/dt-1.10.11,se-1.1.2/datatables.min.js" />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "admin/viewList" />
    <@util.js "admin/MovieList" />
    <@util.js "admin/MovieListChanges" />
    
    <script type="text/javascript">
        <#-- pass along the JSON representation of the list to javascript for manipulation during editing -->
        var movieList = new MovieList('${listJSON}');
        <#-- pass along the url to remove a movie from the list so javascript can use it -->
        var removeMovieFromListUrl = '${links.getLinkToRemoveMovieFromList()}';
    </script>
</@layout.standard>