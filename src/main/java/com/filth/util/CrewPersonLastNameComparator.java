package com.filth.util;

import java.util.Comparator;

import com.filth.model.CrewPerson;

public class CrewPersonLastNameComparator implements Comparator<CrewPerson> {

    @Override
    public int compare(CrewPerson crewPerson1, CrewPerson crewPerson2) {
        return crewPerson1.getLastName().compareTo(crewPerson2.getLastName());
    }

}
