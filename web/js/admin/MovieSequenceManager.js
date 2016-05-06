/**
 * MovieSequenceManager constructor
 */
function MovieSequenceManager() {
    EntityManager.call(this, 'sequence');
    
    //add sequence-specific dom keys
    this.DOM_KEYS['addSequenceNameInput'] = '#addSequenceNameInput';
    this.DOM_KEYS['editSequenceIdInput'] = '#editSequenceIdInput';
    this.DOM_KEYS['editSequenceNameInput'] = '#editSequenceNameInput';
    this.DOM_KEYS['editSequenceTypeInput'] = '#editSequenceTypeInput';
    
    //add sequence-specific messages
    this.MESSAGES['noName'] = '"Name" cannot be blank.';
}
MovieSequenceManager.prototype = Object.create(EntityManager.prototype);
MovieSequenceManager.prototype.constructor = MovieSequenceManager;


/** MovieSequenceManager functions */

MovieSequenceManager.prototype.addSequence = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.addSequenceNameInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noName)
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addSequenceRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

MovieSequenceManager.prototype.addSequenceRow = function(json) {
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.sequence.id,
        json.sequence.name,
        json.sequence.sequenceType,
        '<a data-remodal-target="editSequenceModal" data-sequence-id="' + json.sequence.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-sequence-id
    newRow.attr('data-sequence-id', json.sequence.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('sequenceId');
    $(newRow.find('td')[1]).addClass('sequenceName');
    $(newRow.find('td')[2]).addClass('sequenceType');
}

MovieSequenceManager.prototype.editButtonClickHandler = function(event) {
    sequenceId = event.target.dataset.sequenceId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();
    
    //get values from sequence row
    sequenceName = $('tr[data-sequence-id="' + sequenceId + '"] td.sequenceName').text();
    sequenceType = $('tr[data-sequence-id="' + sequenceId + '"] td.sequenceType').text();
    //set input values
    $(this.DOM_KEYS.editSequenceIdInput).val(sequenceId);
    $(this.DOM_KEYS.editSequenceNameInput).val(sequenceName);
    $(this.DOM_KEYS.editSequenceTypeInput + ' option[value="' + sequenceType + '"]').attr('selected','selected');
}

MovieSequenceManager.prototype.editSequence = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.editSequenceNameInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noName)
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updateSequenceRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

MovieSequenceManager.prototype.updateSequenceRow = function(json) {
    //get row
    row = $('tr[data-sequence-id="' + json.sequence.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.sequence.id,
        json.sequence.name,
        json.sequence.sequenceType,
        '<a data-remodal-target="editSequenceModal" data-sequence-id="' + json.sequence.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    sequenceManager = new MovieSequenceManager();
});