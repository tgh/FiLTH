<@layout.admin "Manage Countries">
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    <@util.css "third-party/parsley/parsley" />
    
    <h1>Manage Countries</h1>
    
    <#-- Countries table -->
    <table id="countryTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Name</th>
                <th class="editColumn"></th>
            </tr>
        </thead>
        <tbody>
            <#foreach country in countries>
                <#assign rowCssClass = "odd" />
                <#if country_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
                
                <tr class="${rowCssClass}" data-country-id="${country.id}">
                    <td class="countryId">${country.id}</td>
                    <td class="countryName">${country.name}</td>
                    <td>
                        <a data-remodal-target="editCountryModal" data-country-id="${country.id}" class="button editButton">Edit</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add Country button-->
    <a data-remodal-target="addCountryModal" class="addButton button">Add Country</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add Country modal -->
    <div id="addCountryModal" class="remodal modal inputModal" data-remodal-id="addCountryModal" data-remodal-options="hashTracking: false">
        <h2>New Country</h2>
        
        <div class="modalInputContainer">
            <form id="addCountryForm" action="${links.getLinkToSaveCountry()}" method="POST">
                <table>
                    <tr>
                        <td class="label">Name: </td>
                        <td><input id="addCountryNameInput" type="text" name="name" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: countryManager.addCountry();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit Country modal -->
    <div id="editCountryModal" class="remodal modal inputModal" data-remodal-id="editCountryModal" data-remodal-options="hashTracking: false">
        <h2>Edit Country</h2>
        
        <div class="modalInputContainer">
            <form id="editCountryForm" action="${links.getLinkToSaveCountry()}" method="POST">
                <input id="editCountryIdInput" type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label">Rating Code: </td>
                        <td><input id="editCountryNameInput" type="text" name="name" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: countryManager.editCountry();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    

    <@util.js "admin/EntityManager" />
    <@util.js "admin/CountryManager" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "third-party/parsley/parsley.min" />
</@layout.admin>