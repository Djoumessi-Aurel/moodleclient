/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import moodleclient.entity.CourseFile;
import moodleclient.entity.PrivateFile;
import moodleclient.exceptions.NotValidSessionException;
import moodleclient.exceptions.ServerUnreachableException;
import moodleclient.util.HibernateUtil;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pepi
 */
public class PrivateFileHelper {
    
    //default constructor
    public PrivateFileHelper(){
        
    }
    
    //function to pull the user's private files
    public void pullPrivateFiles() throws ParseException, NotValidSessionException, MalformedURLException, ServerUnreachableException, IOException{

        // get URL content

        //get the list of private file objects

        String url_str = moodleclient.Moodleclient.serverAddress + "webservice/rest/server.php?contextlevel=user&instanceid=" + moodleclient.Moodleclient.user.getRemoteId() + "&component=user&filearea=private&itemid=0&contextid=-1&filepath=&wstoken=" + moodleclient.Moodleclient.user.getToken() + "&wsfunction=core_files_get_files&moodlewsrestformat=json&filename";
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
            
            jarr = (JSONArray) parse.parse(jobj.get("files").toString());
            
            Session session = HibernateUtil.getSessionFactory().openSession();
                
            session.beginTransaction();
            
            for(Object file: jarr){
                
                JSONObject jfile = (JSONObject) file;
                
                String contextid = jfile.get("contextid").toString();
                String fileName = jfile.get("filename").toString();
                String encodedFileName = MyURLEncoder.encodeValue(fileName);
                //System.out.println("### Nom du fichier privé: " + fileName);
                //System.out.println("### Nom du encodé: " + encodedFileName);
                
                //build the url to download the file
                String file_str_url = moodleclient.Moodleclient.serverAddress + "webservice/pluginfile.php/" + contextid + "/user/private/" + encodedFileName + "?forcedownload=1&token=" + moodleclient.Moodleclient.user.getToken();
                
                
                //downloading the file
                URL fileURL = new URL(file_str_url);
                        
                InputStream in = fileURL.openStream();

                String hashName = (new Date()).getTime() + "_" + fileName;

                FileOutputStream fos = new FileOutputStream("./files/" + hashName);

                int length = -1;

                byte[] buffer = new byte[1024];// buffer for portion of data from connection

                while ((length = in.read(buffer)) > -1) {
                    fos.write(buffer, 0, length);
                }

                fos.close();
                in.close();
                
                byte b = 1;
                
                //create and save the private file in the database
                PrivateFile privateFile = new PrivateFile(fileName, hashName, new Date(), new Date(), b);
                
                session.save(privateFile);
            }
            
            session.getTransaction().commit();
            
            session.close();
            
            Session session2 = HibernateUtil.getSessionFactory().openSession();
            session2.beginTransaction();
            
            moodleclient.Moodleclient.privateFiles = session2.createQuery("from PrivateFile").list();
            
            session2.getTransaction().commit();
            session2.close();
            
        }else{
            //the server is not reachable
            //***************************
            throw new ServerUnreachableException("Server unreachable");
        }
    }
}
