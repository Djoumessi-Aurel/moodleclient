package moodleclient.entity;
// Generated 17 juin 2022 05:48:45 by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * CourseFile generated by hbm2java
 */
public class CourseFile  implements java.io.Serializable {


     private Integer id;
     private Sections sections;
     private String fileName;
     private String hashName;
     private Date createdAt;
     private Date updatedAt;
     private Byte synced;

    public CourseFile() {
    }

	
    public CourseFile(String fileName) {
        this.fileName = fileName;
    }
    public CourseFile(Sections sections, String fileName, String hashName, Date createdAt, Date updatedAt, Byte synced) {
       this.sections = sections;
       this.fileName = fileName;
       this.hashName = hashName;
       this.createdAt = createdAt;
       this.updatedAt = updatedAt;
       this.synced = synced;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Sections getSections() {
        return this.sections;
    }
    
    public void setSections(Sections sections) {
        this.sections = sections;
    }
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getHashName() {
        return this.hashName;
    }
    
    public void setHashName(String hashName) {
        this.hashName = hashName;
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
    
    public Byte getSynced() {
        return synced;
    }

    public void setSynced(Byte synced) {
        this.synced = synced;
    }



}


