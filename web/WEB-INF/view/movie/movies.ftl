<@layout.standard "FiLTH: Movies">
    <#-- css for datatables AND its Select extension-->
    <@util.external_css "https://cdn.datatables.net/t/dt/dt-1.10.11,se-1.1.2/datatables.min.css" />
    
    <h1>Movies</h1>
    
    <div class="remodal modal" data-remodal-id="movieModal" data-remodal-options="hashTracking: false"></div>
    
    <table id="moviesTable">
        <thead>
            <tr>
                <th>Title</th>
                <th>Year</th>
                <th>Star Rating</th>
                <th>MPAA Rating</th>
                <th>Country</th>
                <th>Times seen in theater</th>
                <th>Comments</th>
            </tr>
        </thead>
        <tbody>
            <#foreach movie in movies>
                <#assign rowCssClass = "odd" />
                <#if movie_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
                
                <tr data-movie-id="${movie.id}" class="${rowCssClass}">
                    <td><a class="movieTitle" data-remodal-target="movieModal">${movie.title}</a></td>
                    <td>
                        <#if movie.year??>
                            ${movie.year}
                        </#if>
                    </td>
                    <td>
                        <#if movie.starRating??>
                            ${movie.starRatingForDisplay}
                        </#if>
                    </td>
                    <td>
                        <#if movie.mpaaRating??>
                            ${movie.mpaaRating}
                        </#if>
                    </td>
                    <td>
                        <#if movie.country??>
                            ${movie.country}
                        </#if>
                    </td>
                    <td>
                        <#if movie.theaterViewings??>
                            ${movie.theaterViewings}
                        </#if>
                    </td>
                    <td>
                        <#if movie.comments??>
                            ${movie.comments}
                        </#if>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- js for datatables AND its Select extension-->
    <@util.external_js "https://cdn.datatables.net/t/dt/dt-1.10.11,se-1.1.2/datatables.min.js" />
    <@util.js "movie/movies" />
    <script type="text/javascript">
        $(document).ready(function() {
            $('#moviesTable').DataTable({
                select: true
            });
        });
    </script>
</@layout.standard>