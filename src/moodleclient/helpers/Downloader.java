/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DELL
 */
public class Downloader {
    
    //Télécharge un fichier. Le dossier de destination est le dossier files
    public static void downloadFile(String url, String name){ //url = url du fichier distant; name = nom du fichier en local
        
        try {
            URL fileURL = new URL(url);
                        
            InputStream in;
            in = fileURL.openStream();

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
    
}
