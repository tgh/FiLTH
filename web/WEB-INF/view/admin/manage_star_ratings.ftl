<@layout.admin "Manage Star Ratings">
    <@util.css "modal" />
    <@util.include_datatables_css />
    <@util.css "third-party/alertify/alertify.core" />
    <@util.css "third-party/alertify/alertify.default" />
    <@util.css "third-party/parsley/parsley" />
    
    <h1>Manage Star Ratings</h1>
    
    <#-- StarRatings table -->
    <table id="starRatingTable">
        <thead>
            <tr>
                <th>Id</th>
                <th>Rating</th>
                <th class="editColumn"></th>
            </tr>
        </thead>
        <tbody>
            <#foreach starRating in starRatings>
                <#assign rowCssClass = "odd" />
                <#if starRating_index % 2 == 0>
                    <#assign rowCssClass = "even" />
                </#if>
                
                <tr class="${rowCssClass}" data-starRating-id="${starRating.id}">
                    <td class="starRatingId">${starRating.id}</td>
                    <td class="starRating">${starRating.rating}</td>
                    <td>
                        <a data-remodal-target="editStarRatingModal" data-starRating-id="${starRating.id}" class="button editButton">Edit</a>
                    </td>
                </tr>
            </#foreach>
        </tbody>
    </table>
    
    <#-- Add Star Rating button-->
    <a data-remodal-target="addStarRatingModal" class="addButton button">Add Star Rating</a>
    
    <#-- Stacktrace container -->
    <div id="stackTraceContainer" class="hidden error"></div>
    
    <#-- Add Star Rating modal -->
    <div id="addStarRatingModal" class="remodal modal inputModal" data-remodal-id="addStarRatingModal" data-remodal-options="hashTracking: false">
        <h2>New Star Rating</h2>
        
        <div class="modalInputContainer">
            <form id="addStarRatingForm" action="${links.getLinkToSaveStarRating()}" method="POST">
                <table>
                    <tr>
                        <td class="label">Rating: </td>
                        <td><input id="addStarRatingInput" type="text" name="rating" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: starRatingManager.addStarRating();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    
    <#-- Edit Star Rating modal -->
    <div id="editStarRatingModal" class="remodal modal inputModal" data-remodal-id="editStarRatingModal" data-remodal-options="hashTracking: false">
        <h2>Edit Star Rating</h2>
        
        <div class="modalInputContainer">
            <form id="editStarRatingForm" action="${links.getLinkToSaveStarRating()}" method="POST">
                <input id="editStarRatingIdInput" type="hidden" name="id">
                <table>
                    <tr>
                        <td class="label">Rating: </td>
                        <td><input id="editStarRatingInput" type="text" name="rating" data-parsley-required data-parsley-errors-messages-disabled></td>
                    </tr>
                </table>
            </form>
        </div>
        
        <div class="modalButtons">
            <span class="modalButtonContainer"><a href="javascript: starRatingManager.editStarRating();" class="button buttonPrimary modalSaveButton">Save</a></span>
            <span class="modalButtonContainer"><a data-remodal-action="cancel" class="button modalCancelButton">Cancel</a></span>
        </div>
    </div>
    

    <@util.js "admin/EntityManager" />
    <@util.js "admin/StarRatingManager" />
    <@util.include_datatables_js />
    <@util.js "third-party/alertify/alertify.min" />
    <@util.js "third-party/parsley/parsley.min" />
</@layout.admin>