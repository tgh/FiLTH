<@layout.standard "FiLTH Admin: Manage MPAA Ratings">
    <@util.css "admin" />
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    <@util.css "third-party/parsley/parsley" />
    
    <h1>Manage MPAA Ratings</h1>
    
    <#-- MpaaRatings table -->
    <table id="mpaaRatingTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Rating</th>
                <th class="editColumn"></th>
            </tr>
        </thead>
        <tbody>
            <#foreach mpaaRating in mpaaRatings>
                <#assign rowCssClass = "odd" />
                <#if mpaaRating_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
                
                <tr class="${rowCssClass}" data-mpaaRating-id="${mpaaRating.id}">
                    <td class="mpaaRatingId">${mpaaRating.id}</td>
                    <td class="mpaaRatingCode">${mpaaRating.ratingCode}</td>
                    <td>
                        <a data-remodal-target="editMpaaRatingModal" data-mpaaRating-id="${mpaaRating.id}" class="button editButton">Edit</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add MPAA Rating button-->
    <a data-remodal-target="addMpaaRatingModal" class="addButton button">Add MPAA Rating</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add MPAA Rating modal -->
    <div id="addMpaaRatingModal" class="remodal modal inputModal" data-remodal-id="addMpaaRatingModal" data-remodal-options="hashTracking: false">
        <h2>New MPAA Rating</h2>
        
        <div class="modalInputContainer">
            <form id="addMpaaRatingForm" action="${links.getLinkToSaveMpaaRating()}" method="POST">
                <table>
                    <tr>
                        <td class="label">Rating Code: </td>
                        <td><input id="addMpaaRatingCodeInput" type="text" name="ratingCode" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: mpaaRatingManager.addMpaaRating();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit MPAA Rating modal -->
    <div id="editMpaaRatingModal" class="remodal modal inputModal" data-remodal-id="editMpaaRatingModal" data-remodal-options="hashTracking: false">
        <h2>Edit MPAA Rating</h2>
        
        <div class="modalInputContainer">
            <form id="editMpaaRatingForm" action="${links.getLinkToSaveMpaaRating()}" method="POST">
                <input id="editMpaaRatingIdInput" type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label">Rating Code: </td>
                        <td><input id="editMpaaRatingCodeInput" type="text" name="ratingCode" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: mpaaRatingManager.editMpaaRating();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    

    <@util.js "admin/EntityManager" />
    <@util.js "admin/MpaaRatingManager" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "third-party/parsley/parsley.min" />
</@layout.standard>