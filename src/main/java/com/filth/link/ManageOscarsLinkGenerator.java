package com.filth.link;

/**
 * Link generator interface for {@link ManageOscarsController}.
 */
public interface ManageOscarsLinkGenerator {

    public Link getLinkToManageOscars();
    public Link getLinkToDeleteOscar(int id);
    public Link getLinkToEditOscar(int id);
    public Link getLinkToCreateOscar();
    public Link getLinkToSaveOscar();
    
}
