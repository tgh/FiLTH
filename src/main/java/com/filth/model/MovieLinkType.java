package com.filth.model;

/**
 * This enum is used to differentiate how two movies are linked together.
 * For example, since "Back to the Future Part II" is a sequel to "Back to 
 * the Future", the "Back to the Future Part II" -> "Back to the Future" link
 * is of the type SUCCESSOR_TO (and, conversely, the "Back to the Future" ->
 * "Back to the Future Part II" link is of the type PREDECESSOR_OF).
 */
public enum MovieLinkType {

    PREDECESSOR_OF,
    SUCCESSOR_TO,
    CHILD_OF,
    PARENT_TO,
    REMAKE_OF,
    RELATED_TO
    
}
