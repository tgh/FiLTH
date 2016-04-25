function movieTitleClickHandler(event) {
    movieId = $(event.target).parents('tr').attr('data-movie-id');
    $.ajax(contextPath + '/movie?id=' + movieId, {
        success: function(data) {
            $('.remodal[data-remodal-id="movieModal"]').html(data);
        }
    });
}

function addListeners() {
    $('.movieTitle').click(movieTitleClickHandler);
}

$(document).ready(function() {
    addListeners();
});