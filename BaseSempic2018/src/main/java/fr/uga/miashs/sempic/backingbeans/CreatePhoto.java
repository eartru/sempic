/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.qualifiers.SelectedAlbum;
import fr.uga.miashs.sempic.SempicModelException;
import fr.uga.miashs.sempic.entities.Album;
import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.rdf.RDFStore;
import fr.uga.miashs.sempic.services.AlbumFacade;
import fr.uga.miashs.sempic.services.PhotoFacade;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

/**
 *
 *
 */
@Named
@ViewScoped
public class CreatePhoto implements Serializable {
    
    @Inject
    @SelectedAlbum
    private Album selectedAlbum;
    
    @EJB
    private RDFStore rDFStore;
    
    private List<Part> files;

    @Inject
    private PhotoFacade service;
    
    public CreatePhoto() {
        
    }
    
    @PostConstruct
    public void init() {
    }


    
    public List<Part> getFiles() {
        return files;
    }

    public void setFiles(List<Part> files) {
        this.files = files;
        //current.setFilename("");//file.getName());
    }
    
    /**
     * Create a photo in database and semanticaly
     * @return
     */
    public String create() {
        if (selectedAlbum==null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("parameter albumId must be given"));
            return "failure";
        }
        boolean partiallyFailed=false;
        for (Part p : files) {
            Photo current=new Photo();
            current.setAlbum(selectedAlbum);
            try {
                service.create(current,p.getInputStream());
                rDFStore.createPhoto(current.getId(), current.getAlbum().getId(), current.getAlbum().getOwner().getId());
            } catch (SempicModelException ex) {
               FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
               partiallyFailed=true; 
               
            } catch (IOException ex) {
                 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
                 partiallyFailed=true;
            }
        }
        if (partiallyFailed) {
             return "failure";
        }
        else {
            init();
            return "success";
        }
    }
}
