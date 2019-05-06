package minerva.anthony.bquiet;

import android.util.Log;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;

public class Security {
    private static Cipher ecipher;
    private static Cipher dcipher;
    private static PrivateKey privKey;

    protected String encryptionType = "RSA";

    public Security() {
        try{
            ecipher = Cipher.getInstance(encryptionType);
            dcipher = Cipher.getInstance(encryptionType);
        }catch(Exception exc){
            Log.e("ERROR", exc.toString());
        }
    }

    public String encrypt(String plainField, String pubKey) {
        try{
            byte[] decodedKey = Base64.getDecoder().decode(pubKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey key = factory.generatePublic(spec);

            ecipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedField = ecipher.doFinal(plainField.getBytes());
            return Base64.getEncoder().encodeToString(encryptedField);
        }
        catch(Exception e){
            Log.e("ERROR", e.toString());
            return plainField;
        }
    }

    public String decrypt(String encryptedField) {
        try
        {
            dcipher.init(Cipher.DECRYPT_MODE, privKey);
            byte[] plainField = dcipher.doFinal(Base64.getDecoder().decode(encryptedField));
            return plainField.toString();
        }
        catch(Exception e){
            Log.e("ERROR", e.toString());
            return encryptedField;
        }
    }

    public static String keyGenerator()
    {
        try
        {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(512);
            KeyPair kpair = kpg.generateKeyPair();
            privKey = kpair.getPrivate();
            return Base64.getEncoder().encodeToString(kpair.getPublic().getEncoded());
        }catch(Exception e){
            Log.e("ERROR", e.toString());
            return null;
        }
    }
}
