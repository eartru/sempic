/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.SempicModelException;
import fr.uga.miashs.sempic.entities.Album;
import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.services.AlbumFacade;
import fr.uga.miashs.sempic.services.PhotoFacade;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author estelle
 */
@Named
@ViewScoped
public class DeleteAlbum implements Serializable {
      
    @Inject
    private AlbumFacade service;
    
    @Inject
    private PhotoFacade photoService;
    
    private List<Photo> listPhoto;
    
    public DeleteAlbum() {
    
    }
    
     @PostConstruct
    public void init() {
    }

    public String delete(Album selectedAlbum) {
        if (selectedAlbum==null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("parameter album must be given"));
            return "failure";
        }
        boolean partiallyFailed=false;
        try {
            listPhoto = photoService.findAll(selectedAlbum);
            for(Photo p : listPhoto){
                try {
                    photoService.delete(p);
                } catch (SempicModelException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
                    partiallyFailed=true; 
                }
            }
            if (partiallyFailed) {
             return "failure";
            }
            else {
                try {
                    service.delete(selectedAlbum);
                } catch (SempicModelException ex) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
                        partiallyFailed=true; 
                }
            }
        } catch (SempicModelException ex) {
           FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
           partiallyFailed=true; 
        }
        if (partiallyFailed) {
             return "failure";
        }
        else {
            init();
            return "suppAlbumSuccess";
        }
    }
}
