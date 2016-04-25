function show(element) {
    element.removeClass('hidden');
}

function hide(element) {
    element.addClass('hidden');
}

function initDataTable(tableId) {
    $(tableId).DataTable();
}

String.prototype.capitalizeFirstLetter = function() {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

$(document).ready(function() {
    //add event handler for movie modal (load movie data when opening movie modal for background image)
    $(document).on('opening', '.remodal[data-remodal-id="bgMovieModal"]', function () {
        movieId = $('#bgImageMovie').attr('data-movie-id');
        $.ajax(contextPath + '/movie?id=' + movieId, {
            success: function(data) {
                $('.remodal[data-remodal-id="bgMovieModal"]').html(data);
            }
        });
    });
    
    //prevent form submission when hitting the 'enter' key
    $(document).keydown(function(event) {
        if(event.keyCode == 13) {
            event.preventDefault();
            return false;
        }
    });
});