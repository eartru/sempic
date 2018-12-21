/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.services;

import fr.uga.miashs.sempic.SempicException;
import fr.uga.miashs.sempic.entities.SempicUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Singleton;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@Singleton
//@Stateless
public class SempicUserService {

    private Map<String, SempicUser> users;

    @PostConstruct
    public void init() {
        users = new HashMap<>();
    }

    
    public void create(SempicUser u) throws SempicException {
        if (users.containsKey(u.getEmail())) {
            throw new SempicException("The users with email "+u.getEmail()+ " already exists");
        }
        users.put(u.getEmail(), u);
    }
    
    
    @RolesAllowed("ADMIN")
    public List<SempicUser> findAll() {
        return new ArrayList<>(users.values());
    }
}
