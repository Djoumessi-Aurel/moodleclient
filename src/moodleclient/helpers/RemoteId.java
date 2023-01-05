/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

/**
 *
 * @author STEVE LENING
 */
public class RemoteId {
    static String getRemoteId (String url){
        try{
            URL login_url = new URL(url);
            HttpURLConnection con = (HttpURLConnection)login_url.openConnection();

            con.setRequestMethod("GET");
            con.connect();

            int status = con.getResponseCode();

            if(status == 200){
                String res = "";
                Scanner sc = new Scanner(login_url.openStream());

                while(sc.hasNext()){
                    res += sc.nextLine();
                }
                // pour convertir le xml obtenu en json
                JSONParser parse = new JSONParser();
                JSONObject json = (JSONObject) parse.parse(res);
               
                String remoteId = json.get("userid").toString();
                
                return remoteId;
                
            }
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        
        return "undefined";
    }
}
