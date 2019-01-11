/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.entities;

import java.io.Serializable;

/**
 *
 *
 */

public class GenericSemantic implements Serializable {
        
    private String uri;
    private String label;

    public GenericSemantic() {
    }
    
    public GenericSemantic(String uri, String label) {
       this.uri = uri;
       this.label = label;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
