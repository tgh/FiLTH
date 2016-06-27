function addListeners() {
    $('.listRankDisplay').click(listRankClickHandler);
    $('.removeButton').click(removeMovieFromList);
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
    rankEditElement.find('input')[0].focus();
}

function listRankKeydownHandler(event) {
    //'enter' key, keep change in rank
    if(event.keyCode == 13) {
        rankEditElement = $(event.target).parent();
        rankDisplayElement = rankEditElement.siblings('.listRankDisplay');
        newRank = event.target.value;
        
        //verify new rank is valid (numeric)
        //TODO
        
        rankDisplayElement.html(newRank);
        hide(rankEditElement);
        show(rankDisplayElement);
    }
    //'esc' key, cancel: revert back to original rank
    else if (event.keyCode == 27) {
        hide(rankEditElement);
        show(rankDisplayElement);
    }   
}

function removeMovieFromList(event) {
    movieRow = $(event.target).parents('.listMovieRow');
    hide(movieRow);
}

$(document).ready(function() {
    addListeners();
    start = Math.floor(Date.now() / 1000);
    console.log('Initializing data table');
    $('#moviesTable').DataTable({
        //allow selection of table rows
        select: true,
        //do not load all content at once--only when displaying on the page
        deferRender: true,
        //sort by rank first, then title
        order: [[2, 'asc'], [1, 'asc']],
        initComplete: function() {
            hide($('#loadingText'));
            //hack: without this width resize, the movies table renders with a width of 0 for some reason :-/
            $('#moviesTable').css('width', '100%');
            show($('#moviesTable'));
        }
    });
    end = Math.floor(Date.now() / 1000);
    console.log('DONE initializing data table in ' + (end - start) + ' seconds');
});