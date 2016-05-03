/**
 * CrewPersonManager constructor
 */
function CrewPersonManager() {
    EntityManager.call(this, 'crewPerson');
    
    //add crewPerson-specific dom keys
    this.DOM_KEYS['addCrewPersonFirstNameInput'] = '#addCrewPersonFirstNameInput';
    this.DOM_KEYS['addCrewPersonLastNameInput'] = '#addCrewPersonLastNameInput';
    this.DOM_KEYS['addCrewPersonMiddleNameInput'] = '#addCrewPersonMiddleNameInput';
    this.DOM_KEYS['addCrewPersonPositionInput'] = '#addCrewPersonPositionInput';
    this.DOM_KEYS['editCrewPersonIdInput'] = '#editCrewPersonIdInput';
    this.DOM_KEYS['editCrewPersonFirstNameInput'] = '#editCrewPersonFirstNameInput';
    this.DOM_KEYS['editCrewPersonLastNameInput'] = '#editCrewPersonLastNameInput';
    this.DOM_KEYS['editCrewPersonMiddleNameInput'] = '#editCrewPersonMiddleNameInput';
    this.DOM_KEYS['editCrewPersonPositionInput'] = '#editCrewPersonPositionInput';
    
    //add crewPerson-specific messages
    this.MESSAGES['noLastName'] = '"Last Name" cannot be blank.';
    this.MESSAGES['noPosition'] = '"Position" cannot be blank.';
}
CrewPersonManager.prototype = Object.create(EntityManager.prototype);
CrewPersonManager.prototype.constructor = CrewPersonManager;


/** CrewPersonManager functions */

/* Override */
CrewPersonManager.prototype.initTable = function(table) {
    $(table).DataTable({
        'aoColumnDefs': [
            //do not allow sorting on the column containing the 'Edit' button
            {'bSortable': false, 'aTargets': ['editColumn']}
        ]}
    );
}

CrewPersonManager.prototype.addCrewPerson = function() {
    if (false == $(this.DOM_KEYS.addForm).parsley().isValid()) {
        //validate() returns an object if validation fails, hence the 'true !== ...'
        if (true !== $(this.DOM_KEYS.addCrewPersonLastNameInput).parsley().validate()) {
            alertify.error(this.MESSAGES.noLastName)
        }
        if (true !== $(this.DOM_KEYS.addCrewPersonPositionInput).parsley().validate()) {
            alertify.error(this.MESSAGES.noPosition)
        }
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addCrewPersonRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

CrewPersonManager.prototype.addCrewPersonRow = function(json) {
    if (typeof json.crewPerson.firstName === 'undefined') {
        firstName = '';
    } else {
        firstName = json.crewPerson.firstName;
    }
    
    if (typeof json.crewPerson.middleName === 'undefined') {
        middleName = '';
    } else {
        middleName = json.crewPerson.middleName;
    }
    
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.crewPerson.id,
        json.crewPerson.fullName,
        json.crewPerson.lastName,
        firstName,
        middleName,
        json.crewPerson.positionKnownAs,
        '<a data-remodal-target="editCrewPersonModal" data-crewPerson-id="' + json.crewPerson.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-crewPerson-id
    newRow.attr('data-crewPerson-id', json.crewPerson.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('crewPersonId');
    $(newRow.find('td')[1]).addClass('crewPersonFullName');
    $(newRow.find('td')[2]).addClass('crewPersonLastName');
    $(newRow.find('td')[3]).addClass('crewPersonFirstName');
    $(newRow.find('td')[4]).addClass('crewPersonMiddleName');
    $(newRow.find('td')[5]).addClass('crewPersonPosition');
}

CrewPersonManager.prototype.editButtonClickHandler = function(event) {
    crewPersonId = event.target.dataset.crewpersonId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();
    
    //get values from crewPerson row
    crewPersonLastName = $('tr[data-crewPerson-id="' + crewPersonId + '"] td.crewPersonLastName').text();
    crewPersonFirstName = $('tr[data-crewPerson-id="' + crewPersonId + '"] td.crewPersonFirstName').text();
    crewPersonMiddleName = $('tr[data-crewPerson-id="' + crewPersonId + '"] td.crewPersonMiddleName').text();
    crewPersonPosition = $('tr[data-crewPerson-id="' + crewPersonId + '"] td.crewPersonPosition').text();
    //set input values
    $(this.DOM_KEYS.editCrewPersonIdInput).val(crewPersonId);
    $(this.DOM_KEYS.editCrewPersonLastNameInput).val(crewPersonLastName);
    $(this.DOM_KEYS.editCrewPersonFirstNameInput).val(crewPersonFirstName);
    $(this.DOM_KEYS.editCrewPersonMiddleNameInput).val(crewPersonMiddleName);
    $(this.DOM_KEYS.editCrewPersonPositionInput + ' option[value="' + crewPersonPosition + '"]').attr('selected','selected');
}

CrewPersonManager.prototype.editCrewPerson = function() {
    if (false == $(this.DOM_KEYS.editForm).parsley().isValid()) {
        //validate() returns an object if validation fails, hence the 'true !== ...'
        if (true !== $(this.DOM_KEYS.editCrewPersonLastNameInput).parsley().validate()) {
            alertify.error(this.MESSAGES.noLastName)
        }
        if (true !== $(this.DOM_KEYS.editCrewPersonPositionInput).parsley().validate()) {
            alertify.error(this.MESSAGES.noPosition)
        }
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updateCrewPersonRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

CrewPersonManager.prototype.updateCrewPersonRow = function(json) {
    if (typeof json.crewPerson.firstName === 'undefined') {
        firstName = '';
    } else {
        firstName = json.crewPerson.firstName;
    }
    
    if (typeof json.crewPerson.middleName === 'undefined') {
        middleName = '';
    } else {
        middleName = json.crewPerson.middleName;
    }
    
    //get row
    row = $('tr[data-crewPerson-id="' + json.crewPerson.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.crewPerson.id,
        json.crewPerson.fullName,
        json.crewPerson.lastName,
        firstName,
        middleName,
        json.crewPerson.positionKnownAs,
        '<a data-remodal-target="editCrewPersonModal" data-crewPerson-id="' + json.crewPerson.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    crewPersonManager = new CrewPersonManager();
});