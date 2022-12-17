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
        
        String login_url_str = moodleclient.Moodleclient.serverAddress + "login/token.php?username=" + username + "&password=" + password + "&service=moodle_mobile_app";
            
        URL login_url = new URL(login_url_str);
            
        HttpURLConnection con = (HttpURLConnection) login_url.openConnection();
            
        con.setRequestMethod("GET");
        con.connect();
            
        int status = con.getResponseCode();
        String user_token; 
            
        if(status == 200){
            //the server is reachable
            //get the request response
            String res = "";

            Scanner sc = new Scanner(login_url.openStream());

            while(sc.hasNext()){
                res += sc.nextLine();
            }
                
            JSONParser parse = new JSONParser();
            JSONObject jobj = (JSONObject) parse.parse(res);
                System.out.println(jobj);
            if(jobj.keySet().contains("token")){
                //the login credentials are correct
                //get the user's token
                user_token = jobj.get("token").toString();
                System.out.println("token="+user_token);
                
                result.put("token", user_token);
                //result.put("remoteid", jobj.get("remoteid").toString());//LIGNE DE L'ANCIEN CODE
                result.put("remoteid", "1");
                
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
