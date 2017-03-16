<@layout.standard "FiLTH: Movies">
    <#-- css for datatables AND its Select extension-->
    <@util.external_css "https://cdn.datatables.net/t/dt/dt-1.10.11,se-1.1.2/datatables.min.css" />
    <@util.css "movie/movies" />
    
    <h1>Movies</h1>
    
    <#--<img id="spinner" src="${rc.contextPath}/images/spinner.svg"/>-->
    <div id="loadingText"><h1>Loading...</h1></div>
    
    <table id="moviesTable" class="hidden">
        <thead>
            <tr>
                <th>Title</th>
                <th>Year</th>
                <th>Star Rating</th>
                <th>MPAA Rating</th>
                <th>Country</th>
                <th>Runtime</th>
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
                    <td><a class="movieTitle movieLink" data-remodal-target="movieModal" data-movie-id="${movie.id}">${movie.title}</a></td>
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
                        <#if movie.runtime??>
                            ${movie.runtime}
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
            start = Math.floor(Date.now() / 1000);
            console.log('Initializing data table');
            $('#moviesTable').DataTable({
                //allow selection of table rows
                select: true,
                //do not load all content at once--only when displaying on the page
                deferRender: true,
                initComplete: function() {
                    hide($('#loadingText'));
                    //hack: without this width resize, the movies table renders with a width of 0 for some reason :-/
                    $('#moviesTable').css('width', '100%');
                    show($('#moviesTable'));
                }
            });
            end = Math.floor(Date.now() / 1000);
            console.log('DONE initializing data table in ' + (end - start) + ' seconds');
        });
    </script>
</@layout.standard>