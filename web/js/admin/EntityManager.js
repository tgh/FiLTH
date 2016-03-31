EntityManager = function(entityName) {
    this.entityName = entityName;
    this.DOM_KEYS = {
        table: '#' + entityName + 'Table',
        addForm: '#add' + entityName.capitalizeFirstLetter() + 'Form',
        stackTraceContainer: '#stackTraceContainer',
        editButton: '.editButton',
        addButton: '.addButton'
    };
    
    this.initTable(this.DOM_KEYS.table);
    this.addEventHandlers();
};

EntityManager.prototype.initTable = function(table) {
    $(table).DataTable({
        'aoColumnDefs': [
            //do not allow sorting on the columns containing the 'Edit' and 'Delete' buttons
            {'bSortable': false, 'aTargets': ['editColumn']},
            {'bSortable': false, 'aTargets': ['deleteColumn']}
        ]}
    );
}

EntityManager.prototype.addEventHandlers = function() {
    //(these handler functions are defined in objects extending from EntityManager)
    $(document).on('click', this.DOM_KEYS.editButton, this.editButtonClickHandler);
    $(this.DOM_KEYS.addButton).click(this.addButtonClickHandler.bind(this));
}

EntityManager.prototype.addButtonClickHandler = function() {
    //clear the add form validation UI
    $(this.DOM_KEYS.addForm).parsley().reset();
}

EntityManager.prototype.clearStackTraceContainer = function() {
    stackTraceContainer = $(this.DOM_KEYS.stackTraceContainer);
    stackTraceContainer.html('');
    hide(stackTraceContainer);
};

EntityManager.prototype.showStackTrace = function(message) {
    stackTraceContainer = $(this.DOM_KEYS.stackTraceContainer);
    stackTraceContainer.html(message);
    show(stackTraceContainer);
}

EntityManager.prototype.ajaxSuccessHandler = function(json) {
    if (TRUE === json[JSON_KEY_SUCCESS]) {
        alertify.success(json[JSON_KEY_MESSAGE]);
        return true;
    } else {
        alertify.error(json[JSON_KEY_MESSAGE]);
        return false;
    }
};

EntityManager.prototype.ajaxErrorHandler = function(data, defaultMessage) {
    if (data && typeof data !== 'undefined') {
        this.showStackTrace(data.responseText);
    } else {
        alertify.error(defaultMessage);
    }
};

EntityManager.prototype.saveEntity = function(form, successCallback) {
    this.clearStackTraceContainer();
    self = this;
    
    form.ajaxSubmit({
       success: function(json) {
           success = self.ajaxSuccessHandler(json);
           if (success) {
               successCallback(json);
           }
       },
       error: function(data) {
           self.ajaxErrorHandler(data, 'Sorry, a problem occurred during save and no info was given.');
       }
    });
    
    //close the modal
    $('.modalCancelButton').trigger('click');
};

EntityManager.prototype.deleteEntity = function(deleteUrl, entityId) {
    this.clearStackTraceContainer();
    self = this;
    
    $.ajax({
        url: deleteUrl,
        type: 'POST',
        success: function(json) {
            if (TRUE === json[JSON_KEY_SUCCESS]) {
                alertify.success(json[JSON_KEY_MESSAGE]);
                
                //remove row from the table
                table = $(self.DOM_KEYS.table).DataTable();
                table.row($('tr[data-' + self.entityName + '-id="' + entityId + '"]')).remove().draw('full-hold');
            } else {
                alertify.error(json[JSON_KEY_MESSAGE]);
            }
        },
        error: function(data) {
            self.ajaxErrorHandler(data, 'Sorry, a problem occurred during delete and no info was given.');
        }
    })
}