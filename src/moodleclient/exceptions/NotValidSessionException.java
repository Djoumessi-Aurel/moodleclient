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
public class NotValidSessionException extends Exception {
    
    public NotValidSessionException(String message){
        super(message);
    }
}
