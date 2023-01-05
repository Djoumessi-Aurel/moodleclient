/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.exceptions;

/**
 *
 * @author pepi
 */
public class ServerUnreachableException extends Exception {
    
    public ServerUnreachableException(String message){
        
        super(message);
    }
}
