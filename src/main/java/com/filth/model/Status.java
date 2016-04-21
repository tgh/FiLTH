package com.filth.model;

/**
 * This enum is used for the status of awards such as Oscar and Tyler.
 * For example, the Status of the {@link MovieOscar} object for
 * "No Country For Old Men" Best Picture category would be WON.
 */
public enum Status {
    NOMINATED("Nominated"),
    WON("Won"),
    TIED("Tied");
    
    private String _displayText;
    
    private Status(String displayName) {
        _displayText = displayName;
    }
    
    public String getDisplayText() {
        return _displayText;
    }
    
    @Override
    public String toString() {
        return _displayText;
    }
}
