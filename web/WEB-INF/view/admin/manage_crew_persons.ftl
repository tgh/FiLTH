<@layout.standard "FiLTH Admin: Manage Crew Persons">
    <@util.css "admin" />
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    <@util.css "third-party/parsley/parsley" />
    
    <h1>Manage Crew Persons</h1>
    
    <#-- Crew Person table -->
    <table id="crewPersonTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Full Name</th>
                <th>Last Name</th>
                <th>First Name</th>
                <th>Middle Name</th>
                <th>Position</th>
                <th class="editColumn"></th>
            </tr>
        </thead>
        <tbody>
            <#foreach crewPerson in crewPersons>
                <#assign rowCssClass = "odd" />
                <#if crewPerson_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
            
                <tr class="${rowCssClass}" data-crewPerson-id="${crewPerson.id}">
                    <td class="crewPersonId">${crewPerson.id}</td>
                    <td class="crewPersonFullName">${crewPerson.fullName}</td>
                    <td class="crewPersonLastName">${crewPerson.lastName}</td>
                    <#if crewPerson.firstName??>
                        <td class="crewPersonFirstName">${crewPerson.firstName}</td>
                    <#else>
                        <td class="crewPersonFirstName"></td>
                    </#if>
                    <#if crewPerson.middleName??>
                        <td class="crewPersonMiddleName">${crewPerson.middleName}</td>
                    <#else>
                        <td class="crewPersonMiddleName"></td>
                    </#if>
                    <td class="crewPersonPosition">${crewPerson.positionKnownAs}</td>
                    <td>
                        <a data-remodal-target="editCrewPersonModal" data-crewPerson-id="${crewPerson.id}" class="button editButton">Edit</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add crewPerson button-->
    <a data-remodal-target="addCrewPersonModal" class="addButton button">Add Crew Person</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add crewPerson modal -->
    <div id="addCrewPersonModal" class="remodal modal inputModal" data-remodal-id="addCrewPersonModal" data-remodal-options="hashTracking: false">
        <h2>New Crew Person</h2>
        
        <div>
            <p>Note: if the person only has one name (e.g. Cher, Costa-Gavras, etc) then enter that name as the <i>last</i> name.</p>
        </div>
        
        <div class="modalInputContainer">
            <form id="addCrewPersonForm" action="${links.getLinkToSaveCrewPerson()}" method="POST">
                <table>
                    <tr>
                        <td class="label">First Name: </td>
                        <td><input id="addCrewPersonFirstNameInput" type="text" name="firstName" data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Middle Name: </td>
                        <td><input id="addCrewPersonMiddleNameInput" type="text" name="middleName" data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Last Name: </td>
                        <td><input id="addCrewPersonLastNameInput" type="text" name="lastName" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Postition: </td>
                        <td><input id="addCrewPersonPositionInput" type="text" name="positionKnownAs" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: crewPersonManager.addCrewPerson();" class="button modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit crewPerson modal -->
    <div id="editCrewPersonModal" class="remodal modal inputModal" data-remodal-id="editCrewPersonModal" data-remodal-options="hashTracking: false">
        <h2>Edit Crew Person</h2>
        
        <div>
            <p class="alignLeft"><b>NOTE:</b> if the person only has one name (e.g. Cher, Costa-Gavras, etc) then enter that name as the <i>last</i> name.</p>
        </div>
        
        <div class="modalInputContainer">
            <form id="editCrewPersonForm" action="${links.getLinkToSaveCrewPerson()}" method="POST">
                <input id="editCrewPersonIdInput" type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label">First Name: </td>
                        <td><input id="editCrewPersonFirstNameInput" type="text" name="firstName" data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Middle Name: </td>
                        <td><input id="editCrewPersonMiddleNameInput" type="text" name="middleName" data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Last Name: </td>
                        <td><input id="editCrewPersonLastNameInput" type="text" name="lastName" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Postition: </td>
                        <td><input id="editCrewPersonPositionInput" type="text" name="positionKnownAs" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: crewPersonManager.editCrewPerson();" class="button modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    

    <@util.js "admin/EntityManager" />
    <@util.js "admin/CrewPersonManager" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "third-party/parsley/parsley.min" />
</@layout.standard>