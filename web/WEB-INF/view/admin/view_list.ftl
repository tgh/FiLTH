<@layout.standard "FiLTH Admin: View List">
    <@util.css "admin" />
    
    <h1>${list.title}</h1>
    
    <table>
        <#list list.listMovies as listMovie>
            <tr class="listMovieRow" data-movie-id="${listMovie.movie.id}">
                <td>${listMovie.movie.title} <#if listMovie.movie.year??>(${listMovie.movie.year})</#if></td>
                <td class="listRankDisplay"><#if listMovie.rank??>${listMovie.rank}</#if></td>
                <td class="listRankEdit hidden">
                    <input type="text" <#if listMovie.rank??>value="${listMovie.rank}"</#if>>
                </td>
                <td><#if listMovie.comments??>${listMovie.comments}</#if></td>
                <td><a class="button redButton removeButton">Remove</a></td>
            </tr>
        </#list>
    </table>
    
    <@util.js "admin/viewList" />
</@layout.standard>