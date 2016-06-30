function addListeners() {
    $('.listRankDisplay').click(listRankClickHandler);
}

function listRankClickHandler(event) {
    //close any open rank text fields first
    openRankEdit = $('.listRankEdit').not('.hidden');
    openRankDisplay = openRankEdit.siblings('.listRankDisplay');
    hide(openRankEdit);
    show(openRankDisplay);
    
    rankDisplayElement = $(event.target);
    rankEditElement = rankDisplayElement.siblings('.listRankEdit');
    hide(rankDisplayElement);
    show(rankEditElement);
    rankEditElement.keydown(listRankKeydownHandler);
    //activate the rank text field
    rankEditElement.focus();
}

function listRankKeydownHandler(event) {
    //'enter' key, keep change in rank
    if(event.keyCode == 13) {
        rankEditElement = $(event.target);
        rankDisplayElement = rankEditElement.siblings('.listRankDisplay');
        newRank = event.target.value;
        
        //verify new rank is valid (numeric)
        if (isNaN(newRank) || newRank < 1) {
            alertify.error("Rank must be a positive, non-zero number.")
            return;
        }
        
        movieId = rankEditElement.parents('tr')[0].dataset.movieId;
        movieList.setRankForMovie(movieId, newRank);
        rankDisplayElement.html(newRank);
        hide(rankEditElement);
        show(rankDisplayElement);
        
        $('#listJSONInput').val(movieList.toJsonString());
        
        clearStackTraceContainer();
        $('#saveListForm').ajaxSubmit({
            success: function(json) {
                if (TRUE === json[JSON_KEY_SUCCESS]) {
                    alertify.success(json[JSON_KEY_MESSAGE]);
                } else {
                    alertify.error(json[JSON_KEY_MESSAGE]);
                }
            },
            error: function(data) {
                ajaxErrorHandler(data, 'Sorry, a problem occurred during save and no info was given.');
            }
        });
    }
    //'esc' key, cancel: revert back to original rank
    else if (event.keyCode == 27) {
        hide(rankEditElement);
        show(rankDisplayElement);
    }   
}

function removeFromList(movieId) {
    movieRow = $('tr[data-movie-id=' + movieId + ']');
    table = $('#moviesTable').DataTable();
    table.row($('tr[data-movie-id="' + movieId + '"]')).remove().draw('full-hold');
    movieList.removeMovie(movieId);
}

function clearStackTraceContainer() {
    stackTraceContainer = $('#stackTraceContainer');
    stackTraceContainer.html('');
    hide(stackTraceContainer);
}

function showStackTrace(message) {
    stackTraceContainer = $('#stackTraceContainer');
    stackTraceContainer.html(message);
    show(stackTraceContainer);
}

function ajaxErrorHandler(data, defaultMessage) {
    if (data && typeof data !== 'undefined') {
        showStackTrace(data.responseText);
    } else {
        alertify.error(defaultMessage);
    }
};

function initTable() {
    start = Math.floor(Date.now() / 1000);
    console.log('Initializing data table');
    $('#moviesTable').DataTable({
        //allow selection of table rows
        select: true,
        //do not load all content at once--only when displaying on the page
        deferRender: true,
        //sort by rank first, then title
        order: [[2, 'asc'], [1, 'asc']],
        //set number of rows to show per page
        pageLength: 100,
        //to do once init is complete:
        initComplete: function() {
            hide($('#loadingText'));
            //hack: without this width resize, the movies table renders with a width of 0 for some reason :-/
            $('#moviesTable').css('width', '100%');
            show($('#moviesTable'));
        }
    });
    end = Math.floor(Date.now() / 1000);
    console.log('DONE initializing data table in ' + (end - start) + ' seconds');
}

$(document).ready(function() {
    initTable();
    addListeners();
});