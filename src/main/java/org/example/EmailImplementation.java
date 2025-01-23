package org.example;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class EmailImplementation {
    public static void main(String[] args) {
        Properties props = new Properties();
        try(InputStream input = EmailImplementation.class.getClassLoader().getResourceAsStream("config.properties")){
            if(input != null){
                props.load(input);
            }
            else{
                System.out.println("Unable to load config.properties");
                return;
            }
        }catch(IOException e){
            e.printStackTrace();
            return;
        }
        String username = props.getProperty("username");
        String appPassword = props.getProperty("appPassword");
        EmailTest obj = new EmailTest(username, appPassword);
        try{
            Store store = obj.establishConnection();
            double emailCount = obj.emailCount(store, "Applications");
            System.out.println(emailCount);
        }catch(MessagingException e){
            System.out.println(e.getMessage());
        }




    }
}
