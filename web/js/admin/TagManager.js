/**
 * TagManager constructor
 */
function TagManager() {
    EntityManager.call(this, 'tag');
}
TagManager.prototype = Object.create(EntityManager.prototype);
TagManager.prototype.constructor = TagManager;


/** TagManager functions */

TagManager.prototype.addTag = function() {
    if (false == $(this.DOM_KEYS.addForm).parsley().isValid()) {
        //validate() returns an object if validation fails, hence the 'true !== ...'
        if (true !== $('#addTagNameInput').parsley().validate()) {
            alertify.error('"Name" cannot be blank.')
        }
        if (true !== $('#addTagParentInput').parsley().validate()) {
            alertify.error('"Parent Id" must be a valid integer.')
        }
    } else {
        this.saveEntity($('#addTagForm'), this.addTagRow);
        //clear modal inputs
        $('#addTagModal input').val('');
    }
}

TagManager.prototype.addTagRow = function(json) {
    if (typeof json.tag.parent === 'undefined') {
        parentId = '';
    } else {
        parentId = json.tag.parent.id;
    }
    
    //add row into table
    table = $('#tagTable').DataTable();
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
    $('#editTagForm').parsley().reset();
    
    //get values from tag row
    tagName = $('tr[data-tag-id="' + tagId + '"] td.tagName').text();
    tagParentId = $('tr[data-tag-id="' + tagId + '"] td.parentId').text();
    //set input values
    $('#editTagModal input[name="id"]').val(tagId);
    $('#editTagModal input[name="name"]').val(tagName);
    $('#editTagModal input[name="parent"]').val(tagParentId);
}

TagManager.prototype.editTag = function() {
    if (false == $('#editTagForm').parsley().isValid()) {
        //validate() returns an object if validation fails, hence the 'true !== ...'
        if (true !== $('#editTagNameInput').parsley().validate()) {
            alertify.error('"Name" cannot be blank.')
        }
        if (true !== $('#editTagParentInput').parsley().validate()) {
            alertify.error('"Parent Id" must be a valid integer.')
        }
    } else {
        this.saveEntity($('#editTagForm'), this.updateTagRow);
        //clear modal inputs
        $('#editTagModal input').val('');
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
    table = $('#tagTable').DataTable();
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