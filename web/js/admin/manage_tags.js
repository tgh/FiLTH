function saveTag(successCallback) {
    clearStackTraceContainer();
    
    $('#saveTagForm').ajaxSubmit({
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
    saveTag(addTagRow);
    //clear modal inputs
    $('#addTagContainer input').val('');
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

$(document).ready(function() {
    initDataTable('#tagsTable');
});