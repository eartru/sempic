/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.SempicModelException;
import fr.uga.miashs.sempic.entities.Album;
import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.qualifiers.SelectedAlbum;
import fr.uga.miashs.sempic.qualifiers.SelectedPhoto;
import fr.uga.miashs.sempic.rdf.RDFStore;
import fr.uga.miashs.sempic.services.PhotoFacade;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import javax.annotation.PostConstruct;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;


/**
 *
 */
@Named
@ViewScoped
public class ViewPhoto implements Serializable {
    
    @Inject
    @SelectedPhoto
    private Photo current;
    
    @Inject
    private PhotoFacade service;
    
        
    public ViewPhoto() {
        
    }
    
    @PostConstruct
    public void init() {
    }
    
    public String update() {
        System.out.println("hello");
        RDFStore s = new RDFStore();
        
        try {
            Resource pRes = s.createPhoto(current.getId(), current.getAlbum().getId(), current.getAlbum().getOwner().getId());

            Model m = ModelFactory.createDefaultModel();

            Resource someone = m.createResource(SempicOnto.Person);
            someone.addLiteral(RDFS.label, "Georges");
            m.add(pRes, SempicOnto.depicts, someone);

            m.write(System.out, "turtle");
            pRes.getModel().write(System.out);
            s.saveModel(m);
            
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
            return "failure";
        }
        
        return "success";
    }

    public Photo getCurrent() {
        return current;
    }

    public void setCurrent(Photo current) {
        this.current = current;
    }
    
}
