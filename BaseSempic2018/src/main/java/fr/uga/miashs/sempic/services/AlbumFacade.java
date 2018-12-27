/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.services;

import fr.uga.miashs.sempic.SempicException;
import fr.uga.miashs.sempic.SempicModelException;
import fr.uga.miashs.sempic.entities.Album;
import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.entities.SempicUser;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@Stateful
public class AlbumFacade extends AbstractJpaFacade<Long,Album> {


    public AlbumFacade() {
        super(Album.class);
    }
    
    public List<Album> findAlbumsOf(SempicUser owner) {
        Query q = getEntityManager().createNamedQuery("findUserAlbums");
        q.setParameter("owner", owner);
        return q.getResultList();
    }    
    
    @Override
    public void delete(Album a) throws SempicModelException {
        super.delete(a);
    }
}
