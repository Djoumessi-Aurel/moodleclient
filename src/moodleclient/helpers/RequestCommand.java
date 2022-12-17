/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author pepi
 */
public class RequestCommand {
    public String command;
    public String response="";
    
    public RequestCommand(String _command){
        this.command = _command;
    }
    
    public String runCommand(){
        
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command("cmd.exe", "/c", " "+this.command);

        try{
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while((line = reader.readLine()) != null){
                output.append(line + "\n");
                this.response += line + "\n";
            }

            int exitval = process.waitFor();

            if(exitval == 0){

            }else{

            }

        }catch(IOException e){
            e.printStackTrace();

        }catch(InterruptedException e){
            e.printStackTrace();

        }
        
        return this.response;
    }
}
