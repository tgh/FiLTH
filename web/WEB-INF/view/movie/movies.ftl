<@layout.standard "Movies">
    <h1>Movies</h1>
    
    <table id="moviesTable">
        <tr>
            <th>Title</th>
            <th>Year</th>
            <th>Star Rating</th>
            <th>MPAA Rating</th>
            <th>Country</th>
            <th>Times seen in theatre</th>
            <th>Comments</th>
        </tr>
        <#foreach movie in movies>
            <tr data-movie-id="${movie.id}">
                <td>${movie.title}</td>
                <td>
                    <#if movie.year??>
                        ${movie.year}
                    </#if>
                </td>
                <td>
                    <#if movie.starRating??>
                        ${movie.starRating}
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
    </table>
</@layout.standard>