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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import moodleclient.exceptions.ServerUnreachableException;

/**
 *
 * @author DELL
 */
public class Downloader {
    
    //Télécharge un fichier. Le dossier de destination est le dossier files
    public static void downloadFile(String url, String name){ //url = url du fichier distant; name = nom du fichier en local
        
        try {
            URL fileURL = new URL(url);
                        
            InputStream in = fileURL.openStream();

            FileOutputStream fos = new FileOutputStream("./files/" + name);

            int length = -1;

            byte[] buffer = new byte[1024];// buffer for portion of data from connection

            while ((length = in.read(buffer)) > -1) {
                fos.write(buffer, 0, length);
            }

            fos.close();
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //Une autre version de la fonction de téléchargement
    public static void downloadFile2(String url, String name) throws ServerUnreachableException{
            
        try{
            URL fileURL = new URL(url);

            HttpURLConnection con = (HttpURLConnection) fileURL.openConnection();

            con.setRequestMethod("GET");
            con.connect();

            int status = con.getResponseCode();

            if(status == 200){
                //the server is reachable
                //get the request response
                InputStream in = fileURL.openStream();

                BufferedInputStream bis = new BufferedInputStream(in);
                FileOutputStream fos = new FileOutputStream("./files/" + name);

                byte[] data = new byte[1024];
                int count;

                while ((count = bis.read(data, 0, 1024)) != -1) {
                    fos.write(data, 0, count);
                }
                
                fos.close();
                in.close();
            }else{
                //the server is not reachable
                //***************************
                throw new ServerUnreachableException("Server unreachable");
            }
	} catch (IOException ex) {
		Logger.getLogger(Downloader.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    
    
}
