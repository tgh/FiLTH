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
    $(document).on('opening', '.remodal[data-remodal-id="movieModal"]', function () {
        movieId = $('#bgImageMovie').attr('data-movie-id');
        $.ajax(contextPath + '/movie?id=' + movieId, {
            success: function(data) {
                $('.remodal[data-remodal-id="movieModal"]').html(data);
            }
        });
    });
});