package minerva.anthony.bquiet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class InboxService extends Service {

    String userID;
    StitchAppClient stitchClient;
    RemoteMongoClient mongo;
    RemoteMongoCollection<Document> BQcollection;
    MyDatabase myDatabase;
    private static final String CHANNEL_ID = "BQUIET_CHANNEL ";

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
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                Intent i = new Intent(getApplicationContext(), MessageExpirationService.class);
                                i.putExtra("EXPIRE_TIME", (long)doc.get("expireTime"));
                                i.putExtra("CID", m.conversationID);
                                startService(i);
                                Log.v("STITCH", "Starting Expire Service");
                            }
                        };
                        (new Timer()).schedule(task, 5000, 20000);
                    }
                    createNotificationChannel();
                    Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                    i.putExtra("rID", c.getReceivers());
                    i.putExtra("CID", c.getCID());
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setContentTitle("Message Received!")
                            .setContentText(m.getSender().getName() + ": " + m.getMessage())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.notify((new Random()).nextInt(), builder.build());
                    //https://developer.android.com/training/notify-user/build-notification
                });
            }
        };
        //Starts after 5 sec and will repeat on every 5 sec of time interval.
        (new Timer()).schedule(doAsynchronousTask, 5000,5000);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "BQuiet";
            String description = "BQuiet Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
