/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.validation.constraints.*;

/**
 *
 *
 */

public class Person implements Serializable {
        
    private long Id;
    private String firstname;
    private String lastname;
    private String gender;

    public Person() {
    }
    
    public Person(long id, String firstname, String lastname, String gender) {
       this.Id = id;
       this.firstname = firstname;
       this.lastname = lastname;
       this.gender = gender;
    }
    
    public long getId() {
        return Id;
    }

    public void setId(long id) {
        this.Id = id;
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

}
