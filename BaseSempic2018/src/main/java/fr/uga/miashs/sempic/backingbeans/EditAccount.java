/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.SempicModelException;
import fr.uga.miashs.sempic.entities.Album;
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
import org.apache.jena.rdf.model.Property;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@Named
@ViewScoped
public class EditAccount implements Serializable {
    
    @Inject
    @SelectedUser
    private SempicUser current;
    
    @Inject
    private SempicUserFacade userService;
    
    private String parent1;
    private String parent2;
    private ArrayList<SempicUser> listChild;
    private ArrayList<SempicUser> listFriend;
    
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

    public ArrayList<SempicUser> getListChild() {
        return listChild;
    }

    public void setListChild(ArrayList<SempicUser> listChild) {
        this.listChild = listChild;
    }

    public ArrayList<SempicUser> getListFriend() {
        return listFriend;
    }

    public void setListFriend(ArrayList<SempicUser> listFriend) {
        this.listFriend = listFriend;
    }

    
    
    
    public String edit() {
        
        //System.out.println(current);
        //System.out.println(parent1);
        //System.out.println(parent2);
        //System.out.println(listChild);
        //System.out.println(listFriend);
        
        if (listFriend != null){
            System.out.println(listFriend);
            RDFStore rdfStore = new RDFStore();
            boolean partiallyFailed = false;
            
            for (int i = 0; i < listFriend.size(); i++) {
                try {
                System.out.println("i = " +listFriend.get(i));
                    
                    //Resource pRes = rdfStore.createPhoto(1, 1, 1);

                    Model m = ModelFactory.createDefaultModel();

                    /*Resource friendRes = m.createResource(SempicOnto.Person);
                    friendRes.addLiteral(RDFS.label, listFriend.get(i));
                    Resource someoneRes = m.createResource(SempicOnto.Person);
                    someoneRes.addLiteral(RDFS.label, current.getId());*/
//                    long number = listFriend.get(i).getId();
//                    Resource friendRes = rdfStore.createPerson(number);
//                    System.out.println("friendRes:");
//                    System.out.println(friendRes);
//                    Resource someoneRes = rdfStore.createPerson(current.getId());
//                    System.out.println(someoneRes);
//                    
//                    m.add(someoneRes, SempicOnto.isFriendOf, friendRes);

                     //m.write(System.out, "turtle");

                     rdfStore.saveModel(m);
                } catch (Exception e) {
                    System.out.println(e);
                }
                
                
                System.out.println(listFriend); 
            }
        }else{
            System.out.println("nullllllllll !!");
        }
        
        return "success";
    }
    
    public List<SempicUser> completeText(String query) {
        /*List<SempicUser> users = userService.findAll();
        List<String> results = new ArrayList<String>();
        for(int i = 0; i < users.size(); i++) {
            results.add(users.get(i).getEmail());
        }
         
        return results;*/
        return userService.findAll();
    }
}
