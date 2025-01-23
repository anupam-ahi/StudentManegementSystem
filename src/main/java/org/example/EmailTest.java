package org.example;
import jakarta.mail.*;
import jakarta.mail.search.BodyTerm;
import jakarta.mail.search.SearchTerm;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class EmailTest {
    static String user;
    static String password;
    public EmailTest(String user, String password){
        EmailTest.user = user;
        EmailTest.password = password;
    }
    static Store establishConnection() throws MessagingException{
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", user, password);
        return store;
    }
    static double emailCount(Store store, String folderName) throws MessagingException{
        Folder inbox = store.getFolder(folderName);
        inbox.open(Folder.READ_WRITE);
        inbox.close(true);
        return inbox.getMessageCount();
    }
    static void readEmail(Store store) throws MessagingException, IOException {
        Folder inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_WRITE);
        Message[] messages = inbox.getMessages();
        if(messages.length > 0){
            Message message = messages[0];
        }
        inbox.close(true);
    }
    static void searchEmails(Store store, String keyword, String destinationFolder) throws MessagingException, IOException{
        Folder inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_WRITE);
        SearchTerm emailTerm = new BodyTerm(keyword);
        Message[] messages = inbox.search(emailTerm);
        Arrays.sort(messages, (m1, m2) -> {
            try{
                return m2.getSentDate().compareTo(m1.getSentDate());
            }catch(MessagingException e){
                throw new RuntimeException(e);
            }
        });
        Message[] getFirstFiveEmails = Arrays.copyOfRange(messages, 0, 10);
        for(Message message: getFirstFiveEmails){
            moveToFolder(store, message, destinationFolder);
        }
        inbox.close(true);
    }
    static void moveToFolder(Store store, Message message, String folderName) throws MessagingException{
        Folder destinationFolder = store.getFolder(folderName);
        if(!destinationFolder.exists()) destinationFolder.create(Folder.HOLDS_MESSAGES);
        Message[] messagesToMove = new Message[] {message};
        message.getFolder().copyMessages(messagesToMove, destinationFolder);
        message.setFlag(Flags.Flag.DELETED, true);
    }
    static void deleteEmail(Store store, String folderName) throws MessagingException{
        Folder folder = store.getFolder(folderName);
        if(!folder.exists()){
            System.out.println("Folder does not exist: " + folderName);
            return;
        }
        folder.open(Folder.READ_WRITE);
        Message[] messages = folder.getMessages();
        for(Message message: messages) message.setFlag(Flags.Flag.DELETED, true);
        folder.close(true);
    }
    public static void main(String[] args) throws MessagingException {
        Store store = establishConnection();
//        emailCount(store);
        long startTime = System.currentTimeMillis();
        try{
//            readEmail(store);
            searchEmails(store, "Unfortunately we will not be moving", "Rejections");
            deleteEmail(store, "Rejections");
            searchEmails(store, "We have received your application", "Applications");
            searchEmails(store, "Next Step", "Success");
        } catch (IOException e){
            System.out.println(e);
        }
        long endTime = System.currentTimeMillis();
        long execTime = endTime - startTime;
        System.out.println("Time taken for sorting the first 10 emails of applications and rejections is: " + execTime);
    }
}
