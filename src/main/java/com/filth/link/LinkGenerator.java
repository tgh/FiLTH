package com.filth.link;

/**
 * Interface for *all* links in the application generated through the Controllers
 * via their corresponding implemented *LinkGenerator interface.
 */
public interface LinkGenerator
    extends
        ManageOscarsLinkGenerator,
        ManageTagsLinkGenerator,
        ManageListsLinkGenerator,
        ManageTylersLinkGenerator
{ }