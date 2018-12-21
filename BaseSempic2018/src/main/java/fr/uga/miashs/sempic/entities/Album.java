/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
@Entity
@NamedQueries({
@NamedQuery(
        name = "findUserAlbums",
        query = "SELECT DISTINCT a FROM Album a WHERE a.owner=:owner"
)
})
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueAlbumNameForUser", columnNames = {"title","owner_id"})
})
public class Album implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    @NotNull
    @ManyToOne
    private SempicUser owner;

    @NotBlank
    private String title;
    
    /*@OneToMany(mappedBy="album")
    private Set<Photo> photos;*/

    public Album() {}
    
    public Album(SempicUser u) {
        owner = u;
    }

    public long getId() {
        return id;
    }

    public SempicUser getOwner() {
        return owner;
    }
    public void setOwner(SempicUser owner) {
        this.owner= owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /*public Set<Photo> getPhotos() {
        return photos;
    }*/

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (int) (this.id ^ (this.id >>> 32));
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
        final Album other = (Album) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
}
