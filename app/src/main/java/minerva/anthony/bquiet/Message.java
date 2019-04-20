package minerva.anthony.bquiet;

import java.sql.Date;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {

    @ColumnInfo
    String message;
    @Embedded
    User sender;
    @ColumnInfo
    long createdAt;
    @ColumnInfo
    String conversationID;
    @ColumnInfo @PrimaryKey @NonNull
    String MID;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    @NonNull
    public String getMID() {
        return MID;
    }

    public void setMID(@NonNull String MID) {
        this.MID = MID;
    }

    public Message(){}

    public Message(String m, User s, long cA, String CID){
        message = m;
        sender = s;
        createdAt = cA;
        conversationID = CID;
        MID = UUID.randomUUID().toString();
    }

    String getMessage(){
        return message;
    }

    User getSender(){
        return sender;
    }

    long getCreatedAt(){
        return createdAt;
    }
}
