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
     
    public String add() {
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
    
}
