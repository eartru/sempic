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
import java.util.Date;
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
    
    private String title;
    private String person;
    private List<String> persons;
    private String country;
    private List<String> objects;
    private String object;
    private String event;
    private Date date;
    
    public String searchS() {
        boolean partiallyFailed = false;
        List<Resource> list = new ArrayList<>();
        String self = current.getFirstname()+current.getLastname();
        //String self = "JeffDupond";
        photos = new ArrayList<>();
        try {
            if (requete.equals("1"))
                list = rDFStore.getFamilyPhotos(current.getId(), self);
            if (requete.equals("2"))
                list = rDFStore.getFriendPhotos(current.getId(), self);
            if (requete.equals("3"))
                list = rDFStore.getPhotosPeople(current.getId());
            if (requete.equals("4"))
                list = rDFStore.getPhotosNoPeople(current.getId());
            if (requete.equals("5"))
                list = rDFStore.getSelfies(current.getId(), self);
            
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
        List<Resource> list = new ArrayList<>();
        //String self = current.getFirstname()+current.getLastname();
        String self = "JeffDupond";
        photos = new ArrayList<>();
        try {
            //list = rDFStore.getAdvancedSearchPhotos(current.getId(), self, title, persons, objects, country, event);
            list = rDFStore.getAdvancedSearchPhotos(51, self, title, persons, objects, country, event);
            
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
    
    public List<Resource> completePerson(String query) {     
        List<Resource> list = rDFStore.getPersons(query);
    
        return list;
    }
    
    public List<Resource> completeCountry(String query) { 
        List<Resource> list = rDFStore.getCountry(query);
   
        return list;
    }
    
    public List<Resource> completeObject(String query) {
        List<Resource> list = rDFStore.getObject(query);
        
        return list;
    }
    
    public List<Resource> completeEvent(String query) {
        List<Resource> list = rDFStore.getEvent(query);
     
        return list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public List<String> getPersons() {
        return persons;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getObjects() {
        return objects;
    }

    public void setObjects(List<String> objects) {
        this.objects = objects;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    
    
}
