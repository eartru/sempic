/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.SempicModelException;
import fr.uga.miashs.sempic.SempicModelUniqueException;
import fr.uga.miashs.sempic.services.SempicUserFacade;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.rdf.RDFStore;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.validation.constraints.NotBlank;

/**
 *
 *
 */
@Named
@ViewScoped
public class CreateUser implements Serializable {
    
    private SempicUser current;
    
    @Inject
    private SempicUserFacade service;
    
    @Inject
    private transient Pbkdf2PasswordHash hashAlgo;

    
    public CreateUser() {
    }
    
    @PostConstruct
    public void init() {
        current=new SempicUser();
    }


    public SempicUser getCurrent() {
        return current;
    }

    public void setCurrent(SempicUser current) {
        this.current = current;
    }
    
    public String getPassword() {
        return null;
    }
    
    
    public void setPassword(@NotBlank(message="Password is required") String password) {
        getCurrent().setPasswordHash(hashAlgo.generate(password.toCharArray()));
    }
    
    /*public String generateHash(String s) {
        return hashAlgo.generate(s.toCharArray());
    }*/

    /**
     * Create a user in database and semanticaly
     * @return
     */
    public String create() {
        System.out.println(current);
        
        try {
            service.create(current);
            RDFStore s = new RDFStore();
            s.createPerson(current.getFirstname(), current.getLastname(), current.getGender());
        } 
        catch (SempicModelUniqueException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("un utilisateur avec cette adresse mail existe déjà"));
            return "failure";
        }
        catch (SempicModelException ex) {
           FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
            return "failure";
        }
        
        return "success";
    }
}
