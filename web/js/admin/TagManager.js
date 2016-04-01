/**
 * TagManager constructor
 */
function TagManager() {
    EntityManager.call(this, 'tag');
    
    //add tag-specific dom keys
    this.DOM_KEYS['addTagNameInput'] = '#addTagNameInput';
    this.DOM_KEYS['addTagParentInput'] = '#addTagParentInput';
    this.DOM_KEYS['editTagIdInput'] = '#editTagIdInput';
    this.DOM_KEYS['editTagNameInput'] = '#editTagNameInput';
    this.DOM_KEYS['editTagParentInput'] = '#editTagParentInput';
    
    //add tag-specific messages
    this.MESSAGES['noName'] = '"Name" cannot be blank.';
    this.MESSAGES['parentNotInteger'] = '"Parent Id" must be a valid integer.';
}
TagManager.prototype = Object.create(EntityManager.prototype);
TagManager.prototype.constructor = TagManager;


/** TagManager functions */

TagManager.prototype.addTag = function() {
    if (false == $(this.DOM_KEYS.addForm).parsley().isValid()) {
        //validate() returns an object if validation fails, hence the 'true !== ...'
        if (true !== $(this.DOM_KEYS.addTagNameInput).parsley().validate()) {
            alertify.error(this.MESSAGES.noName)
        }
        if (true !== $(this.DOM_KEYS.addTagParentInput).parsley().validate()) {
            alertify.error(this.MESSAGES.parentNotInteger)
        }
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addTagRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

TagManager.prototype.addTagRow = function(json) {
    if (typeof json.tag.parent === 'undefined') {
        parentId = '';
    } else {
        parentId = json.tag.parent.id;
    }
    
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.tag.id,
        json.tag.name,
        parentId,
        '<a data-remodal-target="editTagModal" data-tag-id="' + json.tag.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: tagManager.deleteEntity(\'' + deleteUrl + '?id=' + json.tag.id + '\', ' + json.tag.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-tag-id
    newRow.attr('data-tag-id', json.tag.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('tagId');
    $(newRow.find('td')[1]).addClass('tagName');
    $(newRow.find('td')[2]).addClass('parentId');
}

TagManager.prototype.editButtonClickHandler = function(event) {
    tagId = event.target.dataset.tagId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();
    
    //get values from tag row
    tagName = $('tr[data-tag-id="' + tagId + '"] td.tagName').text();
    tagParentId = $('tr[data-tag-id="' + tagId + '"] td.parentId').text();
    //set input values
    $(this.DOM_KEYS.editTagIdInput).val(tagId);
    $(this.DOM_KEYS.editTagNameInput).val(tagName);
    $(this.DOM_KEYS.editTagParentInput).val(tagParentId);
}

TagManager.prototype.editTag = function() {
    if (false == $(this.DOM_KEYS.editForm).parsley().isValid()) {
        //validate() returns an object if validation fails, hence the 'true !== ...'
        if (true !== $(this.DOM_KEYS.editTagNameInput).parsley().validate()) {
            alertify.error(this.MESSAGES.noName)
        }
        if (true !== $(this.DOM_KEYS.editTagParentInput).parsley().validate()) {
            alertify.error(this.MESSAGES.parentNotInteger)
        }
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updateTagRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

TagManager.prototype.updateTagRow = function(json) {
    if (typeof json.tag.parent === 'undefined') {
        parentId = '';
    } else {
        parentId = json.tag.parent.id;
    }
    
    //get row
    row = $('tr[data-tag-id="' + json.tag.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.tag.id,
        json.tag.name,
        parentId,
        '<a data-remodal-target="editTagModal" data-tag-id="' + json.tag.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: tagManager.deleteEntity(\'' + deleteUrl + '?id=' + json.tag.id + '\', ' + json.tag.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    tagManager = new TagManager();
});