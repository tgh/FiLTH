/**
 * TylerManager constructor
 */
function TylerManager() {
    EntityManager.call(this, 'tyler');
    
    //add tyler-specific dom keys
    this.DOM_KEYS['addTylerCategoryInput'] = '#addTylerCategoryInput';
    this.DOM_KEYS['editTylerIdInput'] = '#editTylerIdInput';
    this.DOM_KEYS['editTylerCategoryInput'] = '#editTylerCategoryInput';
    
    //add tyler-specific messages
    this.MESSAGES['noCategory'] = '"Category" cannot be blank.';
}
TylerManager.prototype = Object.create(EntityManager.prototype);
TylerManager.prototype.constructor = TylerManager;


/** TylerManager functions */

TylerManager.prototype.addTyler = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.addTylerCategoryInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noCategory)
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addTylerRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

TylerManager.prototype.addTylerRow = function(json) {
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.tyler.id,
        json.tyler.category,
        '<a data-remodal-target="editTylerModal" data-tyler-id="' + json.tyler.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: tylerManager.deleteEntity(\'' + deleteUrl + '?id=' + json.tyler.id + '\', ' + json.tyler.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-tyler-id
    newRow.attr('data-tyler-id', json.tyler.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('tylerId');
    $(newRow.find('td')[1]).addClass('tylerCategory');
}

TylerManager.prototype.editButtonClickHandler = function(event) {
    tylerId = event.target.dataset.tylerId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();
    
    //get values from tyler row
    tylerCategory = $('tr[data-tyler-id="' + tylerId + '"] td.tylerCategory').text();
    //set input values
    $(this.DOM_KEYS.editTylerIdInput).val(tylerId);
    $(this.DOM_KEYS.editTylerCategoryInput).val(tylerCategory);
}

TylerManager.prototype.editTyler = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.editTylerCategoryInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noCategory)
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updateTylerRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

TylerManager.prototype.updateTylerRow = function(json) {
    //get row
    row = $('tr[data-tyler-id="' + json.tyler.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.tyler.id,
        json.tyler.category,
        '<a data-remodal-target="editTylerModal" data-tyler-id="' + json.tyler.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: tylerManager.deleteEntity(\'' + deleteUrl + '?id=' + json.tyler.id + '\', ' + json.tyler.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    tylerManager = new TylerManager();
});