/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.SempicModelException;
import fr.uga.miashs.sempic.entities.Album;
import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.services.AlbumFacade;
import fr.uga.miashs.sempic.services.PhotoFacade;
import java.io.Serializable;
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
public class DeletePhoto implements Serializable {
    
    @Inject
    private PhotoFacade service;
    
    public DeletePhoto() {
    
    }
    
     @PostConstruct
    public void init() {
    }

    public String delete(Photo selectedPhoto) {
        if (selectedPhoto==null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("parameter photo must be given"));
            return "failure";
        }
        boolean partiallyFailed=false;
        try {
            service.delete(selectedPhoto);
        } catch (SempicModelException ex) {
           FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
           partiallyFailed=true; 
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
