function movieTitleClickHandler(event) {
    movieId = $(event.target).parents('tr').attr('data-movie-id');
    $.ajax(contextPath + '/movie?id=' + movieId, {
        success: function(data) {
            //the '.mCSB_container *must* be present here (see http://manos.malihu.gr/jquery-custom-content-scroller/4/#faq-4)
            $('#movieModal .mCSB_container').html(data);
        }
    });
}

function addListeners() {
    $('.movieTitle').click(movieTitleClickHandler);
}

$(document).ready(function() {
    addListeners();
});