/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileOutputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;

/**
 *
 * @author Makue212
 */
public class DatabaseConfig {

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */

    /**
     * @return the username
     */
    public String getUsername() {
        return decrypt(username);
    }
    /**
     * @param username the username to set
     */
    /**
     * @return the encryptedPassword
     */
    public String getEncryptedPassword() {
        return decrypt(encryptedPassword);
    }

    /**
     * @param encryptedPassword the encryptedPassword to set
     */

    private String url;
    private String username;
    private String encryptedPassword;
    private String key;
    private CustomJOptionPane err = new CustomJOptionPane();
     private static final String initVector = "RandomInitVector"; // 16 bytes IV
    
    public DatabaseConfig(){
        loadEncryptionKey();
        try (FileInputStream fis = new FileInputStream("updates/config.properties")){
            Properties prop = new Properties();
            prop.load(fis);
            //key = prop.getProperty("encryption.key");
            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            encryptedPassword = prop.getProperty("db.password");
            
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //err.showErorrMessageDialog(null,"Fatal Error ReInitialise Please Or Contact Service","Power Control");
            } catch (InterruptedException ex) {
                Logger.getLogger(DatabaseConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void loadEncryptionKey(){
        try(FileInputStream gen = new FileInputStream("encrypt.key")) {
            if(gen==null){
                System.out.println("file not found");
            }else{
                Properties prop = new Properties();
                prop.load(gen);
                key = prop.getProperty("encryption.key");
            }
        } catch (IOException e) {
                        e.printStackTrace();
            try {
                //err.showErorrMessageDialog(null,e+" Fatal Error Wrong Key","Power Control");
            } catch (InterruptedException ex) {
                Logger.getLogger(DatabaseConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
         public String encrypt(String strToEncrypt) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Altered Access Key encryption Failed ","Power Security",JOptionPane.OK_OPTION);
            /* e.printStackTrace(); // Handle exceptions appropriately */
        }
        return null;
    }
         
     
       private String decrypt(String strToDecrypt) {
        try (FileInputStream fis = new FileInputStream("updates/config.properties")) {
            Properties prop = new Properties();
            prop.load(fis);
            String status = prop.getProperty("encrypted");

            if (status.contains("false")) {
                System.out.println("Not Encrypted");
                return strToDecrypt;
            } else {
                SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

                cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
                byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
                return new String(decrypted, "UTF-8");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Altered Access File Decryption Failed ","Power Security",JOptionPane.OK_OPTION); // Handle exceptions appropriately
        }
        return null;
    }
}
