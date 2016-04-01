/**
 * OscarManager constructor
 */
function OscarManager() {
    EntityManager.call(this, 'oscar');
    
    //add oscar-specific dom keys
    this.DOM_KEYS['addOscarCategoryInput'] = '#addOscarCategoryInput';
    this.DOM_KEYS['editOscarIdInput'] = '#editOscarIdInput';
    this.DOM_KEYS['editOscarCategoryInput'] = '#editOscarCategoryInput';
    
    //add oscar-specific messages
    this.MESSAGES['noCategory'] = '"Category" cannot be blank.';
}
OscarManager.prototype = Object.create(EntityManager.prototype);
OscarManager.prototype.constructor = OscarManager;


/** OscarManager functions */

OscarManager.prototype.addOscar = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.addOscarCategoryInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noCategory)
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addOscarRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

OscarManager.prototype.addOscarRow = function(json) {
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.oscar.id,
        json.oscar.category,
        '<a data-remodal-target="editOscarModal" data-oscar-id="' + json.oscar.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: oscarManager.deleteEntity(\'' + deleteUrl + '?id=' + json.oscar.id + '\', ' + json.oscar.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-oscar-id
    newRow.attr('data-oscar-id', json.oscar.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('oscarId');
    $(newRow.find('td')[1]).addClass('oscarCategory');
}

OscarManager.prototype.editButtonClickHandler = function(event) {
    oscarId = event.target.dataset.oscarId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();
    
    //get values from oscar row
    oscarCategory = $('tr[data-oscar-id="' + oscarId + '"] td.oscarCategory').text();
    //set input values
    $(this.DOM_KEYS.editOscarIdInput).val(oscarId);
    $(this.DOM_KEYS.editOscarCategoryInput).val(oscarCategory);
}

OscarManager.prototype.editOscar = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.editOscarCategoryInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noCategory)
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updateOscarRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

OscarManager.prototype.updateOscarRow = function(json) {
    //get row
    row = $('tr[data-oscar-id="' + json.oscar.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.oscar.id,
        json.oscar.category,
        '<a data-remodal-target="editOscarModal" data-oscar-id="' + json.oscar.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: oscarManager.deleteEntity(\'' + deleteUrl + '?id=' + json.oscar.id + '\', ' + json.oscar.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    oscarManager = new OscarManager();
});