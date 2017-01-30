/**
 * MovieListChanges constructor.
 * 
 * MovieListChanges keeps track of movie changes (additions and removals) to the
 * current movie list being created/edited while the edit panel is open.
 */
MovieListChanges = function MovieListChanges() {
    //only need to track additions since removals happen outside the edit panel
    this.additions = [];
}

/** MovieListChanges functions */

MovieListChanges.prototype.selectMovie = function(id) {
    this.additions.push(id);
}

MovieListChanges.prototype.deselectMovie = function(id) {
    //since actual removal of a movie from the list happens outside the edit panel,
    //deselect means deslecting a movie that was just selected during this edit session
    //(basically an 'undo' of a selection)
    if (this.additions.indexOf(id) !== -1) {
        var index = this.additions.indexOf(id);
        //remove the id from the additions array
        this.additions.splice(index, 1);
    } else {
        console.log('WARNING: entered else block that is never expected to be entered');
    }
}

MovieListChanges.prototype.clear = function() {
    //clear the additions array
    this.additions.splice(0, this.additions.length);
}

MovieListChanges.prototype.hasChanges = function() {
    return this.additions.length > 0;
}