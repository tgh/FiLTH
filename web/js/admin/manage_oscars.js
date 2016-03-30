function saveOscar(form, successCallback) {
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

function addOscarButtonClickHandler() {
    //clear the add oscar form validation UI
    $('#addOscarForm').parsley().reset();
}

function addOscar() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $('#addOscarCategoryInput').parsley().validate()) {
        alertify.error('"Category" cannot be blank.')
    } else {
        saveOscar($('#addOscarForm'), addOscarRow);
        //clear modal inputs
        $('#addOscarModal input').val('');
    }
}

function addOscarRow(json) {
    //add row into table
    table = $('#oscarsTable').DataTable();
    newRow = table.row.add([
        json.oscar.id,
        json.oscar.category,
        '<a data-remodal-target="editOscarModal" data-oscar-id="' + json.oscar.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: deleteOscar(\'' + deleteUrl + '?id=' + json.oscar.id + '\', ' + json.oscar.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-oscar-id
    newRow.attr('data-oscar-id', json.oscar.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('oscarId');
    $(newRow.find('td')[1]).addClass('oscarCategory');
}

function editButtonClickHandler(event) {
    oscarId = event.target.dataset.oscarId;
    
    //clear form input validation UI
    $('#editOscarForm').parsley().reset();
    
    //get values from oscar row
    oscarCategory = $('tr[data-oscar-id="' + oscarId + '"] td.oscarCategory').text();
    //set input values
    $('#editOscarModal input[name="id"]').val(oscarId);
    $('#editOscarModal input[name="category"]').val(oscarCategory);
}

function editOscar() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $('#editOscarCategoryInput').parsley().validate()) {
        alertify.error('"Category" cannot be blank.')
    } else {
        saveOscar($('#editOscarForm'), updateOscarRow);
        //clear modal inputs
        $('#editOscarModal input').val('');
    }
}

function updateOscarRow(json) {
    //get row
    row = $('tr[data-oscar-id="' + json.oscar.id + '"]');
        
    //update row in table
    table = $('#oscarsTable').DataTable();
    table.row(row).data([
        json.oscar.id,
        json.oscar.category,
        '<a data-remodal-target="editOscarModal" data-oscar-id="' + json.oscar.id + '" class="button editButton">Edit</a>',
        '<a href="javascript: deleteOscar(\'' + deleteUrl + '?id=' + json.oscar.id + '\', ' + json.oscar.id + ');" class="button deleteButton">Delete</a>'
    ]).draw('full-hold');
}

function deleteOscar(deleteUrl, oscarId) {
    clearStackTraceContainer();
    
    $.ajax({
        url: deleteUrl,
        type: 'POST',
        success: function(json) {
            if (TRUE === json[JSON_KEY_SUCCESS]) {
                alertify.success(json[JSON_KEY_MESSAGE]);
                
                //remove row from the table
                table = $('#oscarsTable').DataTable();
                table.row($('tr[data-oscar-id="' + oscarId + '"]')).remove().draw('full-hold');
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
    $('#addOscarButton').click(addOscarButtonClickHandler);
}

function initOscarTable() {
    $('#oscarsTable').DataTable({
        'aoColumnDefs': [
            //do not allow sorting on the columns containing the 'Edit' and 'Delete' buttons
            {'bSortable': false, 'aTargets': ['editColumn']},
            {'bSortable': false, 'aTargets': ['deleteColumn']}
        ]}
    );
}

$(document).ready(function() {
    initOscarTable();
    addEventHandlers();
});