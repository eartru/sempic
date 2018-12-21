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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@Stateful
public class PhotoFacade extends AbstractJpaFacade<Long,Photo> {
    
    @Inject
    private PhotoStorage storage;

    public PhotoFacade() {
        super(Photo.class);        
    }
    

    @Override
    public void create(Photo p) throws SempicModelException {
        throw new UnsupportedOperationException("use create(Photo p, InputStream data) instead of create(Photo p)");
    }
    
    public void create(Photo p, InputStream data) throws SempicModelException {
        super.create(p);
        try {
            storage.savePicture(Paths.get(String.valueOf(p.getAlbum().getId()), String.valueOf(p.getId())),data);
        } catch (SempicException ex) {
            throw new SempicModelException("The picture file could not be saved",ex);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public List<Photo> findAll(Album a) throws SempicModelException {
        Query q = getEntityManager().createNamedQuery("findPhotoAlbum");
        q.setParameter("album", a);
        return q.getResultList();
    }

    @Override
    public void delete(Photo p) throws SempicModelException {
            super.delete(p);
        try {
            storage.deletePicture(Paths.get(String.valueOf(p.getAlbum().getId()), String.valueOf(p.getId())));
        } catch (SempicException ex) {
            Logger.getLogger(PhotoFacade.class.getName()).log(Level.INFO, null, ex);
        }

    }
}
