/**
 * ListManager constructor
 */
function ListManager() {
    EntityManager.call(this, 'list');
    
    //add list-specific dom keys
    this.DOM_KEYS['addListTitleInput'] = '#addListTitleInput';
    this.DOM_KEYS['editListIdInput'] = '#editListIdInput';
    this.DOM_KEYS['editListTitleInput'] = '#editListTitleInput';
    this.DOM_KEYS['editListAuthorInput'] = '#editListAuthorInput';
    
    //add list-specific messages
    this.MESSAGES['noTitle'] = '"Title" cannot be blank.';
}
ListManager.prototype = Object.create(EntityManager.prototype);
ListManager.prototype.constructor = ListManager;


/** ListManager functions */

ListManager.prototype.addList = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.addListTitleInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noTitle)
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addListRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

ListManager.prototype.addListRow = function(json) {
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.list.id,
        json.list.title,
        json.list.author,
        '<a data-remodal-target="editListModal" data-list-id="' + json.list.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: listManager.deleteEntity(\'' + deleteUrl + '?id=' + json.list.id + '\', ' + json.list.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-list-id
    newRow.attr('data-list-id', json.list.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('listId');
    $(newRow.find('td')[1]).addClass('listTitle');
    $(newRow.find('td')[2]).addClass('listAuthor');
}

ListManager.prototype.editButtonClickHandler = function(event) {
    listId = event.target.dataset.listId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();
    
    //get values from list row
    listTitle = $('tr[data-list-id="' + listId + '"] td.listTitle').text();
    listAuthor = $('tr[data-list-id="' + listId + '"] td.listAuthor').text();
    //set input values
    $(this.DOM_KEYS.editListIdInput).val(listId);
    $(this.DOM_KEYS.editListTitleInput).val(listTitle);
    $(this.DOM_KEYS.editListAuthorInput).val(listAuthor);
}

ListManager.prototype.editList = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.editListTitleInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noTitle)
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updateListRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

ListManager.prototype.updateListRow = function(json) {
    //get row
    row = $('tr[data-list-id="' + json.list.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.list.id,
        json.list.title,
        json.list.author,
        '<a data-remodal-target="editListModal" data-list-id="' + json.list.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: listManager.deleteEntity(\'' + deleteUrl + '?id=' + json.list.id + '\', ' + json.list.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    listManager = new ListManager();
});