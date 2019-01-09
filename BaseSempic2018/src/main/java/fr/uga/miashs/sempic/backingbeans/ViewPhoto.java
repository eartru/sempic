/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.entities.Person;
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
    private Person perso; 
    private GenericSemantic country;
    private GenericSemantic region;
    private GenericSemantic department;
    private GenericSemantic city;
    private GenericSemantic object;
    private GenericSemantic event;
    private Date date;
    
    public String annotate() {
        RDFStore s = new RDFStore();
        boolean partiallyFailed = false;

        try {
            Resource pRes = s.createPhoto(current.getId(), current.getAlbum().getId(), current.getAlbum().getOwner().getId());

            Model m = ModelFactory.createDefaultModel();
            
            //String personURI = "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Person/"; 
          
//            Resource someone = m.createResource(personURI);
//            someone.addLiteral(RDFS.label, "Jeff Dupond");
//            someone.addProperty(RDF.type, SempicOnto.Person);
//            m.add(pRes, SempicOnto.depicts, someone);

            m.write(System.out, "turtle");

            s.saveModel(m);

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
    
    public List<Person> completePerson(String query) {
        List<Person> personList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getPersons(query);

        list.forEach(c -> {    
            String[] labelSplit = c.getProperty(RDFS.label).getObject().toString().split("\\s+");
            perso = new Person(c.getURI()
                    , labelSplit[0]
                    , labelSplit[1]);
            
            personList.add(perso);
        });        
        return personList;
    }
    
    public List<GenericSemantic> completeCountry(String query) {
        List<GenericSemantic> countryList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getCountry(query);

        list.forEach(c -> {    
            country = new GenericSemantic(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            countryList.add(country);
        });        
        return countryList;
    }
    
    public List<GenericSemantic> completeRegion(String query) {
        List<GenericSemantic> regionList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getRegion(query, country.getUri());

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
    
    public List<GenericSemantic> completeObject(String query) {
        List<GenericSemantic> objectList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getObject(query);

        list.forEach(c -> {    
            object = new GenericSemantic(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            objectList.add(object);
        });        
        return objectList;
    }
    
    public List<GenericSemantic> completeEvent(String query) {
        List<GenericSemantic> eventList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getEvent(query);

        list.forEach(c -> {    
            event = new GenericSemantic(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            eventList.add(event);
        });        
        return eventList;
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
    
    public Person getPerso() {
        return perso;
    }

    public void setPerso(Person perso) {
        this.perso = perso;
    }
     
    public GenericSemantic getCountry() {
        return country;
    }

    public void setCountry(GenericSemantic country) {
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

    public GenericSemantic getObject() {
        return object;
    }

    public void setObject(GenericSemantic object) {
        this.object = object;
    }

    public GenericSemantic getEvent() {
        return event;
    }

    public void setEvent(GenericSemantic event) {
        this.event = event;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    
       
}
