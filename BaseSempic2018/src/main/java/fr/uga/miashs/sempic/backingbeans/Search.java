/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.backingbeans;

import fr.uga.miashs.sempic.entities.Album;
import fr.uga.miashs.sempic.entities.SempicGroup;
import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import fr.uga.miashs.sempic.qualifiers.SelectedUser;
import fr.uga.miashs.sempic.rdf.RDFStore;
import fr.uga.miashs.sempic.services.AlbumFacade;
import fr.uga.miashs.sempic.services.GroupFacade;
import fr.uga.miashs.sempic.services.SempicUserFacade;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author alice
 */
@Named
@ViewScoped
public class Search implements Serializable{
    @Inject
    @SelectedUser
    private SempicUser current;
    
    @PostConstruct
    public void init() {
        
    }
    
    public Search() {
        
    }
    
    public String search() {
        RDFStore s = new RDFStore();
        boolean partiallyFailed = false;

        try {
            Resource pRes = s.createPhoto(1, 1, 1);

            Model m = ModelFactory.createDefaultModel();

            Resource someone = m.createResource(SempicOnto.Person);
            someone.addLiteral(RDFS.label, "Georges");
            m.add(pRes, SempicOnto.depicts, someone);

            //m.write(System.out, "turtle");

            s.saveModel(m);
        } catch (Exception e) {
            partiallyFailed = true;
        }
        if (partiallyFailed) {
             return "failure";
        }
        else {
            init();
            return "success";
        }
    }
}
