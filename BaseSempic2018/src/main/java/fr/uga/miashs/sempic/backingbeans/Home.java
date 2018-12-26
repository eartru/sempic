/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.entities.Album;
import fr.uga.miashs.sempic.entities.SempicGroup;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.services.AlbumFacade;
import fr.uga.miashs.sempic.services.GroupFacade;
import fr.uga.miashs.sempic.services.SempicUserFacade;
import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author alice
 */
@Named
@ViewScoped
public class Home implements Serializable{
    @Inject
    @SelectedUser
    private SempicUser current;
    
    @Inject
    private AlbumFacade albumService;
    
    public Home() {
        
    }
    
    public List<Album> getAlbums() {
        return albumService.findAlbumsOf(current);
    }

    public SempicUser getCurrent() {
        return current;
    }

    public void setCurrent(SempicUser current) {
        this.current = current;
    }
}
