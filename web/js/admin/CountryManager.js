/**
 * CountryManager constructor
 */
function CountryManager() {
    EntityManager.call(this, 'country');
    
    //add Country-specific dom keys
    this.DOM_KEYS['addCountryNameInput'] = '#addCountryNameInput';
    this.DOM_KEYS['editCountryIdInput'] = '#editCountryIdInput';
    this.DOM_KEYS['editCountryNameInput'] = '#editCountryNameInput';
    
    //add country-specific messages
    this.MESSAGES['noName'] = '"Name" cannot be blank.';
}
CountryManager.prototype = Object.create(EntityManager.prototype);
CountryManager.prototype.constructor = CountryManager;


/** CountryManager functions */

/* Override */
CountryManager.prototype.initTable = function(table) {
    $(table).DataTable({
        'aoColumnDefs': [
            //do not allow sorting on the column containing the 'Edit' button
            {'bSortable': false, 'aTargets': ['editColumn']}
        ]}
    );
}

CountryManager.prototype.addCountry = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.addCountryNameInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noName)
    } else {
        this.saveEntity($(this.DOM_KEYS.addForm), this.addCountryRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.addModalInputs).val('');
    }
}

CountryManager.prototype.addCountryRow = function(json) {
    //add row into table
    table = $(this.DOM_KEYS.table).DataTable();
    newRow = table.row.add([
        json.country.id,
        json.country.name,
        '<a data-remodal-target="editCountryModal" data-country-id="' + json.country.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold')
      .nodes()
      .to$();
    
    //add data-country-id
    newRow.attr('data-country-id', json.country.id);
    //add classes to row
    $(newRow.find('td')[0]).addClass('countryId');
    $(newRow.find('td')[1]).addClass('countryName');
}

CountryManager.prototype.editButtonClickHandler = function(event) {
    countryId = event.target.dataset.countryId;
    
    //clear form input validation UI
    $(this.DOM_KEYS.editForm).parsley().reset();

    //get values from country row
    countryName = $('tr[data-country-id="' + countryId + '"] td.countryName').text();
    //set input values
    $(this.DOM_KEYS.editCountryIdInput).val(countryId);
    $(this.DOM_KEYS.editCountryNameInput).val(countryName);
}

CountryManager.prototype.editCountry = function() {
    //validate() returns an object if validation fails, hence the 'true !== ...'
    if (true !== $(this.DOM_KEYS.editCountryNameInput).parsley().validate()) {
        alertify.error(this.MESSAGES.noName)
    } else {
        this.saveEntity($(this.DOM_KEYS.editForm), this.updateCountryRow.bind(this));
        //clear modal inputs
        $(this.DOM_KEYS.editModalInputs).val('');
    }
}

CountryManager.prototype.updateCountryRow = function(json) {
    //get row
    row = $('tr[data-country-id="' + json.country.id + '"]');
        
    //update row in table
    table = $(this.DOM_KEYS.table).DataTable();
    table.row(row).data([
        json.country.id,
        json.country.name,
        '<a data-remodal-target="editCountryModal" data-country-id="' + json.country.id + '" class="button editButton">Edit</a>'
    ]).draw('full-hold');
}

$(document).ready(function() {
    countryManager = new CountryManager();
});