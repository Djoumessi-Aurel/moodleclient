/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import moodleclient.Moodleclient;
import moodleclient.entity.AssignmentSubmission;
import moodleclient.entity.Cours;
import moodleclient.entity.CourseFile;
import moodleclient.entity.Devoirs;
import moodleclient.entity.RessourceDevoir;
import moodleclient.entity.Sections;
import moodleclient.exceptions.ServerUnreachableException;
import moodleclient.util.HibernateUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author pepi
 */
public class CoursesHelper {
    
    private static final String GET_COURSES_SERVICE_FUNCTION = "mod_assign_get_assignments";
    private static final String GET_COURSE_INFORMATION_SERVICE_FUNCTION = "mod_assign_get_assignments";
    private static final String GET_COURSE_CONTENTS_SERVICE_FUNCTION = "core_course_get_contents";
    private static final String GET_SUBMISSIONS_SERVICE_FUNCTION = "mod_assign_get_submissions";
    private static final String GET_GRADES_SERVICE_FUNCTION = "gradereport_user_get_grade_items";
    private static final String GET_ENROLLMENTS_SERVICE_FUNCTION = "core_enrol_get_enrolled_users";
    
    public CoursesHelper(){
        
    }
    
    
    //returns true if the given user has the given role ("student" or "editingteacher") for the given course (the ids are remote ids)
    public boolean userHasRoleForCourse(Integer userId, String courseId, String role) throws IOException, ServerUnreachableException, ParseException{
        
        String url_str = Moodleclient.serverAddress + "webservice/rest/server.php?wsfunction=" + CoursesHelper.GET_ENROLLMENTS_SERVICE_FUNCTION +"&moodlewsrestformat=json&courseid=" + courseId + "&wstoken=" + Moodleclient.user.getToken();
        String res = RequestAPI.getAPIResult(url_str);
        
        JSONParser parse = new JSONParser();            
        JSONArray jarr = (JSONArray) parse.parse(res);
        
        for(int i=0; i<jarr.size(); i++){
            JSONObject jobj = (JSONObject) jarr.get(i);
            if( ! jobj.get("id").toString().equalsIgnoreCase(String.valueOf(userId))) continue;
            
            JSONArray roles = (JSONArray) jobj.get("roles");
            String a_role = ((JSONObject)roles.get(0)).get("shortname").toString();
            
            return a_role.equalsIgnoreCase(role);
        }
        return false;
    }
   
    //function to get the informations of a given course
    public JSONArray pullCourse(String courseId) throws MalformedURLException, IOException, ServerUnreachableException, ParseException{
   
        String url_str = Moodleclient.serverAddress + "webservice/rest/server.php?wsfunction=" + CoursesHelper.GET_COURSE_INFORMATION_SERVICE_FUNCTION + "&moodlewsrestformat=json&wsfunction=" + CoursesHelper.GET_COURSE_CONTENTS_SERVICE_FUNCTION +"&moodlewsrestformat=json&courseid=" + courseId + "&wstoken=" + Moodleclient.user.getToken();
        String res = RequestAPI.getAPIResult(url_str);
        
        JSONParser parse = new JSONParser();
            
        JSONArray jarr = (JSONArray) parse.parse(res);
        return jarr;
    }
    
    //function to get the list of the courses of a given student
    public JSONArray pullcourses() throws MalformedURLException, IOException, ParseException, ServerUnreachableException{
       
        String url_str = Moodleclient.serverAddress + "webservice/rest/server.php?wsfunction=" + CoursesHelper.GET_COURSES_SERVICE_FUNCTION +"&wstoken=" + Moodleclient.user.getToken() + "&moodlewsrestformat=json";
        String res = RequestAPI.getAPIResult(url_str);
        
        JSONParser parse = new JSONParser();
        JSONObject jobj = (JSONObject) parse.parse(res);
            
        JSONArray jarr = (JSONArray) parse.parse(jobj.get("courses").toString());        
        return jarr;
    }
    
    //function to get the assignments of a user
    public JSONArray pullAssignments() throws MalformedURLException, IOException, ServerUnreachableException, ParseException{
   
        String url_str = Moodleclient.serverAddress + "webservice/rest/server.php?wsfunction=" + CoursesHelper.GET_COURSES_SERVICE_FUNCTION + "&moodlewsrestformat=json&wstoken=" + Moodleclient.user.getToken();
        String res = RequestAPI.getAPIResult(url_str);    
        
        JSONParser parse = new JSONParser();
        JSONObject jobj = (JSONObject) parse.parse(res);
            
        JSONArray jarr = (JSONArray) parse.parse(jobj.get("courses").toString());
        return jarr;
    }
    
    //function to save courses in the local storage
    public void saveCourses(JSONArray coursesjarr, JSONArray coursesWithAssigmentsJarr) throws IOException, MalformedURLException, ServerUnreachableException, ParseException{
        
        for(int i = 0; i < coursesjarr.size(); i++){
            
            JSONObject assignmentCompletionData = new JSONObject(); //Permettra de savoir si tel devoir a été remis ou pas. clé = remoteId du devoir. valeur = 1 (remis) ou 0 (non remis)
        
            //save the course
            JSONObject jobj = (JSONObject) coursesjarr.get(i);
            String courseId = jobj.get("id").toString();
            String a_role = Moodleclient.user.isStudent() ? "student" : "editingteacher";
            
            if(!this.userHasRoleForCourse(Moodleclient.user.getRemoteId(), courseId, a_role)) continue; //Si le cours ne correspond pas au rôle de l'user, on passe au suivant
            
            Moodleclient.session.beginTransaction();
            
            String shortname = jobj.get("shortname").toString();
            Byte b = 1;
            Cours cours = new Cours(jobj.get("fullname").toString(), shortname, shortname, courseId, new Date(), new Date(), new HashSet(), new HashSet(), b);
       
            Moodleclient.session.save(cours);
            
            //get the informations about the course
            JSONArray courseData = this.pullCourse(courseId);
            
            //save the sections of the course
            for(int j = 0; j < courseData.size(); j++){
                
                JSONObject jsection = (JSONObject) courseData.get(j);
                
                Sections section = new Sections(cours, jsection.get("name").toString(), new Date(), new Date(), Integer.valueOf(jsection.get("id").toString()), new HashSet(), (byte)1);
        

                Moodleclient.session.save(section);
                
                //get the ressources of the section
                JSONParser parse = new JSONParser();
                JSONArray modulesArr = (JSONArray) parse.parse(jsection.get("modules").toString());
                
                for(int k = 0; k < modulesArr.size(); k++){
                    
                    JSONObject jmodule = (JSONObject) modulesArr.get(k);
                    
                    // On verifie s'il s'agit d'un assignment
                    if(jmodule.get("modname").equals("assign")){
                        Object remoteId = jmodule.get("instance"); //Le remoteId du devoir
                        Object completionState = ((JSONObject) jmodule.get("completiondata")).get("state"); //état: 1 (remis) ou 0 (non remis)
                        
                        assignmentCompletionData.put(remoteId, completionState);
                    }
                    
                    // il n'y a pas l'attribut "contents" pour les assignements et d'autres modules. Dans ce cas, on passe à l'itération suivante.
                    if(jmodule.get("contents")==null) continue;
                    
                    
                    JSONArray contentsArr = (JSONArray) parse.parse(jmodule.get("contents").toString());
                    
                    for(int l = 0; l < contentsArr.size(); l++){
                        
                        JSONObject jcontent = (JSONObject) contentsArr.get(l);
                        
                        String file_url = jcontent.get("fileurl") + "&token=" + Moodleclient.user.getToken();
                        String hashName = (new Date()).getTime() + "_" + jcontent.get("filename").toString();
                        
                        Downloader.downloadFile(file_url, hashName);
                        
                        //create and save the course file in the database
                        CourseFile courseFile = new CourseFile(section, jcontent.get("filename").toString(), hashName, new Date(), new Date(), (byte)1);
                        
                        Moodleclient.session.save(courseFile);

                    }  
                }
            }
            
            JSONParser parser = new JSONParser();
            
            JSONArray assignments = new JSONArray();
            
            //save the assignments of the course
            for(Object obj: coursesWithAssigmentsJarr){
                
                JSONObject tmpCourse = (JSONObject) obj;
                //System.out.println("**********="  + ": " + tmpCourse.toString());                //DJOUMESSI
                
                if(tmpCourse.get("id").toString().equalsIgnoreCase(jobj.get("id").toString())){

                    //ici
                    assignments = (JSONArray) parser.parse(tmpCourse.get("assignments").toString());
                    
                    break;
                }
            }
            
            Moodleclient.session.getTransaction().commit();
            
            Moodleclient.session.close();
            
            Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
            
            saveAssignments(assignments, cours, assignmentCompletionData);
        
        }
        
        Moodleclient.session.beginTransaction();
        
        Moodleclient.courses =  Moodleclient.session.createQuery("from Cours").list();
        
        Moodleclient.session.getTransaction().commit();

    }
    
    //function to save the assignements of a course
    public void saveAssignments(JSONArray ass_jarr, Cours cours, JSONObject ass_completion_data) throws ParseException, MalformedURLException, IOException, ServerUnreachableException{

        /* Récupérons les notes des devoirs de ce cours */
        JSONObject notes = this.getCourseGrades(cours); System.out.println(notes);
                
        for(int m = 0; m < ass_jarr.size(); m++){
            
            //System.out.println("Valeur ass_jarr :" + ass_jarr);

            JSONObject str = (JSONObject) ass_jarr.get(m);

            String name = str.get("name").toString();
            String remoteId = str.get("id").toString();
            
            String etat = "due";
            try{
                etat = ass_completion_data.get(str.get("id")).toString().equalsIgnoreCase("0") ? "due" : "submitted";
            }catch(NullPointerException e){
                e.printStackTrace();
            }
            
            /*if(str.get("nosubmissions").toString().equalsIgnoreCase("0")){ //Ceci ne marche pas. c'est toujours égal à 0, que le devoir ait été remis ou non
                etat = "submitted";
            }else{
                etat = "due";
            }*/

            if(str.containsKey("introattachments")){

                JSONArray int_att_arr = (JSONArray) str.get("introattachments");
                
                Timestamp ts = new Timestamp((long)str.get("duedate")*1000); 
                Date due_date = new Date(ts.getTime());
                Devoirs devoirs = new Devoirs(cours, name, due_date, etat, remoteId, new Date(), new Date(), new HashSet(), new HashSet());
                //Mise à jour de la note
                try{
                    List notesList = (ArrayList) notes.get(str.get("id"));
                    if(notesList.get(0)!= null) devoirs.setNote(Integer.valueOf(notesList.get(0).toString()));
                    if(notesList.get(1)!= null) devoirs.setNoteMax(Integer.valueOf(notesList.get(1).toString()));
                }catch(NullPointerException e){
                    System.out.println("Erreur lors de l'accès à la note du devoir de remoteId " + remoteId);
                }
                
                Moodleclient.session.beginTransaction();
                Moodleclient.session.save(devoirs);
                Moodleclient.session.getTransaction().commit();

                for(int n = 0; n < int_att_arr.size(); n++){

                    JSONObject int_att_obj = (JSONObject) int_att_arr.get(n);

                    String file_name = int_att_obj.get("filename").toString();
                    String hash_name = (new Date()).getTime() + "_" + file_name;
                    String file_url =  int_att_obj.get("fileurl").toString() + "?token=" + Moodleclient.user.getToken();
                    
                    if(!file_url.isEmpty()){

                        RessourceDevoir res_dev = new RessourceDevoir(devoirs, file_name, hash_name, new Date(), new Date());
                        
                        Moodleclient.session.beginTransaction();
                        
                        //Moodleclient.session.save(devoirs);
                        Moodleclient.session.save(res_dev);
                        
                        Moodleclient.session.getTransaction().commit();
                        
                        Downloader.downloadFile2(file_url, hash_name); //download the file locally
                    }
                }
                
                Moodleclient.session.close();
                        
                Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
                        
                Moodleclient.assignments = Moodleclient.session.createQuery("from Devoirs").list();
                
                //Recherche et pull des soumissions de devoirs correspondant au devoir actuel
                this.pullAssignmentSubmissions(devoirs);
                        
            }
            
        }
    }
    
    
        //Récupère les notes obtenues aux devoirs d'une matière
        public JSONObject getCourseGrades(Cours cours) throws MalformedURLException, IOException, ParseException, ServerUnreachableException{
            JSONObject result = new JSONObject();
            
            String url_str = Moodleclient.serverAddress + "webservice/rest/server.php?wsfunction=" + CoursesHelper.GET_GRADES_SERVICE_FUNCTION+ "&moodlewsrestformat=json&courseid=" + cours.getRemoteId() + "&userid=" + Moodleclient.user.getRemoteId() + "&wstoken=" + Moodleclient.PRIVILEGED_TOKEN;
            String res = RequestAPI.getAPIResult(url_str);                    
            
            JSONParser parse = new JSONParser();
            
            JSONObject gradeObj = (JSONObject) parse.parse(res);
            JSONArray gradeArr = (JSONArray) gradeObj.get("usergrades");
            
            JSONObject tempObj; JSONArray tempArr;
            
            for(int i=0; i<gradeArr.size(); i++){
                tempObj = (JSONObject) gradeArr.get(i);
                tempArr = (JSONArray) tempObj.get("gradeitems");
                
                for(int j=0; j<tempArr.size(); j++){
                    JSONObject obj = (JSONObject) tempArr.get(j); System.out.println("obj"+obj);
                    
                    if(obj.get("itemmodule") != null && obj.get("itemmodule").toString().equalsIgnoreCase("assign")){
                        List<Object> notes = new ArrayList<>();
                        notes.add(obj.get("graderaw"));
                        notes.add(obj.get("grademax"));

                        result.put(obj.get("iteminstance"), notes);
                    }
                    
                }
                
            }
        
            return result;
        }
    
    
        public void pullAssignmentSubmissions(Devoirs dev) throws MalformedURLException, IOException, ServerUnreachableException, ParseException{
        
        JSONArray jarr, jarrSubm, jarrPlugins, jarrFileAreas, jarrSubmFinal;
        String remoteId = String.valueOf(Moodleclient.user.getRemoteId());
        
        String url_str = Moodleclient.serverAddress + "webservice/rest/server.php?wsfunction=" + CoursesHelper.GET_SUBMISSIONS_SERVICE_FUNCTION + "&assignmentids[]=" + dev.getRemoteId() + "&wstoken=" + Moodleclient.PRIVILEGED_TOKEN + "&moodlewsrestformat=json";
        System.out.println(url_str);
        String res = RequestAPI.getAPIResult(url_str);
            
            Moodleclient.session.beginTransaction();

            JSONParser parse = new JSONParser();
            JSONObject jobj = (JSONObject) parse.parse(res);
            
            try{
                jarr = (JSONArray) parse.parse(jobj.get("assignments").toString());
            }
            catch(NullPointerException e){ //L'objet n'a pas d'attribut assignments
                System.out.println("***Vous n'avez pas le droit d'accéder à ces données.***\nRésultat de la requête: "+jobj);
                Moodleclient.session.getTransaction().commit();
                return;
            }
            
            
            for(int i=0; i<jarr.size(); i++){
                JSONObject obj1 = (JSONObject) jarr.get(i);
                
                if(obj1.get("assignmentid").toString().equals(dev.getRemoteId())){
                    jarrSubm = (JSONArray) parse.parse(obj1.get("submissions").toString()); //Tableau des soumissions (tous étudiants confondus)
                    
                    for(int j=0; j<jarrSubm.size(); j++){
                    JSONObject obj2 = (JSONObject) jarrSubm.get(j);
                
                        if(obj2.get("userid").toString().equals(remoteId)){ //S'il s'agit d'une soumission de l'utilisateur actuel
                            jarrPlugins = (JSONArray) parse.parse(obj2.get("plugins").toString());
                            
                            JSONObject obj3 = (JSONObject) jarrPlugins.get(0); //jarrPlugins est un array à un seul élément
                            jarrFileAreas = (JSONArray) parse.parse(obj3.get("fileareas").toString());
                            
                            JSONObject obj4 = (JSONObject) jarrFileAreas.get(0); //jarrFileAreas est un array à un seul élément
                            jarrSubmFinal = (JSONArray) parse.parse(obj4.get("files").toString()); //Tableau des soumissions de l'utilisateur actuel
                            
                            //On peut passer au pull de ces soumissions
                            
                            for(int k=0; k<jarrSubmFinal.size(); k++){
                                JSONObject obj5 = (JSONObject) jarrSubmFinal.get(k); System.out.println(obj5);
                                
                                String fileName = obj5.get("filename").toString();
                                String hashName = (new Date()).getTime() + "_" + fileName;
                                String fileUrl = obj5.get("fileurl").toString() + "?token=" + Moodleclient.PRIVILEGED_TOKEN;
                                byte b = 1;
                                
                                //1. On enregistre la soumission en BD
                                AssignmentSubmission ass = new AssignmentSubmission(dev, fileName, hashName, new Date(), new Date(), b);
                                Moodleclient.session.save(ass);
                                
                                //2. On télécharge le fichier correspondant
                                Downloader.downloadFile(fileUrl, hashName);
                            }
                        }
                    }
                }
            }
            
            Moodleclient.session.getTransaction().commit();
        
    }
 
    
}
