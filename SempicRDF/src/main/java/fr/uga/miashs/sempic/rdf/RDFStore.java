/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

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
import org.apache.jena.update.Update;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

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

    
    public Resource createPhoto(long photoId, long albumId, long ownerId) {
        // create an empty RDF graph
        Model m = ModelFactory.createDefaultModel();
        // create an instance of Photo in Model m
        Resource pRes = m.createResource(Namespaces.getPhotoUri(photoId));
        pRes.addProperty(RDF.type, SempicOnto.Photo);

        pRes.addLiteral(SempicOnto.albumId, albumId);
        pRes.addLiteral(SempicOnto.ownerId, ownerId);

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
                + "FILTER (?p1 IN (<" + SempicOnto.depicts + ">,<" + SempicOnto.takenIn + ">,<" + SempicOnto.takenBy + ">)) "
                + "}"
                + "}";
        Model m = cnx.queryConstruct(s);
        return m.getResource(pUri);
    }

    public List<Resource> getPersons(String q) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?p <" + RDFS.label + "> ?label "
                + "} WHERE {"
                + "?p a <" + SempicOnto.Person + "> ; <" + RDFS.label +"> ?label. FILTER (regex(?label, \"" + q +"\", \"i\"))}");
        
        return m.listSubjects().toList();
    }
    
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
    
    public void annotatePhoto(long id, String label, List<String> persons, List<String> objects, String country, String event, Date date) {
        
        Model m = ModelFactory.createDefaultModel();

        Resource photo = m.getResource(Namespaces.getPhotoUri(id));
        
        List<Resource> perso = new ArrayList();
        persons.forEach(p -> {
            perso.add(m.getResource(p));
        });
        
        List<Resource> obj = new ArrayList();
        objects.forEach(o -> {
            obj.add(m.getResource(o));
        });
        Resource ctry = m.getResource(country);
       // Resource rgn = m.getResource(region);
       // Resource dpt = m.getResource(department);
       // Resource cty = m.getResource(city);
        Resource evt = m.getResource(event);
   
        perso.forEach(p -> {
            m.add(photo, SempicOnto.depicts, p);
        });
        obj.forEach(o -> {
            m.add(photo, SempicOnto.depicts, o);
        });
        m.add(photo, SempicOnto.takenIn, ctry);
        //m.add(photo, SempicOnto.takenIn, rgn);
        //m.add(photo, SempicOnto.takenIn, dpt);
        //m.add(photo, SempicOnto.takenIn, cty);
        m.add(photo, SempicOnto.takenOn, evt);
        m.add(photo, SempicOnto.takenOn, date.toString());
        m.add(photo, RDFS.label, label);

        m.write(System.out, "turtle");

        saveModel(m);
    }
    
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
    
    public List<Resource> getObject(String q) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?p <" + RDFS.label + "> ?label "
                + "} WHERE {"
                + "?p a ?type ; "
                + "<" + RDFS.label +"> ?label. "
                + "FILTER ( ?type IN ( <"+SempicOnto.Object+">, <" +SempicOnto.Animal+">) && regex(?label, \"" + q +"\", \"i\"))}");
        
        return m.listSubjects().toList();
    }
    
    public List<Resource> getEvent(String q) {
        
        Model m = cnx.queryConstruct("CONSTRUCT { ?p <" + RDFS.label + "> ?name "
                + "} WHERE {"
                + "?p a <"+SempicOnto.Event +"> ; "
                + "<" + RDFS.label +"> ?label. "
                + "FILTER (regex(?label, \"" + q +"\", \"i\"))"
                        + "BIND (STR(?label)  AS ?name) }");
        
        return m.listSubjects().toList();
    }
    
    public void addFriend(String person, String friend) {
        
        Model m = ModelFactory.createDefaultModel();
        Resource p = m.getResource(person);
        Resource f = m.getResource(friend);
        System.out.println("test:");
        System.out.println(p);
        System.out.println(f);
        m.add(m.getResource(person), SempicOnto.isFriendOf, m.getResource(friend));
        
        m.write(System.out, "turtle");

        saveModel(m);
    }
}
