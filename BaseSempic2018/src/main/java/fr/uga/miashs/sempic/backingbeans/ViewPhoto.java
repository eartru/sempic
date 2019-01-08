/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.entities.Person;
import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.qualifiers.SelectedPhoto;
import fr.uga.miashs.sempic.rdf.RDFStore;
import fr.uga.miashs.sempic.services.PhotoFacade;
import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
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
    
    @Inject
    @SelectedPhoto
    private Photo current;
    
    @Inject
    private PhotoFacade service;
    
    private Person perso;
    
    private List<Person> persons;
    
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
        RDFStore s = new RDFStore();
        
        List<Resource> list = s.getPersons(query);

        list.forEach(c -> {    
            String[] labelSplit = c.getProperty(RDFS.label).getObject().toString().split("\\s+");
            perso = new Person(c.getURI()
                    , labelSplit[0]
                    , labelSplit[1]);
            
            personList.add(perso);
        });        
        return personList;
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
     
     public List<Person> getPersons() {
        return persons;
    }
 
    public void setPersons(List<Person> p) {
        this.persons = p;
    }

    
}
