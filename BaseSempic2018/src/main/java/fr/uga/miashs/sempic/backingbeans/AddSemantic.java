/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.SempicModelException;
import fr.uga.miashs.sempic.entities.Album;
import fr.uga.miashs.sempic.entities.Person;
import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.qualifiers.SelectedAlbum;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.services.PhotoFacade;
import fr.uga.miashs.sempic.services.SempicUserFacade;
import fr.uga.miashs.sempic.services.SempicUserService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.rdf.RDFStore;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.RDF;

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
    
    @Inject
    private SempicUserFacade userService;
    
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
        RDFStore s = new RDFStore();
        
        Model m = ModelFactory.createDefaultModel();
        
        String personURI = "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Person/" 
                + firstname + lastname; 
          
        Resource someone = m.createResource(personURI);
        someone.addLiteral(RDFS.label, firstname + " " + lastname);
        someone.addProperty(RDF.type, SempicOnto.Person);
        
        m.write(System.out, "turtle");
            
        s.saveModel(m);
        
       return "success";
    }
    
}
