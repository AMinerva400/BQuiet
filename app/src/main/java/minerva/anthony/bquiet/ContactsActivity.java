package minerva.anthony.bquiet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class ContactsActivity extends AppCompatActivity
        implements UserDataFragment.UserDataListener {

    private static User user;
    private final static int QRcodeWidth = 500;
    Bitmap bitmap;
    ImageView ivQR;
    Button btnAddContact;
    private MyDatabase mDatabase;

    @Override
    public void setUserData(User user) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserData", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("UID", user.getUserID());
        editor.putString("NAME", user.getName());
        editor.putString("KEY", user.getPublicKey());
        this.user = user;
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setUID(getApplicationContext(), getSupportFragmentManager());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        ivQR = findViewById(R.id.ivQR);
        btnAddContact = findViewById(R.id.btnAddContact);
        mDatabase = new MyDatabase(getApplicationContext());
        try{
            Gson gson = new Gson();
            bitmap = TextToImageEncode(gson.toJson(user));
        } catch(WriterException e){
            Log.e("ContactsQRCode", "Error Generating QR Code");
        }
        ivQR.setImageBitmap(bitmap);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), QRCodeScanner.class), 0);
            }
        });
    }
    //Handle QR Code Scanned!
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Gson gson = new Gson();
                User u = gson.fromJson(data.getStringExtra("QRCode"), User.class);
                mDatabase.addContact(u);
            }
            if(resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Unable to Scan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Sets the UID
    static void setUID(Context c, FragmentManager fm){
        SharedPreferences sharedPref = c.getSharedPreferences("UserData", 0);
        String UID = sharedPref.getString("UID", "ERROR");
        String name = sharedPref.getString("NAME", "ERROR");
        String pubKey = sharedPref.getString("KEY", "ERROR");
        if(UID.equals("ERROR") || name.equals("ERROR") || pubKey.equals("ERROR")){ // There is no User Account
            UserDataFragment userData = UserDataFragment.newInstance();
            userData.show(fm, "fragment_user_data");
        }else{
            user = new User();
            user.UserID = UID;
            user.name = name;
            user.pubKey = pubKey;
        }
    }
    //Convert Text to QR Image
    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
