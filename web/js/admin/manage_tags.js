function saveTag(form, successCallback) {
    clearStackTraceContainer();
    
    form.ajaxSubmit({
       success: function(json) {
           success = ajaxSuccessHandler(json);
           if (success) {
               successCallback(json);
           }
       },
       error: function(data) {
           ajaxErrorHandler(data, 'Sorry, a problem occurred during save and no info was given.');
       }
    });
    
    //close the modal
    $('.modalCancelButton').trigger('click');
}

function addTagButtonClickHandler() {
    //clear the add tag form validation UI
    $('#addTagForm').parsley().reset();
}

function addTag() {
    if (false == $('#addTagForm').parsley().isValid()) {
        //validate() returns an object if validation fails, hence the 'true !== ...'
        if (true !== $('#addTagNameInput').parsley().validate()) {
            alertify.error('"Name" cannot be blank.')
        }
        if (true !== $('#addTagParentInput').parsley().validate()) {
            alertify.error('"Parent Id" must be a valid integer.')
        }
    } else {
        saveTag($('#addTagForm'), addTagRow);
        //clear modal inputs
        $('#addTagModal input').val('');
    }
}

function addTagRow(json) {
    if (typeof json.tag.parent === 'undefined') {
        parentId = '';
    } else {
        parentId = json.tag.parent.id;
    }
    
    //add row into table
    table = $('#tagsTable').DataTable();
    newRow = table.row.add([
        json.tag.id,
        json.tag.name,
        parentId,
        '<a data-remodal-target="editTagModal" data-tag-id="' + json.tag.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: deleteTag(\'' + deleteUrl + '?id=' + json.tag.id + '\', ' + json.tag.id + ');" class="button deleteButton">Delete</a>'
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

function editButtonClickHandler(event) {
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

function editTag() {
    if (false == $('#editTagForm').parsley().isValid()) {
        //validate() returns an object if validation fails, hence the 'true !== ...'
        if (true !== $('#editTagNameInput').parsley().validate()) {
            alertify.error('"Name" cannot be blank.')
        }
        if (true !== $('#editTagParentInput').parsley().validate()) {
            alertify.error('"Parent Id" must be a valid integer.')
        }
    } else {
        saveTag($('#editTagForm'), updateTagRow);
        //clear modal inputs
        $('#editTagModal input').val('');
    }
}

function updateTagRow(json) {
    if (typeof json.tag.parent === 'undefined') {
        parentId = '';
    } else {
        parentId = json.tag.parent.id;
    }
    
    //get row
    row = $('tr[data-tag-id="' + json.tag.id + '"]');
        
    //update row in table
    table = $('#tagsTable').DataTable();
    table.row(row).data([
        json.tag.id,
        json.tag.name,
        parentId,
        '<a data-remodal-target="editTagModal" data-tag-id="' + json.tag.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: deleteTag(\'' + deleteUrl + '?id=' + json.tag.id + '\', ' + json.tag.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold');
}

function deleteTag(deleteUrl, tagId) {
    clearStackTraceContainer();
    
    $.ajax({
        url: deleteUrl,
        type: 'POST',
        success: function(json) {
            if (TRUE === json[JSON_KEY_SUCCESS]) {
                alertify.success(json[JSON_KEY_MESSAGE]);
                
                //remove row from the table
                table = $('#tagsTable').DataTable();
                table.row($('tr[data-tag-id="' + tagId + '"]')).remove().draw('full-hold');
            } else {
                alertify.error(json[JSON_KEY_MESSAGE]);
            }
        },
        error: function(data) {
            ajaxErrorHandler(data, 'Sorry, a problem occurred during delete and no info was given.');
        }
    })
}

function ajaxSuccessHandler(json) {
    if (TRUE === json[JSON_KEY_SUCCESS]) {
        alertify.success(json[JSON_KEY_MESSAGE]);
        return true;
    } else {
        alertify.error(json[JSON_KEY_MESSAGE]);
        return false;
    }
}

function ajaxErrorHandler(data, defaultMessage) {
    if (data && typeof data !== 'undefined') {
        showStackTrace(data.responseText);
    } else {
        alertify.error(defaultMessage);
    }
}

function showStackTrace(message) {
    stackTraceContainer = $('#stackTraceContainer');
    stackTraceContainer.html(message);
    show(stackTraceContainer);
}

function clearStackTraceContainer() {
    stackTraceContainer = $('#stackTraceContainer');
    stackTraceContainer.html('');
    hide(stackTraceContainer);
}

function addEventHandlers() {
    $(document).on('click', '.editButton', editButtonClickHandler);
    $('#addTagButton').click(addTagButtonClickHandler);
}

function initTagTable() {
    $('#tagsTable').DataTable({
        'aoColumnDefs': [
            //do not allow sorting on the columns containing the 'Edit' and 'Delete' buttons
            {'bSortable': false, 'aTargets': ['editColumn']},
            {'bSortable': false, 'aTargets': ['deleteColumn']}
        ]}
    );
}

$(document).ready(function() {
    initTagTable();
    addEventHandlers();
});