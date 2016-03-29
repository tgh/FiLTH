function saveTag(form, successCallback) {
    clearStackTraceContainer();
    
    form.ajaxSubmit({
       success: function(json) {
           ajaxSuccessHandler(json);
           successCallback(json);
       },
       error: function(data) {
           ajaxErrorHandler(data, 'Sorry, a problem occurred during save and no info was given.');
       }
    });
    
    //close the modal
    $('.modalCancelButton').trigger('click');
}

function addTag() {
    saveTag($('#addTagForm'), addTagRow);
    //clear modal inputs
    $('#addTagModal input').val('');
}

function addTagRow(json) {
    if (typeof json.tag.parent === 'undefined') {
        parentId = '';
    } else {
        parentId = json.tag.parent.id;
    }
    
    //add row into table
    table = $('#tagsTable').DataTable();
    table.row.add([
        json.tag.id,
        json.tag.name,
        parentId,
        '',
        ''
    ]).draw('full-hold');
}

function editButtonClickHandler(event) {
    tagId = event.target.dataset.tagId;
    
    //get values from tag row
    tagName = $('tr[data-tag-id="' + tagId + '"] td.tagName').text();
    tagParentId = $('tr[data-tag-id="' + tagId + '"] td.parentId').text();
    //set input values
    $('#editTagModal input[name="id"]').val(tagId);
    $('#editTagModal input[name="name"]').val(tagName);
    $('#editTagModal input[name="parent"]').val(tagParentId);
}

function editTag() {
    saveTag($('#editTagForm'), updateTagRow);
    //clear modal inputs
    $('#editTagModal input').val('');
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
        '',
        ''
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
    } else {
        alertify.error(json[JSON_KEY_MESSAGE]);
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
}

$(document).ready(function() {
    $('#tagsTable').DataTable({
        'aoColumnDefs': [
            //do not allow sorting on the columns containing the 'Edit' and 'Delete' buttons
            {'bSortable': false, 'aTargets': [-1]},
            {'bSortable': false, 'aTargets': [-2]}
        ]}
    );
    addEventHandlers();
});