/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class ExampleRDFStore {
    public static void main(String[] args) {
        RDFStore s = new RDFStore();
        
//        List<Resource> list = s.getCountry("fra");
//        
//        list.forEach(listItem -> {
//            System.out.println(listItem.getProperty(RDFS.label).getObject().toString());
//        });
             
//        Resource pRes = s.createPhoto(2, 1, 1);
//
        Model m = ModelFactory.createDefaultModel();

        String personURI = "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Person/JeffDupond"; 
        String pFriend = "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Person/PierreCarré";
        String pFamily = "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Person/CaroleDupond";
        String photo = "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Photo/151";
          
        Resource someone = m.getResource(personURI);
        Resource friend = m.getResource(pFriend);
        Resource family = m.getResource(pFamily);
        Resource p = m.getResource(photo);
        someone.addProperty(SempicOnto.isFriendOf, friend);
        someone.addProperty(SempicOnto.hasSister, family);
        p.addLiteral(SempicOnto.path, "1/151");
 
        //m.add(pRes, SempicOnto.depicts, someone);
        
        m.write(System.out, "turtle");
            
        s.saveModel(m);

//        Resource pRes = s.createPhoto(1, 1, 1);
//
//        Model m = ModelFactory.createDefaultModel();
//        
//        Resource meme = m.createResource(SempicOnto.Person);
//        meme.addLiteral(RDFS.label, "Mémé");
//        m.add(pRes, SempicOnto.depicts, meme);
//        
//        Resource newDog = m.createResource(SempicOnto.Dog);
//        newDog.addLiteral(RDFS.label, "Medor");
//        m.add(pRes, SempicOnto.depicts, newDog);
//        m.add(pRes, SempicOnto.owns, newDog);
//        
//        Resource newCat = m.createResource(SempicOnto.Cat);
//        newCat.addLiteral(RDFS.label, "Félix");
//        m.add(pRes, SempicOnto.depicts, newCat);
//        m.add(pRes, SempicOnto.owns, newCat);
//        
//        Resource newTV = m.createResource(SempicOnto.TV);
//        newTV.addLiteral(RDFS.label, "Samsung");
//        m.add(pRes, SempicOnto.depicts, newTV);
//        m.add(pRes, SempicOnto.owns, newTV);
//        
//        m.write(System.out, "turtle");
//
//        s.saveModel(m);
        
        //s.deleteModel(m);
        //s.cnx.load(m);
        /*List<Resource> classes = s.listSubClassesOf(SempicOnto.Depiction);
        classes.forEach(c -> {System.out.println(c);});

        List<Resource> instances = s.createAnonInstances(classes);
        instances.forEach(i -> {
            System.out.println(i.getProperty(RDFS.label));
        });*/

        //s.deleteModel(m);
        //s.readPhoto(1).getModel().write(System.out,"turtle");
        // print the graph on the standard outputs
        //pRes.getModel().write(System.out);
    }
}
