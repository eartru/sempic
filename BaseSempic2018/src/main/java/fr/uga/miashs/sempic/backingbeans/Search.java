/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.rdf.RDFStore;
import fr.uga.miashs.sempic.services.PhotoFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * 
 */
@Named
@ViewScoped
public class Search implements Serializable{

    @EJB
    private RDFStore rDFStore;
    
    @Inject
    @SelectedUser
    private SempicUser current;
    
    @Inject
    private PhotoFacade service;
    
    private List<String> photos;
    
    private String requete;
    
    public String searchS() {
        boolean partiallyFailed = false;
        List<Resource> list = new ArrayList<>();
        String self = current.getFirstname()+current.getLastname();
        //String self = "JeffDupond";
        photos = new ArrayList<>();
        try {
            if (requete.equals("1"))
                list = rDFStore.getFamilyPhotos(self);
            if (requete.equals("2"))
                list = rDFStore.getFriendPhotos(self);
            if (requete.equals("3"))
                list = rDFStore.getPhotosPeople(self);
            if (requete.equals("4"))
                list = rDFStore.getPhotosNoPeople(self);
            if (requete.equals("5"))
                list = rDFStore.getSelfies(self);
            
            list.forEach(p -> {
                photos.add(p.getProperty(SempicOnto.path).getObject().toString());
            });
        } catch (Exception e) {
            partiallyFailed = true;
        }
        if (partiallyFailed) {
             return "failure";
        }
        else {
            return "success";
        }
    }
    
    public String searchA() {
        boolean partiallyFailed = false;

        try {
//            Model m = ModelFactory.createDefaultModel();
//
//            Resource someone = m.createResource(SempicOnto.Person);
//            someone.addLiteral(RDFS.label, "Georges");
//            m.add(pRes, SempicOnto.depicts, someone);

            //m.write(System.out, "turtle");

//            rDFStore.saveModel(m);
        } catch (Exception e) {
            partiallyFailed = true;
        }
        if (partiallyFailed) {
             return "failure";
        }
        else {
            return "success";
        }
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
    
    public SempicUser getCurrent() {
        return current;
    }

    public void setCurrent(SempicUser current) {
        this.current = current;
    }

    public String getRequete() {
        return requete;
    }

    public void setRequete(String requete) {
        this.requete = requete;
    }
    
    
}
