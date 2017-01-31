function addListeners() {
    $(document).click(allClickHandler);
    $('#listTitleEdit').keydown(listTitleKeydownHandler);
    $('#listAuthorEdit').keydown(listAuthorKeydownHandler);
    $('.rankInput').keydown(listRankKeydownHandler);
    $('.rankInput').focusout(listRankFocusoutHandler);
    $('.commentsInput').keydown(listCommentsKeydownHandler);
    $('.commentsInput').focusout(listCommentsFocusoutHandler);
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
        //clicking a checkbox for a movie in the edit panel
        else if ($(event.target).closest('.movieCheckbox').length) {
            movieCheckboxClickHandler(event);
        }
    }
    //'Edit' button
    else if ($(event.target).closest('#editButtonContainer a').length) {
        editClickHandler();
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
        saveList();

        //force exit out of input
        $(':focus').blur();
    }
    //'esc' key, cancel: revert back to original title
    else if (event.keyCode == 27) {
        //reset the input val to the original title as well
        titleEditElement.val(titleDisplayElement.html());
    }
}

function listAuthorKeydownHandler(event) {
    authorEditElement = $(event.target);
    authorDisplayElement = authorEditElement.siblings('.contentDisplay');
    
    //'enter' key, keep change in author
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
        
        saveList();
        //force exit out of input
        $(':focus').blur();
    }
    //'esc' key, cancel: revert back to original author
    else if (event.keyCode == 27) {
        //reset the input val to the original author as well
        authorEditElement.val(authorDisplayElement.html());
    }
}

function listRankKeydownHandler(event) {
    rankInput = $(event.target);
    rankInputCurrentValue = rankInput.siblings('.rankValue').text();
    
    //'enter' key, keep change in rank
    if(event.keyCode == 13) {
        newRank = event.target.value;
        
        //verify new rank is valid (numeric)
        if (isNaN(newRank) || newRank < 1) {
            alertify.error("Rank must be a positive, non-zero number.")
            return;
        }
        
        movieId = rankInput.parents('tr')[0].dataset.movieId;
        movieList.setRankForMovie(movieId, newRank);
        
        //update and re-sort the table
        table = $('#listMoviesTable').DataTable();
        table.cell('tr[data-movie-id="' + movieId + '"] td.rankColumn').data(
            '<div class="rankValue hidden">' + newRank + '</div>' +
            '<input class="rankInput" type="text" value="' + newRank + '">'
        ).order([[2, 'asc']]).draw('full-hold');
        
        //remove and add this handler throughout the DOM so that the newly added rank element has this handler
        $('.rankInput').unbind();
        $('.rankInput').keydown(listRankKeydownHandler);
        
        saveList();
    }
    //'esc' key, cancel: revert back to original rank
    else if (event.keyCode == 27) {
        //reset the input val to the original rank as well
        rankInput.val(rankInputCurrentValue);
        rankInput.blur();
    }
}

function listRankFocusoutHandler(event) {
    var rankInput = $(event.target);
    var originalValue = $(rankInput).siblings('.rankValue').text();
    $(rankInput).val(originalValue);
}

function listCommentsKeydownHandler(event) {
    commentsInput = $(event.target);
    commentsInputCurrentValue = commentsInput.siblings('.commentsValue').text();
    
    //'enter' key, keep change in comments
    if(event.keyCode == 13) {
        newComments = event.target.value;
        
        movieId = commentsInput.parents('tr')[0].dataset.movieId;
        movieList.setCommentsForMovie(movieId, newComments);
        
        //update the table
        table = $('#listMoviesTable').DataTable();
        table.cell('tr[data-movie-id="' + movieId + '"] td.commentsColumn').data(
            '<input class="commentsInput" type="text" value="' + newComments + '">'
        ).draw('full-hold');
        
        //remove and add this handler throughout the DOM so that the newly added comment element has this handler
        $('.commentsInput').unbind();
        $('.commentsInput').keydown(listCommentsKeydownHandler);
        
        saveList();
    }
    //'esc' key, cancel: revert back to original comments
    else if (event.keyCode == 27) {
        //reset the input val to the original comments as well
        commentsInput.val(commentsInputCurrentValue);
    }
}

function listCommentsFocusoutHandler(event) {
    var commentsInput = $(event.target);
    var originalValue = $(commentsInput).siblings('.commentsValue').text();
    $(commentsInput).val(originalValue);
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
            '<div class="rankValue hidden"></div><input class="rankInput" type="text">',
            '<input class="commentsInput" type="text">',
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