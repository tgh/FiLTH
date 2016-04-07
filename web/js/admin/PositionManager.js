/**
 * PositionManager constructor
 */
function PositionManager() {
    EntityManager.call(this, 'position');
    
    //add MPAA Rating-specific dom keys
    this.DOM_KEYS['addPositionTitleInput'] = '#addPositionTitleInput';
    this.DOM_KEYS['editPositionIdInput'] = '#editPositionIdInput';
    this.DOM_KEYS['editPositionTitleInput'] = '#editPositionTitleInput';
    
    //add position-specific messages
    this.MESSAGES['noTitle'] = '"Position Title" cannot be blank.';
}
PositionManager.prototype = Object.create(EntityManager.prototype);
PositionManager.prototype.constructor = PositionManager;


/** PositionManager functions */

/* Override */
PositionManager.prototype.initTable = function(table) {
    $(table).DataTable({
        'aoColumnDefs': [
            //do not allow sorting on the column containing the 'Edit' button
            {'bSortable': false, 'aTargets': ['editColumn']}
        ]}
    );
}

PositionManager.prototype.addPosition = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.addPositionTitleInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noTitle)
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addPositionRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

PositionManager.prototype.addPositionRow = function(json) {
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.position.id,
        json.position.title,
        '<a data-remodal-target="editPositionModal" data-position-id="' + json.position.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-position-id
    newRow.attr('data-position-id', json.position.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('positionId');
    $(newRow.find('td')[1]).addClass('positionTitle');
}

PositionManager.prototype.editButtonClickHandler = function(event) {
    positionId = event.target.dataset.positionId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();

    //get values from position row
    positionTitle = $('tr[data-position-id="' + positionId + '"] td.positionTitle').text();
    //set input values
    $(this.DOM_KEYS.editPositionIdInput).val(positionId);
    $(this.DOM_KEYS.editPositionTitleInput).val(positionTitle);
}

PositionManager.prototype.editPosition = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.editPositionTitleInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noTitle)
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updatePositionRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

PositionManager.prototype.updatePositionRow = function(json) {
    //get row
    row = $('tr[data-position-id="' + json.position.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.position.id,
        json.position.title,
        '<a data-remodal-target="editPositionModal" data-position-id="' + json.position.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    positionManager = new PositionManager();
});