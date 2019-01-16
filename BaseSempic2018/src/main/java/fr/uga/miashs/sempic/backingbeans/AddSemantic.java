/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.entities.Person;
import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.rdf.Namespaces;
import fr.uga.miashs.sempic.rdf.RDFStore;

/**
 *
 *
 */
@Named
@ViewScoped
public class AddSemantic implements Serializable {
    
    @Inject
    @SelectedUser
    private SempicUser current;
    
    private String firstname;
    private String lastname;
    private String gender;
    private String object;
    private String event;
    private String eventType = "gloabal";
    private String animal;
   
    public SempicUser getUser() {
        return current;
    }

    public SempicUser getCurrent() {
        return current;
    }

    public void setCurrent(SempicUser current) {
        this.current = current;
    }
    
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
     public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
     public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }
    
     
    public String addPerson() {
        boolean partiallyFailed = false;
        
        RDFStore s = new RDFStore();
        try {
            s.createPerson(firstname, lastname, gender);
        } catch (Exception e) {
            partiallyFailed = true;
        }
        
        if (partiallyFailed)
        {
            return "failure";
        } else {
            Person p = new Person(Namespaces.getPersonUri(firstname, lastname), firstname, lastname, gender);
            return "success";
        }
    }
    
    public String addObject() {
        boolean partiallyFailed = false;
        
        RDFStore s = new RDFStore();
        try {
            s.createObject(object);
        } catch (Exception e) {
            partiallyFailed = true;
        }
        
        if (partiallyFailed)
        {
            return "failure";
        } else {
            return "success";
        }
    }
    
    public String addEvent() {
        boolean partiallyFailed = false;
        
        RDFStore s = new RDFStore();
        try {
            s.createEvent(event, eventType);
        } catch (Exception e) {
            partiallyFailed = true;
        }
        
        if (partiallyFailed)
        {
            return "failure";
        } else {
            return "success";
        }
    }
    public String addAnimal() {
        boolean partiallyFailed = false;
        
        RDFStore s = new RDFStore();
        try {
            s.createAnimal(animal);
        } catch (Exception e) {
            partiallyFailed = true;
        }
        
        if (partiallyFailed)
        {
            return "failure";
        } else {
            return "success";
        }
    }
    
}
