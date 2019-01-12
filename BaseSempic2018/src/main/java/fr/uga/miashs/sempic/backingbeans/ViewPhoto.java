/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.entities.GenericSemantic;
import fr.uga.miashs.sempic.qualifiers.SelectedPhoto;
import fr.uga.miashs.sempic.rdf.RDFStore;
import fr.uga.miashs.sempic.services.PhotoFacade;
import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.ejb.EJB;
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

    @EJB
    private RDFStore rDFStore;
    
    @Inject
    @SelectedPhoto
    private Photo current;
    
    @Inject
    private PhotoFacade service;
    
    private String title;
    private String person;
    private List<String> persons;
    private String country;
    private GenericSemantic region;
    private GenericSemantic department;
    private GenericSemantic city;
    private List<String> objects;
    private String object;
    private String event;
    private Date date;
    
    public String annotate() {
        boolean partiallyFailed = false;
            
        try {
                        System.out.println("perso");
            System.out.println(persons);
            // (current.getId(), current.getAlbum().getId(), current.getAlbum().getOwner().getId());
            rDFStore.annotatePhoto(current.getId(), title, persons, objects, country, event, date);

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
    
    public List<Resource> completePerson(String query) {     
        List<Resource> list = rDFStore.getPersons(query);
    
        return list;
    }
    
    public List<Resource> completeCountry(String query) { 
        List<Resource> list = rDFStore.getCountry(query);
   
        return list;
    }
    
    // La recherche de region, departement et ville fonctionne avec les objets générique
    // Mais une erreur survient lorsque l'on essaie de récupérer le contenu des Input ...
    // A l'inverse on peut récupérer ce qui vient des Input avec String mais la recherche ne marche plus
    public List<GenericSemantic> completeRegion(String query) {
        List<GenericSemantic> regionList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getRegion(query, country);

        list.forEach(c -> {    
            region = new GenericSemantic(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            regionList.add(region);
        });        
        return regionList;
    }
    
    public List<GenericSemantic> completeDepartment(String query) {
        List<GenericSemantic> departmentList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getDepartment(query, region.getUri());

        list.forEach(c -> {    
            department = new GenericSemantic(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            departmentList.add(department);
        });        
        return departmentList;
    }
    
    public List<GenericSemantic> completeCity(String query) {
        List<GenericSemantic> cityList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getCity(query, department.getUri());

        list.forEach(c -> {    
            city = new GenericSemantic(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            cityList.add(city);
        });        
        return cityList;
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
    
    public Photo getCurrent() {
        return current;
    }

    public void setCurrent(Photo current) {
        this.current = current;
    }
     
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public GenericSemantic getRegion() {
        return region;
    }

    public void setRegion(GenericSemantic region) {
        this.region = region;
    }

    public GenericSemantic getDepartment() {
        return department;
    }

    public void setDepartment(GenericSemantic department) {
        this.department = department;
    }

    public GenericSemantic getCity() {
        return city;
    }

    public void setCity(GenericSemantic city) {
        this.city = city;
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

    public List<String> getObjects() {
        return objects;
    }

    public void setObjects(List<String> objects) {
        this.objects = objects;
    }
  
    
}
