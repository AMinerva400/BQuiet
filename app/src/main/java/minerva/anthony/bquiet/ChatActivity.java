package minerva.anthony.bquiet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity
        implements UserDataFragment.UserDataListener{

    @Override
    public void setUserData(User user) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserData", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("UID", user.getUserID());
        editor.putString("NAME", user.getName());
        editor.commit();
    }

    Button btnSend, btnSettings;
    EditText etChat;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    List<Message> dataSet;
    private static String UID, Name;
    private String[] receiverID;
    private String CID = "";
    private MyDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setUID(getApplicationContext(), getSupportFragmentManager());
        if(getIntent().getExtras() != null){
            receiverID = getIntent().getStringExtra("rID").split(" ");
            CID = getIntent().getStringExtra("CID");
        }
        mMessageRecycler = findViewById(R.id.rvMessageList);
        mDatabase = new MyDatabase(getApplicationContext());
        dataSet = mDatabase.getMessages(CID);
        mMessageAdapter = new MessageListAdapter(this, dataSet, UID);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setStackFromEnd(true); //Focus on Bottom of Stack
        mMessageRecycler.setLayoutManager(llm);
        mMessageRecycler.setAdapter(mMessageAdapter);
        btnSend = findViewById(R.id.btnSend);
        etChat = findViewById(R.id.etChatbox);
        MessageSender mSender = new MessageSender(mDatabase, Name);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message m = new Message(etChat.getText().toString(), new User(Name, UID), System.currentTimeMillis(), CID);
                mDatabase.addMessage(m);
                dataSet.add(m);
                mMessageAdapter.notifyDataSetChanged();
                for(String user : receiverID){
                    User u = mDatabase.getUser(user);
                    if(!user.equals(UID)){
                        mSender.send(m, u);
                    }
                }
            }
        });
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChatSettingsActivity.class);
                i.putExtra("CID", CID);
                startActivity(i);
            }
        });
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
