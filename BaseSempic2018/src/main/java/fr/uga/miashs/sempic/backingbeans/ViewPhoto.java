/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.entities.Person;
import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.entities.Place;
import fr.uga.miashs.sempic.qualifiers.SelectedPhoto;
import fr.uga.miashs.sempic.rdf.RDFStore;
import fr.uga.miashs.sempic.services.PhotoFacade;
import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
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
    private Place country;
    private Place region;
    private Place department;
    private Place city;
    
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
    
    public List<Place> completeCountry(String query) {
        List<Place> countryList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getCountry(query);

        list.forEach(c -> {    
            country = new Place(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            countryList.add(country);
        });        
        return countryList;
    }
    
    public List<Place> completeRegion(String query) {
        List<Place> regionList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getRegion(query, country.getUri());

        list.forEach(c -> {    
            region = new Place(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            regionList.add(region);
        });        
        return regionList;
    }
    
    public List<Place> completeDepartment(String query) {
        List<Place> departmentList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getDepartment(query, region.getUri());

        list.forEach(c -> {    
            department = new Place(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            departmentList.add(department);
        });        
        return departmentList;
    }
    
    public List<Place> completeCity(String query) {
        List<Place> cityList = new ArrayList<>();
        
        List<Resource> list = rDFStore.getCity(query, department.getUri());

        list.forEach(c -> {    
            city = new Place(c.getURI(), c.getProperty(RDFS.label).getObject().toString());
            
            cityList.add(city);
        });        
        return cityList;
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
     
    public Place getCountry() {
        return country;
    }

    public void setCountry(Place country) {
        this.country = country;
    }

    public Place getRegion() {
        return region;
    }

    public void setRegion(Place region) {
        this.region = region;
    }

    public Place getDepartment() {
        return department;
    }

    public void setDepartment(Place department) {
        this.department = department;
    }

    public Place getCity() {
        return city;
    }

    public void setCity(Place city) {
        this.city = city;
    }
    
}
