function addListeners() {
    $('.movieTitle').click(movieLinkClickHandler);  //global_functions.js
}

$(document).ready(function() {
    addListeners();
});