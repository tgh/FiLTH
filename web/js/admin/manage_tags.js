function saveTag() {
    saveResult = $('#saveResult');
    clearSaveResult(saveResult);
    
    $('#saveTagForm').ajaxSubmit({
       success: ajaxSuccessHandler,
       error: function(data) {
           if (data && typeof data !== 'undefined') {
               showSaveResultError(saveResult, data.responseText);
           } else {
               showSaveResultError(saveResult, 'Sorry, a problem occurred during save.');
           }
       }
    });
}

function deleteTag(deleteUrl, tagId) {
    saveResult = $('#saveResult');
    clearSaveResult(saveResult);
    
    $.ajax({
        url: deleteUrl,
        type: 'POST',
        success: function(json) {
            if (json[JSON_KEY_SUCCESS]) {
                saveResult.html(json[JSON_KEY_HTML]);
                saveResult.addClass(SUCCESS_CLASS);
                show(saveResult);
                
                $('tr[data-tag-id="' + tagId + '"]').remove();
            } else {
                showSaveResultError(saveResult, json[JSON_KEY_ERROR]);
            }
        },
        error: function(data) {
            if (data && typeof data !== 'undefined') {
                showSaveResultError(saveResult, data.responseText);
            } else {
                showSaveResultError(saveResult, 'Sorry, a problem occurred during delete.');
            }
        }
    })
}

function ajaxSuccessHandler(json) {
    if (json[JSON_KEY_SUCCESS]) {
        saveResult.html(json[JSON_KEY_HTML]);
        saveResult.addClass(SUCCESS_CLASS);
        show(saveResult);
    } else {
        showSaveResultError(saveResult, json[JSON_KEY_ERROR]);
    }
}

function showSaveResultError(saveResult, message) {
    saveResult.html(message);
    saveResult.addClass(ERROR_CLASS);
    show(saveResult);
}

function clearSaveResult(saveResult) {
    saveResult.html('');
    saveResult.removeClass(ERROR_CLASS);
    saveResult.removeClass(SUCCESS_CLASS);
    hide(saveResult);
}