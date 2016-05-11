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

function movieLinkClickHandler(event) {
    movieId = event.target.dataset.movieId;
    //if the target doesn't have the movie id, check the ancestor elements for one
    if (typeof(movieId) === 'undefined') {
        movieId = $(event.target).parents('[data-movie-id]')[0].dataset.movieId;
    }
    viewMovie(movieId);
}

function viewMovie(movieId) {
    //clear the current modal content
    $('#modalBody').remove();
    $('#modalFooter').remove();
    //show the 'Loading...' text
    show($('#modalLoadingText'));
    
    $.ajax(contextPath + '/movie?id=' + movieId, {
        success: function(data) {
            //hide the 'Loading...' text
            hide($('#modalLoadingText'));
            //the '.mCSB_container *must* be present here (see http://manos.malihu.gr/jquery-custom-content-scroller/4/#faq-4)
            $('#movieModal .mCSB_container').html(data);
            //add click handlers for movie links in the modal
            $('#movieModal .movieLink').click(movieLinkClickHandler);
        }
    });
}

$(document).ready(function() {
    //add event handler for background image movie modal
    //(load movie data when opening movie modal for background image)
    $('#bgImageMovie').click(movieLinkClickHandler);
    
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