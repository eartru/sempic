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
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueEmail", columnNames = {"email"})
})
@NamedQueries({
@NamedQuery(
        name = "findAllEager",
        query = "SELECT DISTINCT u FROM SempicUser u LEFT JOIN FETCH u.groups LEFT JOIN FETCH u.memberOf ",
        hints = {@QueryHint(name="eclipselink.refresh", value= "TRUE")}
),
@NamedQuery(
        name = "readByEmail",
        query = "SELECT DISTINCT u FROM SempicUser u WHERE u.email=:email "
)
})
public class SempicUser implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @NotBlank(message="Un nom de famille doit être donné")
    private String lastname;
    
    @NotBlank(message="Un prénom doit être donné")
    private String firstname;
    
    @Email
    @NotBlank(message="Une adresse mail doit être donnée")
    private String email;
    
    //@NotBlank(message="Un mot de passe doit être donné")
    private String passwordHash;
    
    
    @OneToMany(mappedBy = "owner",cascade = CascadeType.REMOVE)
    private Set<SempicGroup> groups;

    @ManyToMany(mappedBy = "members" )//,cascade = CascadeType.REMOVE)
    private Set<SempicGroup> memberOf;
    
    @Enumerated(EnumType.STRING)
    private SempicUserType userType;

    public SempicUser() {
        userType=SempicUserType.USER;
    }
    
    public long getId() {
        return id;
    }
    
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<SempicGroup> getGroups() {
        return Collections.unmodifiableSet(groups);
    }

    public Set<SempicGroup> getMemberOf() {
        return Collections.unmodifiableSet(memberOf);
    }
    
    public SempicUserType getUserType() {
        return userType;
    }

    public void setUserType(SempicUserType userType) {
        this.userType = userType;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SempicUser other = (SempicUser) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

 
    @Override
    public String toString() {
        return "SempicUser{id="+ id + ", "
                + "lastname=" + lastname + ", firstname=" + firstname + ", email=" + email + '}';
    }
}
