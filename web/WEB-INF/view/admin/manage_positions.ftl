<@layout.standard "FiLTH Admin: Manage Positions">
    <@util.css "admin" />
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    <@util.css "third-party/parsley/parsley" />
    
    <h1>Manage Positions</h1>
    
    <#-- Positions table -->
    <table id="positionTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th class="editColumn"></th>
            </tr>
        </thead>
        <tbody>
            <#foreach position in positions>
                <#assign rowCssClass = "odd" />
                <#if position_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
                
                <tr class="${rowCssClass}" data-position-id="${position.id}">
                    <td class="positionId">${position.id}</td>
                    <td class="positionTitle">${position.title}</td>
                    <td>
                        <a data-remodal-target="editPositionModal" data-position-id="${position.id}" class="button editButton">Edit</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add Position button-->
    <a data-remodal-target="addPositionModal" class="addButton button">Add Position</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add Position modal -->
    <div id="addPositionModal" class="remodal modal inputModal" data-remodal-id="addPositionModal" data-remodal-options="hashTracking: false">
        <h2>New Position</h2>
        
        <div class="modalInputContainer">
            <form id="addPositionForm" action="${links.getLinkToSavePosition()}" method="POST">
                <table>
                    <tr>
                        <td class="label">Position Title: </td>
                        <td><input id="addPositionTitleInput" type="text" name="title" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: positionManager.addPosition();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit Position modal -->
    <div id="editPositionModal" class="remodal modal inputModal" data-remodal-id="editPositionModal" data-remodal-options="hashTracking: false">
        <h2>Edit Position</h2>
        
        <div class="modalInputContainer">
            <form id="editPositionForm" action="${links.getLinkToSavePosition()}" method="POST">
                <input id="editPositionIdInput" type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label"> Position Title: </td>
                        <td><input id="editPositionTitleInput" type="text" name="title" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: positionManager.editPosition();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    

    <@util.js "admin/EntityManager" />
    <@util.js "admin/PositionManager" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "third-party/parsley/parsley.min" />
</@layout.standard>