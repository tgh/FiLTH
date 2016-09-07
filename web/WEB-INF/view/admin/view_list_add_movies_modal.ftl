<h1>Add Movies</h1>

<div id="addMoviesModalBody">
    <table id="addMoviesModalTable" class="hidden">
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
</div>

<#-- js for datatables AND its Select extension-->
<@util.js "movie/movies" />
<script type="text/javascript">
    $(document).ready(function() {
        start = Math.floor(Date.now() / 1000);
        console.log('Initializing movies data table');
        $('#addMoviesModalTable').DataTable({
            //allow selection of table rows
            select: true,
            //do not load all content at once--only when displaying on the page
            deferRender: true,
            initComplete: function() {
                hide($('#loadingText'));
                //hack: without this width resize, the movies table renders with a width of 0 for some reason :-/
                $('#addMoviesModalTable').css('width', '100%');
                show($('#addMoviesModalTable'));
            }
        });
        end = Math.floor(Date.now() / 1000);
        console.log('DONE initializing movies data table in ' + (end - start) + ' seconds');
    });
</script>