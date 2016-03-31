/**
 * OscarManager constructor
 */
function OscarManager() {
    EntityManager.call(this, 'oscar');
}
OscarManager.prototype = Object.create(EntityManager.prototype);
OscarManager.prototype.constructor = OscarManager;


/** OscarManager functions */

OscarManager.prototype.addOscar = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $('#addOscarCategoryInput').parsley().validate()) {
        alertify.error('"Category" cannot be blank.')
    } else {
        this.saveEntity($('#addOscarForm'), this.addOscarRow);
        //clear modal inputs
        $('#addOscarModal input').val('');
    }
}

OscarManager.prototype.addOscarRow = function(json) {
    //add row into table
    table = $('#oscarTable').DataTable();
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
    $('#editOscarForm').parsley().reset();
    
    //get values from oscar row
    oscarCategory = $('tr[data-oscar-id="' + oscarId + '"] td.oscarCategory').text();
    //set input values
    $('#editOscarModal input[name="id"]').val(oscarId);
    $('#editOscarModal input[name="category"]').val(oscarCategory);
}

OscarManager.prototype.editOscar = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $('#editOscarCategoryInput').parsley().validate()) {
        alertify.error('"Category" cannot be blank.')
    } else {
        this.saveEntity($('#editOscarForm'), this.updateOscarRow);
        //clear modal inputs
        $('#editOscarModal input').val('');
    }
}

OscarManager.prototype.updateOscarRow = function(json) {
    //get row
    row = $('tr[data-oscar-id="' + json.oscar.id + '"]');
        
    //update row in table
    table = $('#oscarTable').DataTable();
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