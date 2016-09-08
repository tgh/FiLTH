function addListeners() {
    $(document).click(allClickHandler);
    $('.listRankEdit').keydown(listRankKeydownHandler);
    $('#listTitleEdit').keydown(listTitleKeydownHandler);
    $('#listAuthorEdit').keydown(listAuthorKeydownHandler);
    $('.listCommentsEdit').keydown(listCommentsKeydownHandler);
    $('#addMoviesLink a').click(addMoviesClickHandler);
}

function allClickHandler(event) {
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
        table = $('#moviesTable').DataTable();
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
        table = $('#moviesTable').DataTable();
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
                table = $('#moviesTable').DataTable();
                table.row($('tr[data-movie-id="' + movieId + '"]')).remove().draw('full-hold');
                //remove movie from list json
                movieList.removeMovie(movieId);
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
    if ($('#moviesTable tr[data-movie-id]').length < 1) {
        alertify.error('There must be at least 1 movie in the list.')
        return INVALID;
    }
    
    return VALID;
}

function addMoviesClickHandler() {
    //clear the current modal content
    $('#addMoviesModalBody').remove();
    //show the 'Loading...' text
    show($('#addMoviesModalLoadingText'));
    
    $.ajax(addMoviesUrl, {  // <-- url defined in view_list.ftl
        success: function(data) {
            //hide the 'Loading...' text
            hide($('#addMoviesModalLoadingText'));
            //the '.mCSB_container *must* be present here (see http://manos.malihu.gr/jquery-custom-content-scroller/4/#faq-4)
            $('#addMoviesModal').html(data);
        }
    });
}

function addMoviesModalMovieRowClickHandler(event) {
    checkbox = $(event.target).parents('tr').find('.movieCheckbox');
    if (checkbox.prop('checked')) {
        checkbox.prop('checked', false);
    } else {
        checkbox.prop('checked', true);
    }
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