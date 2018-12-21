/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.services;

import fr.uga.miashs.sempic.entities.SempicUser;
import javax.ejb.Stateless;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */

@Stateless
public class SempicUserFacade extends AbstractJpaFacade<Long,SempicUser> {

    public SempicUserFacade() {
        super(SempicUser.class);
    }
    
    public SempicUser login(String email, String password) {
        Query q = getEntityManager().createNamedQuery("login");
        q.setParameter("email", email);
        q.setParameter("password", password);
        return (SempicUser) q.getSingleResult();
    }

    public SempicUser readByEmail(String email) {
        Query q = getEntityManager().createNamedQuery("readByEmail");
        q.setParameter("email", email);
        return (SempicUser) q.getSingleResult();
    }
    
    
}
