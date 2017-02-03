/**
 * MovieListChanges constructor.
 * 
 * MovieListChanges keeps track of movie changes (additions and removals) to the
 * current movie list being created/edited while the edit panel is open.
 */
MovieListChanges = function MovieListChanges() {
    //only need to track additions since removals happen outside the edit panel.
    //this array will contain objects of the form:
    // {
    //     mid: 1234,
    //     title: "Title",
    //     year: 1999,
    //     isSeen: true
    // }
    this.additions = [];
}

/** MovieListChanges functions */

MovieListChanges.prototype.selectMovie = function(id, title, year, isSeen) {
    this.additions.push({
        'mid':id,
        'title':title,
        'year':year,
        "isSeen":isSeen
    });
}

/**
 * since actual removal of a movie from the list happens outside the edit panel,
 * deselect means deselecting a movie that was just selected during this edit session
 * (basically an 'undo' of a selection)
 */
MovieListChanges.prototype.deselectMovie = function(id) {
    //first, find the index of the movie
    var index = -1;
    for (var i=0; i < this.additions.length; ++i) {
        if (this.additions[i].mid == id) {
            index = i;
            break;
        }
    }
    
    //then, remove it if the movie was found
    if (index != -1) {
        //remove the movie from the additions array
        this.additions.splice(index, 1);
    } else {
        console.log('WARNING: entered else block that is never expected to be entered. ' +
                    'Movie to be deselected (' + id + ') was not found in the list of additions.');
    }
}

MovieListChanges.prototype.clear = function() {
    //clear the additions array
    this.additions.splice(0, this.additions.length);
}

MovieListChanges.prototype.hasChanges = function() {
    return this.additions.length > 0;
}