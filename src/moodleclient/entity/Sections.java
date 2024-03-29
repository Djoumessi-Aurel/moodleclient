package moodleclient.entity;
// Generated 17 juin 2022 05:48:45 by Hibernate Tools 4.3.1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Sections generated by hbm2java
 */
public class Sections  implements java.io.Serializable {


     private Integer id;
     private Cours cours;
     private String nom;
     private Date createdAt;
     private Date updatedAt;
     private Integer remoteId;
     private Set courseFiles = new HashSet(0);
     private Byte synced;
     

    public Sections() {
    }

    public Sections(Cours cours, String nom, Date createdAt, Date updatedAt, Integer remoteId, Set courseFiles, Byte synced) {
       this.cours = cours;
       this.nom = nom;
       this.createdAt = createdAt;
       this.updatedAt = updatedAt;
       this.remoteId = remoteId;
       this.courseFiles = courseFiles;
       this.synced = synced;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Cours getCours() {
        return this.cours;
    }
    
    public void setCours(Cours cours) {
        this.cours = cours;
    }
    public String getNom() {
        return this.nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
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
    public Integer getRemoteId() {
        return this.remoteId;
    }
    
    public void setRemoteId(Integer remoteId) {
        this.remoteId = remoteId;
    }
    public Set getCourseFiles() {
        return this.courseFiles;
    }
    
    public void setCourseFiles(Set courseFiles) {
        this.courseFiles = courseFiles;
    }
    
    public Byte getSynced() {
        return synced;
    }

    public void setSynced(Byte synced) {
        this.synced = synced;
    }


}


