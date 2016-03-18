package com.filth.link;

/**
 * Link generator interface for {@link ManageTagsController}.
 */
public interface ManageTagsLinkGenerator {

    public Link getLinkToManageTags();
    public Link getLinkToDeleteTag(int id);
    public Link getLinkToSaveTag();
    
}
