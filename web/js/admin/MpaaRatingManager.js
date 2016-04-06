/**
 * MpaaRatingManager constructor
 */
function MpaaRatingManager() {
    EntityManager.call(this, 'mpaaRating');
    
    //add MPAA Rating-specific dom keys
    this.DOM_KEYS['addMpaaRatingCodeInput'] = '#addMpaaRatingCodeInput';
    this.DOM_KEYS['editMpaaRatingIdInput'] = '#editMpaaRatingIdInput';
    this.DOM_KEYS['editMpaaRatingCodeInput'] = '#editMpaaRatingCodeInput';
    
    //add mpaaRating-specific messages
    this.MESSAGES['noCode'] = '"Rating Code" cannot be blank.';
}
MpaaRatingManager.prototype = Object.create(EntityManager.prototype);
MpaaRatingManager.prototype.constructor = MpaaRatingManager;


/** MpaaRatingManager functions */

/* Override */
MpaaRatingManager.prototype.initTable = function(table) {
    $(table).DataTable({
        'aoColumnDefs': [
            //do not allow sorting on the column containing the 'Edit' button
            {'bSortable': false, 'aTargets': ['editColumn']}
        ]}
    );
}

MpaaRatingManager.prototype.addMpaaRating = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.addMpaaRatingCodeInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noCode)
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addMpaaRatingRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

MpaaRatingManager.prototype.addMpaaRatingRow = function(json) {
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.mpaaRating.id,
        json.mpaaRating.ratingCode,
        '<a data-remodal-target="editMpaaRatingModal" data-mpaaRating-id="' + json.mpaaRating.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-mpaaRating-id
    newRow.attr('data-mpaaRating-id', json.mpaaRating.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('mpaaRatingId');
    $(newRow.find('td')[1]).addClass('mpaaRatingCode');
}

MpaaRatingManager.prototype.editButtonClickHandler = function(event) {
    mpaaRatingId = event.target.dataset.mpaaratingId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();

    //get values from mpaaRating row
    mpaaRatingCode = $('tr[data-mpaaRating-id="' + mpaaRatingId + '"] td.mpaaRatingCode').text();
    //set input values
    $(this.DOM_KEYS.editMpaaRatingIdInput).val(mpaaRatingId);
    $(this.DOM_KEYS.editMpaaRatingCodeInput).val(mpaaRatingCode);
}

MpaaRatingManager.prototype.editMpaaRating = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.editMpaaRatingCodeInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noCode)
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updateMpaaRatingRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

MpaaRatingManager.prototype.updateMpaaRatingRow = function(json) {
    //get row
    row = $('tr[data-mpaaRating-id="' + json.mpaaRating.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.mpaaRating.id,
        json.mpaaRating.ratingCode,
        '<a data-remodal-target="editMpaaRatingModal" data-mpaaRating-id="' + json.mpaaRating.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    mpaaRatingManager = new MpaaRatingManager();
});