/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic;

import fr.uga.miashs.sempic.entities.SempicUser;
import fr.uga.miashs.sempic.entities.SempicUserType;
import fr.uga.miashs.sempic.services.SempicUserFacade;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;

/**
 * In this class, some generic application config are given
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@DatabaseIdentityStoreDefinition(
        dataSourceLookup = ApplicationConfig.DATA_SOURCE,
        callerQuery = "select passwordhash from sempic.sempicuser where email=?",
        groupsQuery = "select userType from sempic.sempicuser where email = ?",
        hashAlgorithm = Pbkdf2PasswordHash.class,
        hashAlgorithmParameters = { "Pbkdf2PasswordHash.Iterations=3072", 
                                    "Pbkdf2PasswordHash.Algorithm=PBKDF2WithHmacSHA512", 
                                    "Pbkdf2PasswordHash.SaltSizeBytes=64"},

        priority = 30)

@DataSourceDefinition(
        name=ApplicationConfig.DATA_SOURCE,
        className="org.apache.derby.jdbc.ClientDataSource",
        url="jdbc:derby://localhost:1527/SempicDB",
        databaseName="SempicDB",
        user="sempic",
        password="sempic"
)

@FormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/faces/login.xhtml",
                errorPage = "/login-error.html"))

@FacesConfig( version = FacesConfig.Version.JSF_2_3)

@ApplicationScoped
@Startup
public class ApplicationConfig {
    
    public final static String DATA_SOURCE="java:app/sempicdb";
    
    @Inject
    private Pbkdf2PasswordHash passwordHash;
    
    @Inject
    private SempicUserFacade userFacade;
    
    @PostConstruct
    public void init() {
        SempicUser admin = new SempicUser();
        admin.setFirstname("Jack");
        admin.setLastname("Rabbit");
        admin.setEmail("admin@miashs.fr");
        admin.setUserType(SempicUserType.ADMIN);
        admin.setPasswordHash(passwordHash.generate("admin".toCharArray()));
        
        try {
            userFacade.create(admin);
            Logger.getLogger(ApplicationConfig.class.getName()).log(Level.INFO, "Admin created");
            System.out.println("------------------------YES-----------------");
        }
        catch (SempicModelException e) {
            Logger.getLogger(ApplicationConfig.class.getName()).log(Level.INFO, "Admin already exists");
            System.out.println("------------------------SHIT-----------------");
        }
        
    }
    
}
