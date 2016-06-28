/**
 * MovieList constructor
 */
MovieList = function MovieList(json) {
    this.data = JSON.parse(json);
}

/** MovieList functions */

MovieList.prototype.addMovie = function(id, rank, comments) {
    this.data['movies'].push({id: id, rank: rank, comments: comments});
}

MovieList.prototype.removeMovie = function(id) {
    for (i=0; i < this.data['movies'].length; ++i) {
        if (this.data['movies'][i]['id'] == id) {
            this.data['movies'].splice(i, 1);
            break;
        }
    }
}

MovieList.prototype.setTitle = function(title) {
    this.data['title'] = title;
}

MovieList.prototype.setAuthor = function(author) {
    this.data['author'] = author;
}

MovieList.prototype.setRankForMovie = function(movieId, rank) {
    rank = parseInt(rank);
    for (i=0; i < this.data['movies'].length; ++i) {
        if (this.data['movies'][i]['id'] == movieId) {
            this.data['movies'][i]['rank'] = rank;
            break;
        }
    }
}

MovieList.prototype.toJsonString = function() {
    return JSON.stringify(this.data);
}