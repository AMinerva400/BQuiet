package minerva.anthony.bquiet;

import android.util.Base64;
import android.util.Log;

import java.security.Key;

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
            byte[] encodedKey = Base64.decode("4xK23JJqwAyyA36M0pdiBw==", Base64.DEFAULT);
            secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        }catch(Exception exc){
            Log.e("ERROR", exc.toString());
        }
    }

    public String encrypt(String plainField) {
        try{
            ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedField = ecipher.doFinal(plainField.getBytes());
            return encryptedField.toString();
        }
        catch(Exception e){
            Log.e("ERROR", e.toString());
        }
    }

    public String decrypt(String encryptedField) {
        try{
            dcipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] plainField = dcipher.doFinal(encryptedField.getBytes());
            return plainField.toString();
        }
        catch(Exception e){
            Log.e("ERROR", e.toString());
        }
    }
}
