package com.filth.controller.admin;

public abstract class ManageEntityController extends AdminController {
    
    /** [entity] "[entity name or id]" has been deleted." */
    protected static final String DELETE_SUCCESS_MESSAGE_FORMAT = "%s \"%s\" has been deleted.";
    /** An error occurred deleting [entity] "[entity name or id]". */
    protected static final String DELETE_ERROR_MESSAGE_FORMAT = "An error occurred deleting %s \"%s\".";
    /** [entity] "[entity name or id]" saved. */
    protected static final String SAVE_SUCCESS_MESSAGE_FORMAT = "%s \"%s\" saved.";
    /** An error occurred saving [entity] "[entity name or id]". */
    protected static final String SAVE_ERROR_MESSAGE_FORMAT = "An error occurred saving %s \"%s\".";
}
