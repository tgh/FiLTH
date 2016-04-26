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
    $('#bgImageMovie').click(function () {
        movieId = $('#bgImageMovie').attr('data-movie-id');
        $.ajax(contextPath + '/movie?id=' + movieId, {
            success: function(data) {
                //the '.mCSB_container *must* be present here (see http://manos.malihu.gr/jquery-custom-content-scroller/4/#faq-4)
                $('#movieModal .mCSB_container').html(data);
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
    
    //init custom scrollbar for movie modal
    $('#movieModal').mCustomScrollbar({
        theme: "minimal-dark"
    });
});