package com.filth.link;

public interface ManageListsLinkGenerator {

    public Link getLinkToManageLists();
    public Link getLinkToDeleteList();
    public Link getLinkToDeleteList(int id);
    public Link getLinkToSaveList();
    public Link getLinkToNewList();
    public Link getLinkToList(int id);
    public Link getLinkToRemoveMovieFromList(int listId, int movieId);
    
}
