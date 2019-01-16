/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.services.SempicUserFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.jena.rdf.model.Resource;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.rdf.RDFStore;
import javax.ejb.EJB;

/**
 *
 *
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
    private String spouse;
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

    public String getSpouse() {
        return spouse;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }
    

    
    
    
    public String edit() {       
        if (spouse != null){
            RDFStore rdfStore = new RDFStore();

            try {
                rDFStore.addSpouse(current.getFirstname(), current.getLastname(), spouse);
            } catch (Exception e) {
                System.out.println(e);
            }    
        }
        
        if (parent1 != null){
            RDFStore rdfStore = new RDFStore();

            try {
                rDFStore.addParent(current.getFirstname(), current.getLastname(), parent1);
            } catch (Exception e) {
                System.out.println(e);
            }    
        }
        
        if (parent2 != null){
            RDFStore rdfStore = new RDFStore();
 
            try {
                rDFStore.addParent(current.getFirstname(), current.getLastname(), parent2);
            } catch (Exception e) {
                System.out.println(e);
            }     
        }
        
        if (listChild != null){
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
            RDFStore rdfStore = new RDFStore();

            try {
                listFriend.forEach(friend ->{
                    System.out.println(friend);
                    rDFStore.addFriend(current.getFirstname(), current.getLastname(), friend);
                });
            } catch (Exception e) {
                System.out.println(e);
            }     
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
