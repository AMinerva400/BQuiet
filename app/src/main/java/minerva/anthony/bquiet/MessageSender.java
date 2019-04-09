package minerva.anthony.bquiet;

import android.util.Log;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.conversions.Bson;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class MessageSender {

    StitchAppClient stitchClient;
    RemoteMongoClient mongo;
    RemoteMongoCollection<Document> BQcollection;
    MyDatabase myDatabase;
    String senderName;


    public MessageSender(MyDatabase myDatabase, String senderName) {
        stitchClient = Stitch.getDefaultAppClient();
        stitchClient.getAuth().loginWithCredential(new AnonymousCredential()).addOnSuccessListener(new OnSuccessListener<StitchUser>() {
            @Override
            public void onSuccess(StitchUser stitchUser) {
                Log.v("STITCH", "Login Successful");
            }
        });
        this.myDatabase = myDatabase;
        this.senderName = senderName;
    }

    public void send(Message message, User receiver) {
        mongo = stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        BQcollection = mongo.getDatabase("UserMessages").getCollection("Message");
        Conversation c = myDatabase.getConversation(message.conversationID);
        Gson gson = new Gson();
        ArrayList<String> convoUsers = new ArrayList<>();
        for(String u : c.getReceivers().split(" ")){
            User user;
            if(myDatabase.getUser(u) == null){
                user = new User(senderName, u);
            }else {
                user = myDatabase.getUser(u);
            }
            convoUsers.add(gson.toJson(user));
        }
        Security encrypter = new Security();
        Message newMsg = message;
        newMsg.setMessage(encrypter.encrypt(newMsg.getMessage()));
        Document msg = new Document("sendTo", receiver.getUserID())
                .append("convoUsers", gson.toJson(convoUsers))
                .append("convo", gson.toJson(c))
                .append("message", gson.toJson(newMsg));
        Log.v("STITCH", "Message Sent");
        Task<RemoteInsertOneResult> insertTask = BQcollection.insertOne(msg);
        insertTask.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
            @Override
            public void onComplete(@NonNull Task<RemoteInsertOneResult> task) {
                if(task.isSuccessful()){
                    Log.v("STITCH", "Insert Successful");
                }else{
                    Log.v("STITCH", "Failed to insert: " + task.getException());
                }
            }
        });
    }
}
