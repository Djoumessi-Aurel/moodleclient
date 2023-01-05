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
public class WrongCredentialsException extends Exception {
    
    public WrongCredentialsException(String message){
        
        super(message);
        
    }
    
}
