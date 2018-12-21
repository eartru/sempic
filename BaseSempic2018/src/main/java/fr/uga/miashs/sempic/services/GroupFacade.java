/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.services;

import fr.uga.miashs.sempic.entities.SempicGroup;
import javax.ejb.Stateless;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@Stateless
public class GroupFacade extends AbstractJpaFacade<Long,SempicGroup>{
    
    public GroupFacade() {
        super(SempicGroup.class);
    }
    
    
}
