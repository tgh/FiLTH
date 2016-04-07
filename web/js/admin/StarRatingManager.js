/**
 * StarRatingManager constructor
 */
function StarRatingManager() {
    EntityManager.call(this, 'starRating');
    
    //add Star Rating-specific dom keys
    this.DOM_KEYS['addStarRatingInput'] = '#addStarRatingInput';
    this.DOM_KEYS['editStarRatingIdInput'] = '#editStarRatingIdInput';
    this.DOM_KEYS['editStarRatingInput'] = '#editStarRatingInput';
    
    //add starRating-specific messages
    this.MESSAGES['noRating'] = '"Rating" cannot be blank.';
}
StarRatingManager.prototype = Object.create(EntityManager.prototype);
StarRatingManager.prototype.constructor = StarRatingManager;


/** StarRatingManager functions */

/* Override */
StarRatingManager.prototype.initTable = function(table) {
    $(table).DataTable({
        'aoColumnDefs': [
            //do not allow sorting on the column containing the 'Edit' button
            {'bSortable': false, 'aTargets': ['editColumn']}
        ]}
    );
}

StarRatingManager.prototype.addStarRating = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.addStarRatingInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noRating)
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addStarRatingRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

StarRatingManager.prototype.addStarRatingRow = function(json) {
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.starRating.id,
        json.starRating.rating,
        '<a data-remodal-target="editStarRatingModal" data-starRating-id="' + json.starRating.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-starRating-id
    newRow.attr('data-starRating-id', json.starRating.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('starRatingId');
    $(newRow.find('td')[1]).addClass('starRating');
}

StarRatingManager.prototype.editButtonClickHandler = function(event) {
    starRatingId = event.target.dataset.starratingId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();

    //get values from starRating row
    starRating = $('tr[data-starRating-id="' + starRatingId + '"] td.starRating').text();
    //set input values
    $(this.DOM_KEYS.editStarRatingIdInput).val(starRatingId);
    $(this.DOM_KEYS.editStarRatingInput).val(starRating);
}

StarRatingManager.prototype.editStarRating = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.editStarRatingInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noRating)
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updateStarRatingRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

StarRatingManager.prototype.updateStarRatingRow = function(json) {
    //get row
    row = $('tr[data-starRating-id="' + json.starRating.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.starRating.id,
        json.starRating.rating,
        '<a data-remodal-target="editStarRatingModal" data-starRating-id="' + json.starRating.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    starRatingManager = new StarRatingManager();
});