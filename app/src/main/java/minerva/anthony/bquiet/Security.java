package minerva.anthony.bquiet;

import android.util.Log;

import java.security.Key;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

 public class Security {
     private static SecretKey secretKey;
     private static Cipher ecipher;
     private static Cipher dcipher;

     protected String encryptionType = "AES/ECB/PKCS5Padding";

     public Security() {
         try{
            ecipher = Cipher.getInstance(encryptionType);
            dcipher = Cipher.getInstance(encryptionType);
            byte[] encodedKey = Base64.getDecoder().decode("4xK23JJqwAyyA36M0pdiBw==");
            secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
         }catch(Exception exc){
             Log.e("STITCH", exc.toString());
         }
     }

     public String encrypt(String plainField) {
         try{
             ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
             byte[] encryptedField = ecipher.doFinal(plainField.getBytes());
             return Base64.getEncoder().encodeToString(encryptedField);
         }
         catch(Exception e){
             Log.e("STITCH", e.toString());
             return plainField;
         }
     }

     public String decrypt(String encryptedField) {
         try
         {
             dcipher.init(Cipher.DECRYPT_MODE, secretKey);
             byte[] plainField = dcipher.doFinal(Base64.getDecoder().decode(encryptedField));
             return new String(plainField);
         }
         catch(Exception e){
             Log.e("STITCH", "DECRYPT FAILED: " + e.toString());
             return encryptedField;
         }
     }
 }
