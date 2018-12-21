/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.entities;

import java.io.InputStream;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */

@NamedQueries({
@NamedQuery(
        name = "findPhotoAlbum",
        query = "SELECT DISTINCT p FROM Photo p WHERE p.album=:album"
)
})        
@Entity
public class Photo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;
    
    @ManyToOne
    private Album album;
    
    private String filename;

    public long getId() {
        return id;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return getAlbum().getId()+"/"+getId();
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Photo other = (Photo) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Photo{" + "id=" + id + ", album=" + album + ", filename=" + filename + '}';
    }
    
    
    
    
}
