/**
 * MovieList constructor
 */
MovieList = function MovieList(json) {
    this.data = JSON.parse(json);
    //add movies property if not present (if it's a brand new list, for example)
    if (!this.data.movies) {
        this.data['movies'] = [];
    }
    this.INPUT_DOM_KEY = '#listJSONInput';
    this.updateInput();
}

/** MovieList functions */

MovieList.prototype.addMovie = function(id, rank, comments) {
    this.data['movies'].push({mid: id});
    this.updateInput();
}

MovieList.prototype.removeMovie = function(id) {
    for (i=0; i < this.data['movies'].length; ++i) {
        if (this.data['movies'][i]['mid'] == id) {
            this.data['movies'].splice(i, 1);
            break;
        }
    }
    this.updateInput();
}

MovieList.prototype.setTitle = function(title) {
    this.data['title'] = title;
    this.updateInput();
}

MovieList.prototype.setAuthor = function(author) {
    this.data['author'] = author;
    this.updateInput();
}

MovieList.prototype.setRankForMovie = function(movieId, rank) {
    rank = parseInt(rank);
    for (i=0; i < this.data['movies'].length; ++i) {
        if (this.data['movies'][i]['mid'] == movieId) {
            this.data['movies'][i]['rank'] = rank;
            break;
        }
    }
    this.updateInput();
}

MovieList.prototype.setCommentsForMovie = function(movieId, comments) {
    for (i=0; i < this.data['movies'].length; ++i) {
        if (this.data['movies'][i]['mid'] == movieId) {
            this.data['movies'][i]['comments'] = comments;
            break;
        }
    }
    this.updateInput();
}

MovieList.prototype.toJsonString = function() {
    return JSON.stringify(this.data);
}

MovieList.prototype.updateInput = function() {
    $(this.INPUT_DOM_KEY).val(this.toJsonString());
}

MovieList.prototype.hasMovies = function() {
    if (!this.data.movies || this.data.movies.length == 0) {
        return false;
    }
    
    return true;
}