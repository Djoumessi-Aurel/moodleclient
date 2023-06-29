/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import static moodleclient.Moodleclient.session;
import moodleclient.entity.Users;
import moodleclient.exceptions.NotValidSessionException;
import moodleclient.exceptions.ServerUnreachableException;
import moodleclient.exceptions.WrongCredentialsException;
import moodleclient.util.HibernateUtil;
import org.hibernate.Session;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pepi
 */
public class AccountHelper {
    
    public AccountHelper(){
       
    }
    
    //function to get the token of a user
    public Map<String, String> getUserData(String username, String password) throws MalformedURLException, ProtocolException, IOException, ParseException, WrongCredentialsException{
        
        Map<String, String> result = new HashMap<String, String>();
        
        String serverAdd = moodleclient.Moodleclient.serverAddress;
        
        // si l'adresse du serveur fournie par l'utilisateur ne se termine pas par /, alors on l'ajoute
        if(!serverAdd.substring(serverAdd.length()-1, serverAdd.length()).equals("/")){
            serverAdd += "/";
        }
                
        String login_url_str = serverAdd + "/login/token.php?username=" + username + "&password=" + password + "&service=moodle_mobile_app";
        
        URL login_url = new URL(login_url_str);
            
        HttpURLConnection con = (HttpURLConnection) login_url.openConnection();
            
        con.setRequestMethod("GET");
        con.connect();
            
        int status = con.getResponseCode();
        
        System.out.println("Status : "+status);
        
        String user_token = "";
        String remote_id = "";
            
        if(status == 200){
            //the server is reachable
            //get the request response
            String res = "";

            Scanner sc = new Scanner(login_url.openStream());

            while(sc.hasNext()){
                res += sc.nextLine();
            }
            
            System.out.println("res : "+res);
            JSONParser parse = new JSONParser();
            JSONObject jobj = (JSONObject) parse.parse(res);
                            
            if(jobj.keySet().contains("token")){
                //the login credentials are correct
                //get the user's token
                user_token = jobj.get("token").toString();
                
                
                // Ayant recuperer le token, on va l'utiliser pour avoir le remotId
                String url = serverAdd + "webservice/rest/server.php?wstoken="+user_token+"&wsfunction=core_webservice_get_site_info&moodlewsrestformat=json"; // ajout de ma part pour recuperer le remoteid
                remote_id = RemoteId.getRemoteId(url);
                //System.out.println("remote_id : "+ remote_id);
                
                result.put("token", user_token);
                result.put("remoteid", remote_id);

                //System.out.println("Utilisateur récupéré: " + result.toString());

                //result.put("remoteid", jobj.get("remoteid").toString());
                
            }else{
       
                throw new WrongCredentialsException("Wrong credentials");
            } 
            
        }else{
            
            throw new WrongCredentialsException("Wrong credentials");
        }
        
        return result;
    }
    
    //function to save the user profile in the local database
    public Users saveAccount(String username, String password, String userToken, Byte isStudent, int remoteId){
       
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        session.beginTransaction();
                    
        Users user = new Users(username, password, userToken, new Date(), new Date(), moodleclient.Moodleclient.serverAddress, isStudent, remoteId);
                    
        session.save(user);
       
        session.getTransaction().commit();
        
        return user;
    }
    
    //function to check if there is already an account created in the client
    public boolean accountCreated(){
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        session.beginTransaction();
        
        List users = session.createQuery("from Users").list();
        boolean accountCreated = users.size() == 1;
        
        session.getTransaction().commit();
        
        return accountCreated;
    }
    
    //function the local account
    public Users getAccount(){
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        session.beginTransaction();
        
        List users = session.createQuery("from Users").list();
        boolean accountCreated = users.size() == 1;
        
        session.getTransaction().commit();
        
        return (Users) users.get(0);
    }
    
    //function to delete the local account
    public void deleteAccount(Users user){
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        session.beginTransaction();
        
        session.delete(user);
        
        session.getTransaction().commit();
        
        session.close();
    }
    
}
