package com.filth.link;

/**
 * Link generator interface for {@link ManageTylersController}.
 */
public interface ManageTylersLinkGenerator {

    public Link getLinkToManageTylers();
    public Link getLinkToDeleteTyler();
    public Link getLinkToDeleteTyler(int id);
    public Link getLinkToSaveTyler();
    
}
