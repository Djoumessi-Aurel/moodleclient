/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import moodleclient.exceptions.ServerUnreachableException;
import org.json.simple.parser.ParseException;

/**
 *
 * @author DELL
 */
public class RequestAPI {
    
    //Make a get request to moodle API and return the result as String
    public static String getAPIResult(String url_str) throws MalformedURLException, IOException, ParseException, ServerUnreachableException{
                   
        URL url = new URL(url_str);
            
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
        con.setRequestMethod("GET");
        con.connect();
            
        int status = con.getResponseCode();
        String res = "";
            
        if(status == 200){
            //the server is reachable, get the request response

            Scanner sc = new Scanner(url.openStream());

            while(sc.hasNext()){
                res += sc.nextLine();
            }
                
        }else{
            //the server is not reachable
            throw new ServerUnreachableException("Server unreachable");
        }
        
        return res;
    }
    
}
