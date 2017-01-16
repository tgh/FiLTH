function addListeners() {
    $(document).click(allClickHandler);
    $('.listRankEdit').keydown(listRankKeydownHandler);
    $('#listTitleEdit').keydown(listTitleKeydownHandler);
    $('#listAuthorEdit').keydown(listAuthorKeydownHandler);
    $('.listCommentsEdit').keydown(listCommentsKeydownHandler);
    $('#editListMoviesTable td').not('.movieCheckboxContainer').click(editListMoviesTableRowClickHandler);
    $('.movieCheckbox').click(movieCheckboxClickHandler);
    $('.movieTitle').click(movieLinkClickHandler);  //global_functions.js
}

function allClickHandler(event) {
    //if edit panel is open, close the panel if click is outside of panel
    if ($('#editPanel').is(':visible')) {
        //FIXME: we shouldn't need to have this, but jquery doesn't seem to see these
        //paginate buttons as descendants of the the edit panel for some reason :(
        if ($(event.target).closest('.paginate_button').length) {
            //do nothing
            return;
        }
        if ($(event.target).closest('#editPanel').length == 0) {
            closeEditPanel();
        }
    }
    
    //rank click
    if ($(event.target).closest('.listRankDisplay').length) {
        editableContentClickHandler(event);
    }
    //title click
    else if ($(event.target).closest('#listTitleDisplay').length) {
        editableContentClickHandler(event);
    }
    //author click
    else if ($(event.target).closest('#listAuthorDisplay').length) {
        editableContentClickHandler(event);
    }
    //comments click
    else if ($(event.target).closest('.listCommentsDisplay').length) {
        editableContentClickHandler(event);
    }
    //input click
    else if ($(event.target).closest('input').length) {
        //do nothing
    }
    //'Edit' button
    else if ($(event.target).closest('#editLink a').length) {
        hideEditInputs();
        editClickHandler();
    }
    //anything else click
    else {
        //hide any editable inputs
        hideEditInputs();
    }
}

function editableContentClickHandler(event) {
    hideEditInputs();
    
    displayElement = $(event.target);
    editElement = displayElement.siblings('input');
    hide(displayElement);
    show(editElement);
    //activate the text field
    editElement.focus();
}

/**
 * Hide any displayed text fields for editing
 */
function hideEditInputs() {
    contentInput = $('.contentInput').not('.hidden');
    contentDisplay = contentInput.siblings('.contentDisplay');
    hide(contentInput);
    show(contentDisplay);
}

function listRankKeydownHandler(event) {
    rankEditElement = $(event.target);
    rankDisplayElement = rankEditElement.siblings('.contentDisplay');
    
    //'enter' key, keep change in rank
    if(event.keyCode == 13) {
        newRank = event.target.value;
        
        //verify new rank is valid (numeric)
        if (isNaN(newRank) || newRank < 1) {
            alertify.error("Rank must be a positive, non-zero number.")
            return;
        }
        
        movieId = rankEditElement.parents('tr')[0].dataset.movieId;
        movieList.setRankForMovie(movieId, newRank);
        
        //update and re-sort the table
        table = $('#listMoviesTable').DataTable();
        table.cell('tr[data-movie-id="' + movieId + '"] td.rankColumn').data(
            '<div class="listRankDisplay contentDisplay">' + newRank + '</div>' +
            '<input class="hidden listRankEdit contentInput" type="text" value="' + newRank + '">'
        ).order([[2, 'asc']]).draw('full-hold');
        
        saveList();
    }
    //'esc' key, cancel: revert back to original rank
    else if (event.keyCode == 27) {
        hide(rankEditElement);
        show(rankDisplayElement);
        //reset the input val to the original rank as well
        rankEditElement.val(rankDisplayElement.html());
    }
}

function listTitleKeydownHandler(event) {
    titleEditElement = $(event.target);
    titleDisplayElement = titleEditElement.siblings('.contentDisplay');
    
    //'enter' key, keep change in rank
    if(event.keyCode == 13) {
        newTitle = event.target.value;
        
        //verify title is not empty
        if (newTitle == '') {
            alertify.error("Title must not be empty.")
            return;
        }
        
        movieList.setTitle(newTitle);
        
        titleDisplayElement.html(newTitle);
        hide(titleEditElement);
        show(titleDisplayElement);
        
        saveList();
    }
    //'esc' key, cancel: revert back to original title
    else if (event.keyCode == 27) {
        hide(titleEditElement);
        show(titleDisplayElement);
        //reset the input val to the original title as well
        titleEditElement.val(titleDisplayElement.html());
    }
}

function listAuthorKeydownHandler(event) {
    authorEditElement = $(event.target);
    authorDisplayElement = authorEditElement.siblings('.contentDisplay');
    
    //'enter' key, keep change in rank
    if(event.keyCode == 13) {
        newAuthor = event.target.value;
        
        movieList.setAuthor(newAuthor);
        
        if (newAuthor == '') {
            authorDisplayElement.html('[no author]');
            authorDisplayElement.addClass('noAuthor');
            hide($('#by'));
        } else {
            authorDisplayElement.html(newAuthor);
            authorDisplayElement.removeClass('noAuthor');
            show($('#by'));
        }
        
        hide(authorEditElement);
        show(authorDisplayElement);
        
        saveList();
    }
    //'esc' key, cancel: revert back to original author
    else if (event.keyCode == 27) {
        hide(authorEditElement);
        show(authorDisplayElement);
        //reset the input val to the original author as well
        authorEditElement.val(authorDisplayElement.html());
    }
}

function listCommentsKeydownHandler(event) {
    commentsEditElement = $(event.target);
    commentsDisplayElement = commentsEditElement.siblings('.contentDisplay');
    
    //'enter' key, keep change in comments
    if(event.keyCode == 13) {
        newComments = event.target.value;
        
        movieId = commentsEditElement.parents('tr')[0].dataset.movieId;
        movieList.setCommentsForMovie(movieId, newComments);
        
        //update the table
        table = $('#listMoviesTable').DataTable();
        table.cell('tr[data-movie-id="' + movieId + '"] td.commentsColumn').data(
            '<div class="listCommentsDisplay contentDisplay">' + newComments + '</div>' +
            '<input class="hidden listCommentsEdit contentInput" type="text" value="' + newComments + '">'
        ).draw('full-hold');
        
        saveList();
    }
    //'esc' key, cancel: revert back to original comments
    else if (event.keyCode == 27) {
        hide(commentsEditElement);
        show(commentsDisplayElement);
        //reset the input val to the original comments as well
        commentsEditElement.val(commentsDisplayElement.html());
    }
}

function saveList() {
    //do not attempt to save if the list is invalid
    if (VALID == validateList()) {
        clearStackTraceContainer();
        
        $('#saveListForm').ajaxSubmit({
            success: function(json) {
                if (TRUE === json[JSON_KEY_SUCCESS]) {
                    //TODO: add list id in json in the controller, and insert list id into the list id div
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
}

function removeFromList(removeUrl, movieId) {
    movieRow = $('tr[data-movie-id=' + movieId + ']');
    
    $.ajax({
        url: removeUrl,
        type: 'POST',
        success: function(json) {
            if (TRUE === json[JSON_KEY_SUCCESS]) {
                alertify.success(json[JSON_KEY_MESSAGE]);
                
                //remove row from the table
                table = $('#listMoviesTable').DataTable();
                table.row($('tr[data-movie-id="' + movieId + '"]')).remove().draw('full-hold');
                //remove movie from list json
                movieList.removeMovie(movieId);
                //uncheck and enable the movie from the edit panel
                movieCheckbox = $('#editListMoviesTable tr[data-movie-id="' + movieId + '"] .movieCheckbox');
                movieCheckbox.prop('checked', false);
                movieCheckbox.removeAttr('disabled');
            } else {
                alertify.error(json[JSON_KEY_MESSAGE]);
            }
        },
        error: function(data) {
            ajaxErrorHandler(data, 'Sorry, a problem occurred during movie removal and no info was given.');
        }
    });
}

function validateList() {
    //a title must be present
    title = $('#listTitleDisplay').text();
    if (title === '[title]' || title === '') {
        alertify.error('You must give the list a title.')
        return INVALID;
    }
    
    //there must be at least 1 movie in the list
    if (false == movieList.hasMovies()) {
        alertify.error('There must be at least 1 movie in the list.')
        return INVALID;
    }
    
    return VALID;
}

function editClickHandler() {
    $('#editPanel').slideDown(500);
}

/**
 * Check/uncheck the checkbox for the movie row clicked in the edit panel.
 */
function editListMoviesTableRowClickHandler(event) {
    var checkbox = $(event.target).parents('tr').find('.movieCheckbox');
    var movieId = $(event.target).parents('tr').data('movie-id');
    
    //movie is being de-selected
    if (checkbox.prop('checked')) {
        checkbox.prop('checked', false);
        //remove movie from the list of changes
        movieListChanges.deselectMovie(movieId);
    }
    //movie is being selected
    else {
        checkbox.prop('checked', true);
        //add the movie to the list of changes
        movieListChanges.selectMovie(movieId);
    }
}

function movieCheckboxClickHandler(event) {
    var checkbox = $(event.target);
    var movieId = $(event.target).parents('tr').data('movie-id');
    
    //movie is being selected
    if (checkbox.prop('checked')) {
        //remove movie from the list of changes
        movieListChanges.selectMovie(movieId);
    }
    //movie is being deselected
    else {
        //add the movie to the list of changes
        movieListChanges.deselectMovie(movieId);
    }
}

/**
 * Add the selected movies checked in the edit panel to the list:
 * add movies to json, and add movies to the list DataTable.
 */
function addSelectedMovies() {
    table = $('#listMoviesTable').DataTable();
    
    //add movies to json
    $('.movieCheckbox:checked:enabled').each(function() {
        row = $(this).parents('tr');
        
        //get movie id
        movieId = $(row).data('movie-id');
        //add movie to json
        movieList.addMovie(movieId);
    });
    
    saveList();
    
    rows = [];
    //add a new row in the table for each movie
    $('.movieCheckbox:checked:enabled').each(function() {
        row = $(this).parents('tr');
        
        //get movie id
        movieId = $(row).data('movie-id');
        //get movie title
        movieTitle = $(row).find('.movieTitle').text();
        movieYear = $(row).find('.movieYear').text();
        if (movieYear !== '') {
            movieTitle = movieTitle + ' (' + movieYear + ')';
        }
        
        //get list id
        listId = $('#listId').data('list-id');
        //has the movie been seen?
        isSeen = $(row).find('.movieStarRating').text() !== 'not seen';
        //first column will be check mark if seen, blank otherwise
        firstColumn = isSeen ? '&#x2714;' : '';
        
        //add row into table
        newRow = table.row.add([
            firstColumn,
            '<a class="movieTitle movieLink" data-remodal-target="movieModal" data-movie-id="' + movieId +'">' + movieTitle + '</a>',
            '<div class="listRankDisplay contentDisplay"></div><input class="hidden listRankEdit contentInput" type="text">',
            '<div class="listCommentsDisplay contentDisplay"></div><input class="hidden listCommentsEdit contentInput" type="text">',
            '<a class="button redButton circleButton arialBlack white" title="Remove from list" href="javascript: removeFromList(\'' + removeMovieFromListUrl + '?id=' + listId + '&movieId=' + movieId + '\', ' + movieId + ')">X</a>'
        ]).draw('full-hold').nodes().to$();
       
        //add data-movie-id
        newRow.attr('data-movie-id', movieId);
        //add classes to row
        $(newRow.find('td')[2]).addClass('rankColumn');
        $(newRow.find('td')[3]).addClass('commentsColumn');
    });
    
    //disable the checkboxes for each added movie in the Edit panel
    $('.movieCheckbox:checked:enabled').each(function() {
        $(this).attr('disabled', true);
    });
}

function closeEditPanel() {
    //only save the list if changes have been made to the list
    if (movieListChanges.hasChanges()) {
        //persist the changes
        addSelectedMovies();
        //clear the list of changes
        movieListChanges.clear();
    }
    
    $('#editPanel').slideUp();
}

/**
 * For all movies in the list, make sure the corresponding movie in the edit
 * panel is checked.
 */
function checkMoviesInList() {
    $('#listMoviesTable tr').each(function() {
        movieId = $(this).data('movie-id');
        movieCheckbox = $('#editListMoviesTable tr[data-movie-id="' + movieId + '"] .movieCheckbox');
        movieCheckbox.prop('checked', true);
        movieCheckbox.attr('disabled', true);
    });
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

function initListTable() {
    start = Math.floor(Date.now() / 1000);
    console.log('Initializing data table');
    $('#listMoviesTable').DataTable({
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
            $('#listMoviesTable').css('width', '100%');
            show($('#listMoviesTable'));
        }
    });
    end = Math.floor(Date.now() / 1000);
    console.log('DONE initializing data table in ' + (end - start) + ' seconds');
}

function initMoviesTable() {
    start = Math.floor(Date.now() / 1000);
    console.log('Initializing movies data table');
    $('#editListMoviesTable').DataTable({
        //do not load all content at once--only when displaying on the page
        deferRender: true,
        initComplete: function() {
            hide($('#loadingText'));
            //hack: without this width resize, the movies table renders with a width of 0 for some reason :-/
            $('#editListMoviesTable').css('width', '100%');
            show($('#editListMoviesTable'));
        }
    });
    end = Math.floor(Date.now() / 1000);
    console.log('DONE initializing movies data table in ' + (end - start) + ' seconds');
}

$(document).ready(function() {
    initListTable();
    initMoviesTable();
    checkMoviesInList();
    addListeners();
    //create object to keep track of movie changes while editing the list
    movieListChanges = new MovieListChanges();
});