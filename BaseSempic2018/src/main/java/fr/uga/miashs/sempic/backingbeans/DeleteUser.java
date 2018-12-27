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
import fr.uga.miashs.sempic.services.SempicUserFacade;
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
public class DeleteUser implements Serializable {
      
    @Inject
    private SempicUserFacade service;
    
    @Inject
    private AlbumFacade albumService;
    
    @Inject
    private PhotoFacade photoService;
    
    private List<Photo> listPhoto;
    
    private List<Album> listAlbum;
    
    public DeleteUser() {
    
    }
    
    @PostConstruct
    public void init() {
    }

    public String delete(SempicUser selectedUser) {
        if (selectedUser==null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("parameter user must be given"));
            return "failure";
        }
        boolean partiallyFailed=false;
        try {
            listAlbum = albumService.findAlbumsOf(selectedUser);
            for(Album a: listAlbum){
                listPhoto = photoService.findAll(a);
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
                        albumService.delete(a);
                    } catch (SempicModelException ex) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
                            partiallyFailed=true; 
                    }
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
            try {
                service.delete(selectedUser);
            } catch (SempicModelException ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
                    partiallyFailed=true; 
            }
            if (partiallyFailed) {
             return "failure";
            } else {
                init();
                return "supprUserSuccess";
            }
        }
    }
}
