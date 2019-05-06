package minerva.anthony.bquiet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements UserDataFragment.UserDataListener, ConversationFragment.ConversationListener{

    @Override
    public void setUserData(User u) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserData", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("UID", u.getUserID());
        editor.putString("NAME", u.getName());
        editor.putString("KEY", u.getPublicKey());
        user = u;
        editor.apply();
        startInbox(getApplicationContext());
    }

    @Override
    public void addConversation(Conversation c) {
        mDatabase.addConversation(c);
        dataSet.add(c);
        Log.v("CONVO", "Data Set Changed: " + c.toString());
        conversationAdapter.notifyDataSetChanged();
    }

    private static User user = new User();
    List<Conversation> dataSet = new ArrayList<>();
    ConversationAdapter conversationAdapter;
    ListView lvConversations;
    MyDatabase mDatabase;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //user.UserID = "ERROR";
        //user.name = "ERROR";
        //user.pubKey = "ERROR";
        setUID(getApplicationContext(), getSupportFragmentManager());
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ConversationFragment conversation = ConversationFragment.newInstance(mDatabase.getContacts(), user);
                conversation.show(fm, "fragment_conversation");
            }
        });
        Button contactsListBtn = findViewById(R.id.btnContactsList);
        contactsListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ContactsActivity.class));
            }
        });
        mDatabase = new MyDatabase(getApplicationContext());
        mDatabase.cleanUsers("ERROR");
        dataSet = mDatabase.getConversations();
        conversationAdapter = new ConversationAdapter(this, dataSet, user.UserID);
        lvConversations = findViewById(R.id.lvConversationsList);
        lvConversations.setAdapter(conversationAdapter);
        lvConversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                i.putExtra("rID", dataSet.get(position).getReceivers());
                i.putExtra("CID", dataSet.get(position).getCID());
                startActivity(i);
            }
        });
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                conversationAdapter.notifyDataSetChanged();
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
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
        if (UID == null || UID.equals("ERROR") || name == null || name.equals("ERROR") || pubKey == null || pubKey.equals("ERROR")) { // There is no User Account
            UserDataFragment userData = UserDataFragment.newInstance();
            userData.show(fm, "fragment_user_data");
        } else {
            user.UserID = UID;
            user.name = name;
            user.pubKey = pubKey;
            startInbox(c);
        }
    }

    public static void startInbox(Context c){
        MessageReceiver mReceiver = new MessageReceiver(user.UserID, c);
        mReceiver.checkInbox();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.btnContactsList) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
