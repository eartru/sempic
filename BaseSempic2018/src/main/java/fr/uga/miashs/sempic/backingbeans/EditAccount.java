/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.SempicModelException;
import fr.uga.miashs.sempic.entities.Album;
import fr.uga.miashs.sempic.entities.GenericSemantic;
import fr.uga.miashs.sempic.entities.Person;
import fr.uga.miashs.sempic.entities.Photo;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.qualifiers.SelectedAlbum;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.services.PhotoFacade;
import fr.uga.miashs.sempic.services.SempicUserFacade;
import fr.uga.miashs.sempic.services.SempicUserService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.rdf.RDFStore;
import javax.ejb.EJB;
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@Named
@ViewScoped
public class EditAccount implements Serializable {
    @EJB
    private RDFStore rDFStore;
    
    @Inject
    @SelectedUser
    private SempicUser current;
    
    @Inject
    private SempicUserFacade userService;
    
    private String parent1;
    private String parent2;
    private ArrayList<String> listChild;
    private ArrayList<String> listFriend;
    
    public SempicUser getUser() {
        return current;
    }

    public SempicUser getCurrent() {
        return current;
    }

    public void setCurrent(SempicUser current) {
        this.current = current;
    }

    public String getParent1() {
        return parent1;
    }

    public void setParent1(String parent1) {
        this.parent1 = parent1;
    }
    
    public String getParent2() {
        return parent2;
    }

    public void setParent2(String parent2) {
        this.parent2 = parent2;
    }

    public ArrayList<String> getListChild() {
        return listChild;
    }

    public void setListChild(ArrayList<String> listChild) {
        this.listChild = listChild;
    }

    public ArrayList<String> getListFriend() {
        return listFriend;
    }

    public void setListFriend(ArrayList<String> listFriend) {
        this.listFriend = listFriend;
    }
    
    

    
    
    
    public String edit() {
        System.out.println("Edit methode");
        //System.out.println(current);
        //System.out.println(parent1);
        //System.out.println(parent2);
        //System.out.println(listChild);
        //System.out.println(listFriend);
        
        
        
        if (parent1 != null){
            System.out.println(parent1);
            RDFStore rdfStore = new RDFStore();

            try {
                rDFStore.addParent(current.getFirstname(), current.getLastname(), parent1);
            } catch (Exception e) {
                System.out.println(e);
            }    
        }
        
        if (parent2 != null){
            System.out.println(parent2);
            RDFStore rdfStore = new RDFStore();
 
            try {
                rDFStore.addParent(current.getFirstname(), current.getLastname(), parent2);
            } catch (Exception e) {
                System.out.println(e);
            }     
        }
        
        if (listChild != null){
            System.out.println(listChild);
            RDFStore rdfStore = new RDFStore();

            try {
                listChild.forEach(child ->{
                    System.out.println(child);
                    rDFStore.addChild(current.getFirstname(), current.getLastname(), child);
                });
            } catch (Exception e) {
                System.out.println(e);
            }     
        }
        
        if (listFriend != null){
            System.out.println(listFriend);
            RDFStore rdfStore = new RDFStore();

            try {
                listFriend.forEach(friend ->{
                    System.out.println(friend);
                    rDFStore.addFriend(current.getFirstname(), current.getLastname(), friend);
                });
            } catch (Exception e) {
                System.out.println(e);
            }     
        }else{
            System.out.println("nullllllllll !!");
        }
        
        return "success";
    }
    
    public List<Resource> completePerson(String query) {       
        List<Resource> list = rDFStore.getPersons(query);
        System.out.println("personList:");
        System.out.println(list);
        return list;
    }
}
