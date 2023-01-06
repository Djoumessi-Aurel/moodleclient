/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import moodleclient.Moodleclient;
import moodleclient.entity.Cours;
import moodleclient.entity.CourseFile;
import moodleclient.entity.Devoirs;
import moodleclient.entity.RessourceDevoir;
import moodleclient.entity.Sections;
import moodleclient.entity.Users;
import moodleclient.exceptions.ServerUnreachableException;
import moodleclient.util.HibernateUtil;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import sun.misc.IOUtils;

/**
 *
 * @author pepi
 */
public class CoursesHelper {
    
    private static String GET_COURSES_SERVICE_FUNCTION = "mod_assign_get_assignments";
    private static String GET_COURSE_INFORMATION_SERVICE_FUNCTION = "mod_assign_get_assignments";
    private static String GET_COURSE_CONTENTS_SERVICE_FUNCTION = "core_course_get_contents";
    
    public CoursesHelper(){
        
    }
    
    //function to get the list of the courses of a given student
    public JSONArray pullcourses() throws MalformedURLException, IOException, ParseException, ServerUnreachableException{
       
        String url_str = Moodleclient.serverAddress + "webservice/rest/server.php?wsfunction=" + this.GET_COURSES_SERVICE_FUNCTION +"&wstoken=" + Moodleclient.user.getToken() + "&moodlewsrestformat=json";
            
        URL url = new URL(url_str);
            
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
        con.setRequestMethod("GET");
        con.connect();
            
        int status = con.getResponseCode();
        JSONArray jarr;
            
        if(status == 200){
            //the server is reachable
            //get the request response
            String res = "";

            Scanner sc = new Scanner(url.openStream());

            while(sc.hasNext()){
                res += sc.nextLine();
            }

            JSONParser parse = new JSONParser();
            JSONObject jobj = (JSONObject) parse.parse(res);
            
            jarr = (JSONArray) parse.parse(jobj.get("courses").toString());
                
        }else{
            //the server is not reachable
            //***************************
            throw new ServerUnreachableException("Server unreachable");
        }
        
        return jarr;
    }
    
    //function to get the informations of a given course
    public JSONArray pullCourse(String courseId) throws MalformedURLException, IOException, ServerUnreachableException, ParseException{
   
        String url_str = Moodleclient.serverAddress + "webservice/rest/server.php?wsfunction=" + this.GET_COURSE_INFORMATION_SERVICE_FUNCTION + "&moodlewsrestformat=json&wsfunction=" + this.GET_COURSE_CONTENTS_SERVICE_FUNCTION +"&moodlewsrestformat=json&courseid=" + courseId + "&wstoken=" + Moodleclient.user.getToken();
            
        URL url = new URL(url_str);
            
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
        con.setRequestMethod("GET");
        con.connect();
            
        int status = con.getResponseCode();
        
        JSONArray jarr;
            
        if(status == 200){
            //the server is reachable
            //get the request response
            String res = "";

            Scanner sc = new Scanner(url.openStream());

            while(sc.hasNext()){
                res += sc.nextLine();
            }
            
            JSONParser parse = new JSONParser();
            
            jarr = (JSONArray) parse.parse(res);
                
        }else{
            //the server is not reachable
            //***************************
            throw new ServerUnreachableException("Server unreachable");
        }
        
        return jarr;
    }
    
    //function to get the assignments of a user
    public JSONArray pullAssignments() throws MalformedURLException, IOException, ServerUnreachableException, ParseException{
   
        String url_str = Moodleclient.serverAddress + "webservice/rest/server.php?wsfunction=" + this.GET_COURSES_SERVICE_FUNCTION + "&moodlewsrestformat=json&wstoken=" + Moodleclient.user.getToken();
            
        URL url = new URL(url_str);
            
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
        con.setRequestMethod("GET");
        con.connect();
            
        int status = con.getResponseCode();
        
        JSONArray jarr;
            
        if(status == 200){
            //the server is reachable
            //get the request response
            String res = "";

            Scanner sc = new Scanner(url.openStream());

            while(sc.hasNext()){
                res += sc.nextLine();
            }
            
            JSONParser parse = new JSONParser();
            
            JSONObject jobj = (JSONObject) parse.parse(res);
            
            jarr = (JSONArray) parse.parse(jobj.get("courses").toString());
                
        }else{
            //the server is not reachable
            //***************************
            throw new ServerUnreachableException("Server unreachable");
        }
        
        return jarr;
    }
    
    
    //function to save courses in the local storage
    public void saveCourses(JSONArray coursesjarr, JSONArray coursesWithAssigmentsJarr) throws IOException, MalformedURLException, ServerUnreachableException, ParseException{
        
        for(int i = 0; i < coursesjarr.size(); i++){
            
            Moodleclient.session.beginTransaction();
        
            //save the course
            JSONObject jobj = (JSONObject) coursesjarr.get(i);
          
            Cours cours = new Cours(jobj.get("fullname").toString(), jobj.get("shortname").toString(), jobj.get("id").toString(), new Date(), new Date(), new HashSet(), new HashSet());
       
            Moodleclient.session.save(cours);
            
            //get the informations about the course
            JSONArray courseData = this.pullCourse(jobj.get("id").toString());
            
            //save the sections of the course
            for(int j = 1; j < courseData.size(); j++){
                
                JSONObject jsection = (JSONObject) courseData.get(j);
                
                Sections section = new Sections(cours, jsection.get("name").toString(), new Date(), new Date(), Integer.valueOf(jsection.get("id").toString()), new HashSet());
        

                Moodleclient.session.save(section);
                
                //get the ressources of the section
                JSONParser parse = new JSONParser();
                JSONArray modulesArr = (JSONArray) parse.parse(jsection.get("modules").toString());
                
                for(int k = 0; k < modulesArr.size(); k++){
                    
                    JSONObject jmodule = (JSONObject) modulesArr.get(k);
                                        
                    // Ajoutons une petite condition
                    // On verifie s'il s'agit d'un assignment
                    if(jmodule.get("modname").equals("assign")){
                        // ceci est un assignment, on saute cette iteration de la boucle
                        continue;
                    }
                    
                    // il n'y a pas l'attribut "contents" pour les assignements

                    JSONArray contentsArr = (JSONArray) parse.parse(jmodule.get("contents").toString());
                    
                    for(int l = 0; l < contentsArr.size(); l++){
                        
                        JSONObject jcontent = (JSONObject) contentsArr.get(l);
                        
                        URL fileURL = new URL(jcontent.get("fileurl") + "&token=" + Moodleclient.user.getToken());
                        
                        InputStream in = fileURL.openStream();
                        
                        String hashName = (new Date()).getTime() + "_" + jcontent.get("filename").toString();
        
                        FileOutputStream fos = new FileOutputStream("./files/" + hashName);
       
                        int length = -1;
                        
                        byte[] buffer = new byte[1024];// buffer for portion of data from connection
                        
                        while ((length = in.read(buffer)) > -1) {
                            fos.write(buffer, 0, length);
                        }
                        
                        fos.close();
                        in.close();
                        
                        //create and save the course file in the database
                        CourseFile courseFile = new CourseFile(section, jcontent.get("filename").toString(), hashName, new Date(), new Date());
                        
                        Moodleclient.session.save(courseFile);

                    }  
                }
            }
            
            JSONParser parser = new JSONParser();
            
            JSONArray assignments = new JSONArray();
            
            //save the assignments of the course
            for(Object obj: coursesWithAssigmentsJarr){
                
                JSONObject tmpCourse = (JSONObject) obj;
                
                if(tmpCourse.get("id").toString().equalsIgnoreCase(jobj.get("id").toString())){

                    //ici
                    assignments = (JSONArray) parser.parse(tmpCourse.get("assignments").toString());
                    
                    break;
                }
            }
            
            Moodleclient.session.getTransaction().commit();
            
            Moodleclient.session.close();
            
            Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
            
            saveAssignments(assignments, cours);
        
        }
        
        Moodleclient.session.beginTransaction();
        
        Moodleclient.courses =  Moodleclient.session.createQuery("from Cours").list();
        
        Moodleclient.session.getTransaction().commit();

    }
    
    //function to save the assignements of a course
    public void saveAssignments(JSONArray ass_jarr, Cours cours) throws ParseException, MalformedURLException, IOException, ServerUnreachableException{
                    
        for(int m = 0; m < ass_jarr.size(); m++){
            
            System.out.println("Valeur ass_jarr :" + ass_jarr);

            JSONObject str = (JSONObject) ass_jarr.get(m);

            String name = str.get("name").toString();
            String etat = "";
            
            if(str.get("nosubmissions").toString().equalsIgnoreCase("0")){
                etat = "submitted";
            }else{
                etat = "due";
            }

            if(str.containsKey("introattachments")){

                JSONArray int_att_arr = (JSONArray) str.get("introattachments");

                for(int n = 0; n < int_att_arr.size(); n++){

                    JSONObject int_att_obj = (JSONObject) int_att_arr.get(n);

                    String file_name = int_att_obj.get("filename").toString();
                    String file_url =  int_att_obj.get("fileurl").toString() + "?token=" + Moodleclient.user.getToken();

                    Timestamp ts = new Timestamp((long)str.get("duedate")*1000); 

                    Date due_date = new Date(ts.getTime());
                    
                    Devoirs devoirs = new Devoirs(cours, name, due_date, etat, str.get("id").toString(), new Date(), new Date(), new HashSet(), new HashSet());
                    
                    if(!file_url.isEmpty()){

                        RessourceDevoir res_dev = new RessourceDevoir(devoirs, file_name, file_name, new Date(), new Date());
                        
                        Moodleclient.session.beginTransaction();
                        
                        Moodleclient.session.save(devoirs);
                        Moodleclient.session.save(res_dev);
                        
                        Moodleclient.session.getTransaction().commit();
                        
                        Moodleclient.session.close();
                        
                        Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
                        
                        Moodleclient.assignments = Moodleclient.session.createQuery("from Devoirs").list();
                        
                        URL f_url = new URL(file_url);

                        HttpURLConnection con = (HttpURLConnection) f_url.openConnection();

                        con.setRequestMethod("GET");
                        con.connect();

                        int status = con.getResponseCode();

                        if(status == 200){
                            //the server is reachable
                            //get the request response

                            InputStream in = f_url.openStream();

                            BufferedInputStream bis = new BufferedInputStream(in);
                            FileOutputStream fos = new FileOutputStream("./files/" + file_name);

                            byte[] data = new byte[1024];
                            int count;

                            while ((count = bis.read(data, 0, 1024)) != -1) {
                                fos.write(data, 0, count);
                            }

                        }else{
                            //the server is not reachable
                            //***************************
                            throw new ServerUnreachableException("Server unreachable");
                        }
                    }
                }
            }
        }
    }
}
