<@layout.standard "FiLTH Admin: View List">
    <#-- css for datatables AND its Select extension-->
    <@util.external_css "https://cdn.datatables.net/t/dt/dt-1.10.11,se-1.1.2/datatables.min.css" />
    <@util.css "admin" />
    
    <h1>${list.title}</h1>
    
    <div id="loadingText"><h1>Loading...</h1></div>
    
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
                    <td>
                        <#if listMovie.rank??>
                            ${listMovie.rank}
                        </#if>
                        <input class="hidden" type="text" <#if listMovie.rank??>value="${listMovie.rank}"</#if>>
                    </td>
                    <td>
                        <#if listMovie.comments??>
                            ${listMovie.comments}
                        </#if>
                    </td>
                    <td><a class="button redButton removeButton">Remove</a></td>
                </tr>
            </#list>
        </tbody>
    </table>
    
    <#-- js for datatables AND its Select extension-->
    <@util.external_js "https://cdn.datatables.net/t/dt/dt-1.10.11,se-1.1.2/datatables.min.js" />
    <@util.js "admin/viewList" />
</@layout.standard>