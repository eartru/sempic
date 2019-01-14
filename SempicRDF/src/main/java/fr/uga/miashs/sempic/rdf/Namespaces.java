/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class Namespaces {
    
    public final static String photoNS =  "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Photo/";
    public final static String personNS =  "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Person/";
    public final static String objectNS =  "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Object/";
    public final static String eventNS =  "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Object/";
    //public final static String photoNS = "http://http://miashs.univ-grenoble-alpes.fr/photo";

    
    public static String getPhotoUri(long photoId) {
        return photoNS+photoId;
    }
    
    public static String getPersonUri(String firstname, String lastname) {
        return personNS+firstname+lastname;
    }
    
    public static String getObjectUri(String name) {
        return objectNS+name;
    }
    
    public static String getEventGlobalUri(String name) {
        return eventNS+"GlobalEvent"+name;
    }
    
    public static String getEventPartyUri(String name) {
        return eventNS+"Party"+name;
    }
}
