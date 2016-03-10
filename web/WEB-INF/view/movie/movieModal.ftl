<button data-remodal-action="close" class="remodal-close"></button>

<h3>${movie.title} (${movie.year})</h3>

<p>${movie.starRatingForDisplay}</p>

<p>MPAA Rating: ${movie.mpaaRating}</p>

<p>Country: ${movie.country}</p>

<p>How many times seen in the theater: ${movie.theaterViewings}<p>

<#if movie.comments??>
    <p>Comments: ${movie.comments}</p>
</#if>