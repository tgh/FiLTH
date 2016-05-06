<@layout.standard "FiLTH Admin: Manage Movie Sequences">
    <@util.css "admin" />
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    <@util.css "third-party/parsley/parsley" />
    
    <h1>Manage Movie Sequences</h1>
    
    <#-- Movie Sequences table -->
    <table id="sequenceTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Name</th>
                <th>Type</th>
                <th class="editColumn"></th>
            </tr>
        </thead>
        <tbody>
            <#foreach sequence in sequences>
                <#assign rowCssClass = "odd" />
                <#if sequence_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
                
                <tr class="${rowCssClass}" data-sequence-id="${sequence.id}">
                    <td class="sequenceId">${sequence.id}</td>
                    <td class="sequenceName">${sequence.name}</td>
                    <td class="sequenceType">${sequence.sequenceType}</td>
                    <td>
                        <a data-remodal-target="editSequenceModal" data-sequence-id="${sequence.id}" class="button editButton">Edit</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add sequence button-->
    <a data-remodal-target="addSequenceModal" class="addButton button">Add Sequence</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add sequence modal -->
    <div id="addSequenceModal" class="remodal modal inputModal" data-remodal-id="addSequenceModal" data-remodal-options="hashTracking: false">
        <h2>New Sequence</h2>
        
        <div class="modalInputContainer">
            <form id="addSequenceForm" action="${links.getLinkToSaveMovieSequence()}" method="POST">
                <table>
                    <tr>
                        <td class="label">Name: </td>
                        <td><input id="addSequenceNameInput" type="text" name="name" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Type: </td>
                        <td>
                            <select id="addSequenceTypeInput" name="type">
                                <#list sequenceTypes as type>
                                    <option value="${type}">${type}</option>
                                </#list>
                            </select>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: sequenceManager.addSequence();" class="button modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit sequence modal -->
    <div id="editSequenceModal" class="remodal modal inputModal" data-remodal-id="editSequenceModal" data-remodal-options="hashTracking: false">
        <h2>Edit Sequence</h2>
        
        <div class="modalInputContainer">
            <form id="editSequenceForm" action="${links.getLinkToSaveMovieSequence()}" method="POST">
                <input id="editSequenceIdInput" type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label">Name: </td>
                        <td><input id="editSequenceNameInput" type="text" name="name" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                    <tr>
                        <td class="label">Type: </td>
                        <td>
                            <select id="editSequenceTypeInput" name="type">
                                <#list sequenceTypes as type>
                                    <option value="${type}">${type}</option>
                                </#list>
                            </select>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: sequenceManager.editSequence();" class="button modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    

    <@util.js "admin/EntityManager" />
    <@util.js "admin/MovieSequenceManager" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "third-party/parsley/parsley.min" />
</@layout.standard>