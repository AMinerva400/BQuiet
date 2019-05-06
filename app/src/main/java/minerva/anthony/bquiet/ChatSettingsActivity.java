package minerva.anthony.bquiet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import java.util.concurrent.TimeUnit;

public class ChatSettingsActivity extends AppCompatActivity
        implements  UserDataFragment.UserDataListener {

    private static String CID;
    private static User user;
    private Switch screenshotPrevent;
    private Spinner msgExpire;
    private Button btnExpire;
    long expireTime = -1;
    String text;

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
        setContentView(R.layout.activity_chat_settings);
        setUID(getApplicationContext(), getSupportFragmentManager());
        screenshotPrevent = findViewById(R.id.settingsSwitch);
        msgExpire = findViewById(R.id.spnExpire);
        btnExpire = findViewById(R.id.btnExpire);
        if(getIntent().getExtras() != null){
            CID = getIntent().getStringExtra("CID");
        }
        msgExpire.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                text = msgExpire.getSelectedItem().toString();
                if(text.equals("Never Expire")){
                    expireTime = -1;
                }else if(text.equals("After 24 Hours")){
                    expireTime = TimeUnit.HOURS.toMillis(24);
                }else if(text.equals("After 1 Hour")){
                    expireTime = TimeUnit.HOURS.toMillis(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnExpire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabase mDatabase = new MyDatabase(getApplicationContext());
                MessageSender mSender = new MessageSender(mDatabase, "ALERT");
                Conversation convo = mDatabase.getConversation(CID);
                Message m = new Message("New Expiration Status: " + text, new User("ALERT", "ALERT_EXPIRE"), System.currentTimeMillis(), CID);
                for(String user : convo.getReceivers().split(" ")){
                    User u = mDatabase.getUser(user);
                    mSender.sendExpirationAlert(m, u, expireTime);
                }
            }
        });
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
}
