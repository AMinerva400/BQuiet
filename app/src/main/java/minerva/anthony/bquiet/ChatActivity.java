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
        editor.putString("KEY", user.getPublicKey());
        this.user = user;
        editor.apply();
    }

    Button btnSend, btnSettings;
    EditText etChat;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    List<Message> dataSet;
    private static User user;
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
        mMessageAdapter = new MessageListAdapter(this, dataSet, user.UserID);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setStackFromEnd(true); //Focus on Bottom of Stack
        mMessageRecycler.setLayoutManager(llm);
        mMessageRecycler.setAdapter(mMessageAdapter);
        btnSend = findViewById(R.id.btnSend);
        etChat = findViewById(R.id.etChatbox);
        setTitle(mDatabase.getConversation(CID).getGroupName());
        MessageSender mSender = new MessageSender(mDatabase, user.name);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message m = new Message(etChat.getText().toString(), user, System.currentTimeMillis(), CID);
                mDatabase.addMessage(m);
                dataSet.add(m);
                mMessageAdapter.notifyDataSetChanged();
                for(String sendTo : receiverID){
                    User u = mDatabase.getUser(sendTo);
                    if(!sendTo.equals(user.UserID)){
                        mSender.send(m, u);
                    }
                }
                etChat.setText("");
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
