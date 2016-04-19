package com.filth.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="crew_person")
public class CrewPerson {
    
    @Id
    @Column(name="cid")
    @SequenceGenerator(name="crew_person_seq", sequenceName="crew_person_cid_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="crew_person_seq")
    private int _id;
    
    @Column(name="last_name")
    private String _lastName;
    
    @Column(name="first_name")
    private String _firstName;
    
    @Column(name="middle_name")
    private String _middleName;
    
    @Column(name="full_name")
    private String _fullName;
    
    @Column(name="known_as")
    private String _positionKnownAs;
    
    @OneToMany(mappedBy="_crewPerson", fetch=FetchType.EAGER)
    private Set<MovieCrewPerson> _movieCrewPersons;
    
    @OneToMany(mappedBy="_crewPerson", fetch=FetchType.EAGER)
    private Set<MovieOscar> _movieOscars;
    
    @OneToMany(mappedBy="_crewPerson", fetch=FetchType.EAGER)
    private Set<MovieTyler> _movieTylers;
    
    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getLastName() {
        return _lastName;
    }

    public void setLastName(String lastName) {
        _lastName = lastName;
    }

    public String getFirstName() {
        return _firstName;
    }

    public void setFirstName(String firstName) {
        _firstName = firstName;
    }

    public String getMiddleName() {
        return _middleName;
    }

    public void setMiddleName(String middleName) {
        _middleName = middleName;
    }

    public String getFullName() {
        return _fullName;
    }

    public void setFullName(String fullName) {
        _fullName = fullName;
    }

    public String getPositionKnownAs() {
        return _positionKnownAs;
    }

    public void setPositionKnownAs(String positionKnownAs) {
        _positionKnownAs = positionKnownAs;
    }
    
    public Set<MovieCrewPerson> getMovieCrewPersons() {
        return _movieCrewPersons;
    }
    
    public void setMovieCrewPersons(Set<MovieCrewPerson> movieCrewPersons) {
        _movieCrewPersons = movieCrewPersons;
    }
    
    public Set<MovieOscar> getMovieOscars() {
        return _movieOscars;
    }
    
    public void setMovieOscars(Set<MovieOscar> movieOscars) {
        _movieOscars = movieOscars;
    }
    
    public Set<MovieTyler> getMovieTylers() {
        return _movieTylers;
    }
    
    public void setMovieTylers(Set<MovieTyler> movieTylers) {
        _movieTylers = movieTylers;
    }

    @Override
    public String toString() {
        return "CrewPerson (" + _id + ", " + _fullName + " (" + _positionKnownAs + "))";
    }

}
