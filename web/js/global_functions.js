function show(element) {
    element.removeClass('hidden');
}

function hide(element) {
    element.addClass('hidden');
}

function initDataTable(tableId) {
    $(tableId).DataTable();
}

$(document).ready(function() {
    $(document).on('opening', '.remodal', function () {
        movieId = $('#bgImageMovie').attr('data-movie-id');
        $.ajax(contextPath + '/movie?id=' + movieId, {
            success: function(data) {
                $('.remodal').html(data);
            }
        });
    });
});