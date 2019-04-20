package minerva.anthony.bquiet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mongodb.Block;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InboxService extends Service {

    String userID;
    StitchAppClient stitchClient;
    RemoteMongoClient mongo;
    RemoteMongoCollection<Document> BQcollection;
    RemoteFindIterable<Document> messages;
    MyDatabase myDatabase;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userID = intent.getStringExtra("UID");
        myDatabase = new MyDatabase(getApplicationContext());
        Stitch.initializeDefaultAppClient("bquiet-pggdj");
        stitchClient = Stitch.getDefaultAppClient();
        stitchClient.getAuth().loginWithCredential(new AnonymousCredential()).addOnSuccessListener(new OnSuccessListener<StitchUser>() {
            @Override
            public void onSuccess(StitchUser stitchUser) {
                Log.v("STITCH", "Login Successful");
            }
        });
        Security sec = new Security();
        Log.v("STITCH", "TESTING: " + sec.decrypt(sec.encrypt("HELLO ANTHONY")));
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                Document filterDoc = new Document().append("sendTo", userID);
                mongo = stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
                BQcollection = mongo.getDatabase("UserMessages").getCollection("Message");
                //messages = BQcollection.find(filterDoc);
                Log.v("STITCH", "Running Inbox Loop");
                RemoteFindIterable findResults = BQcollection.find(filterDoc);
                findResults.forEach(item -> {
                    Document doc = (Document) item;
                    Log.v("STITCH", "Message Received!");
                    Gson gson = new Gson();
                    Security decrypter = new Security();
                    ArrayList cUsers = gson.fromJson(decrypter.decrypt(doc.get("convoUsers").toString()), ArrayList.class);
                    Log.v("STITCH", cUsers.toString());
                    for(Object obj : cUsers){
                        User u = gson.fromJson(obj.toString(), User.class);
                        if(!u.getUserID().equals(userID)){
                            Log.v("STITCH", "User Found! " + u.getName());
                            myDatabase.addContact(u);
                        }
                    }
                    Log.v("STITCH", "Reached this line");
                    Conversation c = gson.fromJson(decrypter.decrypt(doc.get("convo").toString()), Conversation.class);
                    Log.v("STITCH", "Convo Found! " + c.getGroupName());
                    myDatabase.addConversation(c);
                    Message m = gson.fromJson(decrypter.decrypt(doc.get("message").toString()), Message.class);
                    Log.v("STITCH", "Convo Found! " + m.getMessage());
                    Log.v("STITCH", "Reached this line");
                    myDatabase.addMessage(m);
                    if(m.getSender().UserID.equals("ALERT_EXPIRE")){
                        //TODO: Create/Start a Service to Constantly Call This, Possibly Use SharedPreferences
                        myDatabase.expireMessages(System.currentTimeMillis(), (long)doc.get("expireTime"));
                    }
                    //TODO: Notification to User
                });
                BQcollection.deleteMany(filterDoc);

                /*messages.forEach(new Block<Document>() {
                    @Override
                    public void apply(Document item) {
                        Log.v("STITCH", "Message Received!");
                        ArrayList<User> cUsers = (ArrayList<User>) item.get("convoUsers");
                        if(cUsers != null){
                            for(User u : cUsers){
                                if(!u.getUserID().equals(userID)){
                                    myDatabase.addContact(u);
                                }
                            }
                        }
                        myDatabase.addConversation((Conversation) item.get("convo"));
                        myDatabase.addMessage((Message) item.get("message"));
                        //TODO: Notification to User!
                        // delete the message received
                        BQcollection.deleteOne(item);
                    }
                });*/
            }
        };
        //Starts after 5 sec and will repeat on every 5 sec of time interval.
        (new Timer()).schedule(doAsynchronousTask, 5000,5000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
