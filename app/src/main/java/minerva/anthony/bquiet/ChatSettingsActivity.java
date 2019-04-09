package minerva.anthony.bquiet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ChatSettingsActivity extends AppCompatActivity
        implements  UserDataFragment.UserDataListener {

    private static String UID, Name;

    @Override
    public void setUserData(User user) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserData", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("UID", user.getUserID());
        editor.putString("NAME", user.getName());
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        setUID(getApplicationContext(), getSupportFragmentManager());
    }

    //Sets the UID
    static void setUID(Context c, FragmentManager fm){
        SharedPreferences sharedPref = c.getSharedPreferences("UserData", 0);
        UID = sharedPref.getString("UID", "ERROR");
        Name = sharedPref.getString("NAME", "ERROR");
        if(UID.equals("ERROR") || Name.equals("ERROR")){ // There is no User Account
            UserDataFragment userData = UserDataFragment.newInstance();
            userData.show(fm, "fragment_user_data");
        }
    }
}
