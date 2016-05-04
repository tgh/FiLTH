function movieTitleClickHandler(event) {
    viewMovie($(event.target).parents('tr'));   //global_function.js
}

function addListeners() {
    $('.movieTitle').click(movieTitleClickHandler);
}

$(document).ready(function() {
    addListeners();
});