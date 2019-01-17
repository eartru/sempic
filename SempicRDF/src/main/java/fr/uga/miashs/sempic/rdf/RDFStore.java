/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.Dbpedia;
import fr.uga.miashs.sempic.model.rdf.GeoNames;
import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.update.Update;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

/**
 *
 *
 */
@Stateless
public class RDFStore {

    public final static String ENDPOINT_QUERY = "http://localhost:3030/sempic/sparql"; // SPARQL endpoint
    public final static String ENDPOINT_UPDATE = "http://localhost:3030/sempic/update"; // SPARQL UPDATE endpoint
    public final static String ENDPOINT_GSP = "http://localhost:3030/sempic/data"; // Graph Store Protocol

    protected final RDFConnection cnx;

    public RDFStore() {
        cnx = RDFConnectionFactory.connect(ENDPOINT_QUERY, ENDPOINT_UPDATE, ENDPOINT_GSP);
    }

    /**
     * Save the given model into the triple store.
     * @param m THe Jena model to be persisted
     */
    public void saveModel(Model m) {
        cnx.begin(ReadWrite.WRITE);
        cnx.load(m);
        cnx.commit();
    }

    /**
     * Delete the given model from the triple store. Be carreful: Blank nodes
     * are replaced by variables
     * @param m the model to be deleted
     */
    public void deleteModel(Model m) {
        HashMap<Resource, Var> map = new HashMap<>();
        QuadAcc acc = new QuadAcc();
        m.listStatements().forEachRemaining(st -> {
            if (st.getSubject().isAnon() || st.getObject().isAnon()) {
                Node s = blankNodeAsVariable(st.getSubject(), map);
                Node p = st.getPredicate().asNode();
                Node o = blankNodeAsVariable(st.getObject(), map);

                acc.addTriple(new Triple(s, p, o));
            } else {

                acc.addTriple(st.asTriple());
            }
        });

        Update u = new UpdateDeleteWhere(acc);
        //System.out.println(u);
        cnx.begin(ReadWrite.WRITE);
        cnx.update(u);
        cnx.commit();
    }

    private Node blankNodeAsVariable(Resource r, Map<Resource, Var> bnMap) {
        if (r.isAnon()) {
            Var v = bnMap.get(r);
            if (v == null) {
                bnMap.put(r, v = Var.alloc("A" + bnMap.size()));
            }
            return v;
        }
        return r.asNode();
    }

    private Node blankNodeAsVariable(RDFNode r, Map<Resource, Var> bnMap) {
        if (r.isAnon()) {
            return blankNodeAsVariable(r.asResource(), bnMap);
        }
        return r.asNode();
    }

    /**
     * Delete all the statements where the resource appears as subject or object
     * @param r The named resource to be deleted (the resource cannot be annonymous)
     */
    public void deleteResource(Resource r) {
        if (r.isURIResource()) {
            cnx.begin(ReadWrite.WRITE);
            cnx.update("DELETE WHERE { <" + r.getURI() + "> ?p ?o }");
            cnx.update("DELETE WHERE { ?s ?p <" + r.getURI() + "> }");
            cnx.commit();
        }
    }
    
    public void askDelete(String s, char b) {
        Model m = ModelFactory.createDefaultModel();
        Resource r = null;
        if (b == 'p') {
             r = m.getResource(Namespaces.personNS+s);
             deleteResource(r);
        }
        if (b == 'h') {
             r = m.getResource(Namespaces.photoNS+s);
            deleteResource(r);
        }
        if (b == 'a') {
            Model mo = cnx.queryConstruct("CONSTRUCT { ?photo <"+SempicOnto.albumId+"> ?albumId . }\n" +
                " WHERE   { ?photo <"+SempicOnto.albumId+"> ?albumId . \n" +
                "FILTER( ?albumId = \""+s+"\"^^<"+XSD.xlong+"> ) }");
        
            List<Resource> list = mo.listSubjects().toList();
            for(Resource item: list)
                deleteResource(item);
        }
    }

    /**
     * Retieves all the resources that are subclasses of resource c. To be
     * selected classes must have the property rdfs:label instanciated
     *
     * @param c A named class (the resource cannot be annonymous)
     * @return
     */
    public List<Resource> listSubClassesOf(Resource c) {
        Model m = cnx.queryConstruct("CONSTRUCT { "
                + "?s <" + RDFS.label + "> ?o "
                + "} WHERE {"
                + "?s <" + RDFS.subClassOf + "> <" + c.getURI() + "> ."
                + "?s <" + RDFS.label + "> ?o ."
                + "}");
        return m.listSubjects().toList();
    }
    
    /**
     * Create a list of anonymous instances for each of the classes
     * given as parameter. The created instances have a label "a "+ label of the class.
     * @param classes
     * @return 
     */
    public List<Resource> createAnonInstances(List<Resource> classes) {
        Model m = ModelFactory.createDefaultModel();
        List<Resource> res = new ArrayList<>();
        for (Resource c : classes) {
            Resource instance = m.createResource(c);
            instance.addLiteral(RDFS.label, "a " + c.getProperty(RDFS.label).getLiteral());
            res.add(instance);
        }
        return res;
    }

    /**
     * Create a resource photo with properties albumId, ownerId and path.
     * @param photoId
     * @param albumId
     * @param ownerId
     * @return
     */
    public Resource createPhoto(long photoId, long albumId, long ownerId) {
        // create an empty RDF graph
        Model m = ModelFactory.createDefaultModel();
        // create an instance of Photo in Model m
        Resource pRes = m.createResource(Namespaces.getPhotoUri(photoId));
        pRes.addProperty(RDF.type, FOAF.Image);

        pRes.addLiteral(SempicOnto.albumId, albumId);
        pRes.addLiteral(SempicOnto.ownerId, ownerId);
        pRes.addLiteral(SempicOnto.path, albumId+"/"+photoId);

        saveModel(m);

        return pRes;
    }

    /**
     * Query a Photo and retrieve all the direct properties of the photo and if
     * the property are depic, takenIn or takenBy, it also retrieve the labels
     * of the object of these properties
     *
     * @param id
     * @return
     */
    public Resource readPhoto(long id) {
        String pUri = Namespaces.getPhotoUri(id);

        String s = "CONSTRUCT {"
                + "<" + pUri + "> ?p ?o . "
                + "<" + pUri + "> ?p1 ?o1 . "
                + "?o1 <" + RDFS.label + "> ?o2 . "
                + "} "
                + "WHERE { "
                + "<" + pUri + "> ?p ?o . "
                + "OPTIONAL {"
                + "<" + pUri + "> ?p1 ?o1 ."
                + "?o1 <" + RDFS.label + "> ?o2 ."
                + "FILTER (?p1 IN (<" + FOAF.depicts + ">,<" + SempicOnto.takenIn + ">,<" + SempicOnto.takenBy + ">)) "
                + "}"
                + "}";
        Model m = cnx.queryConstruct(s);
        return m.getResource(pUri);
    }

    /**
     * Get list of persons filtered by their label
     * @param q
     * @return
     */
    public List<Resource> getPersons(String q) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?p <" + RDFS.label + "> ?label "
                + "} WHERE {"
                + "?p a <" + SempicOnto.Person + "> ; <" + RDFS.label +"> ?label. FILTER (regex(?label, \"" + q +"\", \"i\"))}");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Create a resource Person in semantic
     * @param firstname
     * @param lastname
     * @param gender
     * @return
     */
    public Resource createPerson(String firstname, String lastname, String gender) {

        Model m = ModelFactory.createDefaultModel(); 
        String personURI = Namespaces.getPersonUri(firstname, lastname);
        
        Resource someone = m.createResource(personURI);
        someone.addLiteral(RDFS.label, firstname + " " + lastname);
        if (gender.equals("femme")) {
            someone.addProperty(RDF.type, SempicOnto.Female);
        } 
        if (gender.equals("homme")) {
            someone.addProperty(RDF.type, SempicOnto.Male);
        }
        someone.addProperty(RDF.type, SempicOnto.Person);

        m.write(System.out, "turtle");

        saveModel(m);

        return someone;
    }
    
    /**
     * Annotate a photo depending on what the user choose as annotations
     * @param id
     * @param label
     * @param persons
     * @param objects
     * @param country
     * @param event
     * @param date
     */
    public void annotatePhoto(long id, String label, List<String> persons, List<String> objects, String country, String event, Date date) {
        
        Model m = ModelFactory.createDefaultModel();

        Resource photo = m.getResource(Namespaces.getPhotoUri(id));
        
        if (persons != null) {
            List<Resource> perso = new ArrayList();
            persons.forEach(p -> {
                perso.add(m.getResource(p));
            });
            perso.forEach(p -> {
                m.add(photo, FOAF.depicts, p);
            });
        }
        
        if (objects != null) {
            List<Resource> obj = new ArrayList();
            objects.forEach(o -> {
                obj.add(m.getResource(o));
            });
            obj.forEach(o -> {
                m.add(photo, FOAF.depicts, o);
            });
        }

        if (country != null) {
            Resource ctry = m.getResource(country);
            m.add(photo, SempicOnto.takenIn, ctry);
        }
        
        if (event != null) {
            Resource evt = m.getResource(event);
            m.add(photo, SempicOnto.takenOn, evt);
        }
        // Resource rgn = m.getResource(region);
        // Resource dpt = m.getResource(department);
        // Resource cty = m.getResource(city);
        //m.add(photo, SempicOnto.takenIn, rgn);
        //m.add(photo, SempicOnto.takenIn, dpt);
        //m.add(photo, SempicOnto.takenIn, cty);
        
        if (date != null) {
            m.add(photo, SempicOnto.takenOn, date.toString());
        }
        
        if(!label.equals("")) {
            m.add(photo, RDFS.label, label);
        }

        m.write(System.out, "turtle");

        saveModel(m);
    }
    
    /**
     * Get list of country filtered by their label
     * @param q
     * @return
     */
    public List<Resource> getCountry(String q) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?uri <"+ RDFS.label +"> ?name } "
        + " WHERE { SERVICE <http://linkedgeodata.org/sparql> " 
	+ " { SELECT DISTINCT ?uri ?name "
	+ "	WHERE { "
	+ "	?uri <http://www.geonames.org/ontology#featureCode> <"+ GeoNames.A_PCLI +"> ; "
	+ "	<http://www.geonames.org/ontology#name> ?name . "
	+ "	FILTER (regex(?name, \"" + q +"\", \"i\")) } } } ");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get list of region filtered by their label and depending on its country
     * @param q
     * @param country
     * @return
     */
    public List<Resource> getRegion(String q, String country) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?uri <"+ RDFS.label +"> ?name } "
        + " WHERE { SERVICE <http://linkedgeodata.org/sparql> " 
	+ " { SELECT DISTINCT ?uri ?name "
	+ "	WHERE { "
	+ "	?uri <http://www.geonames.org/ontology#featureCode> <"+ GeoNames.A_ADM1 +"> ; "
	+ "	<http://www.geonames.org/ontology#name> ?name ; "
        + "	<http://www.geonames.org/ontology#parentFeature> <"+ country +">  . "
	+ "	FILTER (regex(?name, \"" + q +"\", \"i\")) } } } ");
        
        return m.listSubjects().toList();
    }
    
     /**
     * Get list of department filtered by their label and depending on its region
     * @param q
     * @param region
     * @return
     */
    public List<Resource> getDepartment(String q, String region) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?uri <"+ RDFS.label +"> ?name } "
        + " WHERE { SERVICE <http://linkedgeodata.org/sparql> " 
	+ " { SELECT DISTINCT ?uri ?name "
	+ "	WHERE { "
	+ "	?uri <http://www.geonames.org/ontology#featureCode> <"+ GeoNames.A_ADM2 +"> ; "
	+ "	<http://www.geonames.org/ontology#name> ?name ; "
        + "	<http://www.geonames.org/ontology#parentFeature> <"+ region +">  . "
	+ "	FILTER (regex(?name, \"" + q +"\", \"i\")) } } } ");
        
        return m.listSubjects().toList();
    }
    
     /**
     * Get list of city filtered by their label and depending on its department
     * @param q
     * @param department
     * @return
     */
    public List<Resource> getCity(String q, String department) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?uri <"+ RDFS.label +"> ?name } "
        + " WHERE { SERVICE <http://linkedgeodata.org/sparql> " 
	+ " { SELECT DISTINCT ?uri ?name "
	+ "	WHERE { "
	+ "	?uri <http://www.geonames.org/ontology#featureCode> <"+ GeoNames.P_PPLA2 +"> ; "
	+ "	<http://www.geonames.org/ontology#name> ?name ; "
        + "	<http://www.geonames.org/ontology#parentADM2> <"+ department +">  . "
	+ "	FILTER (regex(?name, \"" + q +"\", \"i\")) } } } ");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get a list of objects filtered by their label
     * @param q
     * @return
     */
    public List<Resource> getObject(String q) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?p <" + RDFS.label + "> ?label "
                + "} WHERE {"
                + "?p a ?type ; "
                + "<" + RDFS.label +"> ?label. "
                + "FILTER ( ?type IN ( <"+SempicOnto.Object+">, <" +SempicOnto.Animal+">) && regex(?label, \"" + q +"\", \"i\"))}");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get a list of event filtered by their label
     * @param q
     * @return
     */
    public List<Resource> getEvent(String q) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?p <" + RDFS.label + "> ?name "
                + "} WHERE {"
                + "?p a <"+SempicOnto.Event +"> ; "
                + "<" + RDFS.label +"> ?label. "
                + "FILTER (regex(?label, \"" + q +"\", \"i\"))"
                        + "BIND (STR(?label)  AS ?name) }");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get a list of resource that represents photos. Photos depicts the family of the connected user
     * Photos are owned by the connected user.
     * @param selfId
     * @param self
     * @return
     */
    public List<Resource> getFamilyPhotos(long selfId, String self){
        Model m = cnx.queryConstruct("CONSTRUCT { ?photo <"+SempicOnto.path+"> ?path . }\n" +
                " WHERE   { ?photo a <"+FOAF.Image +"> ; \n" +
                "<"+SempicOnto.ownerId+"> \""+selfId+"\"^^<"+XSD.xlong+">; \n" +
                "<"+SempicOnto.path+"> ?path ; \n" +
                "<"+FOAF.depicts +"> ?someone . \n" +
                "<"+ Namespaces.personNS + self +"> ?lien ?someone. \n" +
                "  FILTER (?lien IN (<"+Dbpedia.sibling+">, <"+Dbpedia.parent+">, <"+Dbpedia.child+">)) }");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get a list of resource that represents photos. Photos depicts the friends of the connected user.
     * Photos are owned by the connected user.
     * @param selfId
     * @param self
     * @return
     */
    public List<Resource> getFriendPhotos(long selfId, String self){
          Model m = cnx.queryConstruct("CONSTRUCT { ?photo <"+SempicOnto.path+"> ?path . }\n" +
                " WHERE   { ?photo a <"+FOAF.Image +"> ; \n" +
               "<"+SempicOnto.ownerId+"> \""+selfId+"\"^^<"+XSD.xlong+">; \n" +
                "<"+SempicOnto.path+"> ?path ; \n" +
                "<"+FOAF.depicts +"> ?someone . \n" +
                "<"+ Namespaces.personNS + self +"> <"+SempicOnto.isFriendOf+"> ?someone. }");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get a list of resource that represents photos. Photos depicts persons.
     * Photos are owned by the connected user.
     * @param selfId
     * @return
     */
    public List<Resource> getPhotosPeople(long selfId){
         Model m = cnx.queryConstruct("CONSTRUCT { ?photo <"+SempicOnto.path+"> ?path . }\n" +
                " WHERE   { ?photo a <"+FOAF.Image +"> ; \n" +
                "<"+SempicOnto.ownerId+"> \""+selfId+"\"^^<"+XSD.xlong+">; \n" +
                "<"+SempicOnto.path+"> ?path ; \n" +
                "<"+FOAF.depicts +"> ?someone . \n" +
                "?someone a <"+Dbpedia.Person+"> . }");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get a list of resource that represents photos. Photos depicts everything but a person.
     * Photos are owned by the connected user.
     * /!\ Does not work as expected
     * @param selfId
     * @return
     */
    public List<Resource> getPhotosNoPeople(long selfId){
         Model m = cnx.queryConstruct("CONSTRUCT { ?photo <"+SempicOnto.path+"> ?path . }\n" +
                " WHERE   { ?photo a <"+FOAF.Image +"> ; \n" +
                "<"+SempicOnto.ownerId+"> \""+selfId+"\"^^<"+XSD.xlong+">; \n" +
                "<"+SempicOnto.path+"> ?path ; \n" +
                "<"+FOAF.depicts +"> ?someone . \n" +
                "FILTER not exists { ?someone a <"+Dbpedia.Person+"> . } }");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get a list of resource that represents photos. Photos depicts the connected user.
     * Photos are owned by the connected user.
     * @param selfId
     * @param self
     * @return
     */
    public List<Resource> getSelfies(long selfId, String self){
         Model m = cnx.queryConstruct("CONSTRUCT { ?photo <"+SempicOnto.path+"> ?path . }\n" +
                " WHERE   { ?photo a <"+FOAF.Image +"> ; \n" +
                "<"+SempicOnto.ownerId+"> \""+selfId+"\"^^<"+XSD.xlong+">; \n" +
                "<"+SempicOnto.path+"> ?path ; \n" +
                "<"+FOAF.depicts +"> <"+Namespaces.eventNS+self+"> . }");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get a list of resource that represents photos. Photos depicts evenement.
     * Photos are owned by the connected user.
     * @param selfId
     * @return
     */
    public List<Resource> getPhotosEvent(long selfId){
         Model m = cnx.queryConstruct("CONSTRUCT { ?photo <"+SempicOnto.path+"> ?path . }\n" +
                " WHERE   { ?photo a <"+FOAF.Image +"> ; \n" +
                "<"+SempicOnto.ownerId+"> \""+selfId+"\"^^<"+XSD.xlong+">; \n" +
                "<"+SempicOnto.path+"> ?path ; \n" +
                "<"+SempicOnto.takenOn +"> ?evt . \n" +
                "?evt  a ?event. \n" +
                "?event  <"+RDFS.subClassOf+"><"+SempicOnto.Event+"> . }");
        
        return m.listSubjects().toList();
    }
    
    /**
     * Get a list of resource that represents photos. Photos depicts whatever the user choose as filter.
     * Photos are owned by the connected user.
     * @param selfId
     * @param self
     * @param label
     * @param persons
     * @param objects
     * @param country
     * @param event
     * @return
     */
    public List<Resource> getAdvancedSearchPhotos(long selfId, String self, String label, List<String> persons, List<String> objects, 
            String country, String event){
        String query = "CONSTRUCT {  ?photo <"+SempicOnto.path+"> ?path . }\n" +
                "WHERE   { ?photo a <"+FOAF.Image +"> ; \n" +
                "<"+SempicOnto.ownerId+"> \""+selfId+"\"^^<"+XSD.xlong+">; \n" +
                "<"+SempicOnto.path+">  ?path ; \n" ;
        if(!label.equals("")) {
            query += " <"+RDFS.label+"> <"+label+"> ; \n";
        }
        if (persons != null) {
            for(String p: persons) {
                query += "<"+ FOAF.depicts+"> <"+p+"> ; \n" ;
            }
        }
        if (objects != null) {
            for(String o: objects) {
                query += "<"+ FOAF.depicts+"> <"+o+"> ; \n" ;
            }
        }
        if (country != null) {
            query += "<"+ SempicOnto.takenIn+"> <"+country+"> ; \n" ;
        }
        if (event != null) {
            query += "<"+ SempicOnto.takenOn+"> <"+event+"> ; \n" ;
        }

        query = query + " }";
        Model m = cnx.queryConstruct(query);
        
        return m.listSubjects().toList();
    }
  
    /**
     * Add property "friend" between two person
     * @param personFirstName
     * @param personLasteName
     * @param friend
     */
    public void addFriend(String personFirstName, String personLasteName, String friend) {
        
        Model m = ModelFactory.createDefaultModel();
        Resource person = m.getResource(Namespaces.getPersonUri(personFirstName, personLasteName));
        m.add(person, SempicOnto.isFriendOf, m.getResource(friend));
        
        m.write(System.out, "turtle");

        saveModel(m);
    }
    
    /**
     * Add property "spouse" between two person
     * @param personFirstName
     * @param personLasteName
     * @param parent
     */
    public void addSpouse(String personFirstName, String personLasteName, String spouse){
        
        Model m = ModelFactory.createDefaultModel();
        Resource person = m.getResource(Namespaces.getPersonUri(personFirstName, personLasteName));

        m.add(person, Dbpedia.spouse, m.getResource(spouse));
        
        m.write(System.out, "turtle");

        saveModel(m);
    }
    
    /**
     * Add property "parent" between two person
     * @param personFirstName
     * @param personLasteName
     * @param parent
     */
    public void addParent(String personFirstName, String personLasteName, String parent){
        
        Model m = ModelFactory.createDefaultModel();
        Resource person = m.getResource(Namespaces.getPersonUri(personFirstName, personLasteName));

        m.add(person, Dbpedia.parent, m.getResource(parent));
        
        m.write(System.out, "turtle");

        saveModel(m);
    }
    
    /**
     * Add property "child" between two person
     * @param personFirstName
     * @param personLasteName
     * @param child
     */
    public void addChild(String personFirstName, String personLasteName, String child) {
        
        Model m = ModelFactory.createDefaultModel();
        Resource person = m.getResource(Namespaces.getPersonUri(personFirstName, personLasteName));
        m.add(person, Dbpedia.child, m.getResource(child));
        
        m.write(System.out, "turtle");

        saveModel(m);
    }
    
    /**
     * Create a resource Object in semantic
     * @param name
     */
    public Resource createObject(String name) {

        Model m = ModelFactory.createDefaultModel(); 
        String objectURI = Namespaces.getObjectUri(name);
        
        Resource object = m.createResource(objectURI);
        
        object.addLiteral(RDFS.label, name);
        object.addProperty(RDF.type, SempicOnto.Object);

        m.write(System.out, "turtle");

        saveModel(m);

        return object;
    }
    
    /**
     * Create a resource Event in semantic
     * @param name
     * @param type
     */
    public Resource createEvent(String name, String type) {

        Model m = ModelFactory.createDefaultModel(); 
        String eventURI = "";
        
        switch (type){
            case "global":
                eventURI = Namespaces.getEventGlobalUri(name);
                break;
            case "party":
                eventURI = Namespaces.getEventPartyUri(name);
                break;
            default:
                eventURI = Namespaces.getEventGlobalUri(name);
                break;
            
        }
        
        Resource event = m.createResource(eventURI);
        
        event.addLiteral(RDFS.label, name);
        event.addProperty(RDF.type, SempicOnto.Event);

        m.write(System.out, "turtle");

        saveModel(m);

        return event;
    }
    
    /**
     * Create a resource Animal in semantic
     * @param name
     */
    public Resource createAnimal(String name) {

        Model m = ModelFactory.createDefaultModel(); 
        String animalURI = Namespaces.getAnimalUri(name);
        
        Resource animal = m.createResource(animalURI);
        
        animal.addLiteral(RDFS.label, name);
        animal.addProperty(RDF.type, SempicOnto.Animal);

        m.write(System.out, "turtle");

        saveModel(m);

        return animal;
    }
}
