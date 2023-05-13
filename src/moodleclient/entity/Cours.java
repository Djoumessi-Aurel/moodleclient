package moodleclient.entity;
// Generated 17 juin 2022 05:48:45 by Hibernate Tools 4.3.1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Cours generated by hbm2java
 */
public class Cours  implements java.io.Serializable {


     private Integer id;
     private String nom;
     private String nomAbrege;
     private String description;
     private String remoteId;
     private Date createdAt;
     private Date updatedAt;
     private Set sectionses = new HashSet(0);
     private Set devoirses = new HashSet(0);
     private Byte synced;


    public Cours() {
    }

	
    public Cours(String remoteId) {
        this.remoteId = remoteId;
    }
    public Cours(String nom, String nomAbrege, String description, String remoteId, Date createdAt, Date updatedAt, Set sectionses, Set devoirses, Byte synced) {
       this.nom = nom;
       this.nomAbrege = nomAbrege;
       this.description = description;
       this.remoteId = remoteId;
       this.createdAt = createdAt;
       this.updatedAt = updatedAt;
       this.sectionses = sectionses;
       this.devoirses = devoirses;
       this.synced = synced;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNom() {
        return this.nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getDescription() {
        return this.description;
    }
    
    public String getNomAbrege() {
        return nomAbrege;
    }

    public void setNomAbrege(String nomAbrege) {
        this.nomAbrege = nomAbrege;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public String getRemoteId() {
        return this.remoteId;
    }
    
    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }
    public Date getCreatedAt() {
        return this.createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getUpdatedAt() {
        return this.updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Set getSectionses() {
        return this.sectionses;
    }
    
    public void setSectionses(Set sectionses) {
        this.sectionses = sectionses;
    }
    public Set getDevoirses() {
        return this.devoirses;
    }
    
    public void setDevoirses(Set devoirses) {
        this.devoirses = devoirses;
    }
    
    public Byte getSynced() {
        return synced;
    }

    public void setSynced(Byte synced) {
        this.synced = synced;
    }


}


