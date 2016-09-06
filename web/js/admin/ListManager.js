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

ListManager.prototype.editButtonClickHandler = function(event) {
    //nothing to do here
}

$(document).ready(function() {
    listManager = new ListManager();
});