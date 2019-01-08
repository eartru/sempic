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
    public final static String friendNS =  "http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#isFriendOf";
    //public final static String photoNS = "http://http://miashs.univ-grenoble-alpes.fr/photo";

    
    public static String getPhotoUri(long photoId) {
        return photoNS+photoId;
    }
    
    public static String getFriendUri(long personId) {
        return photoNS+personId;
    }
    
    public static String getPersonUri(String firstname, String lastname) {
        return personNS+firstname+lastname;
    }
}
