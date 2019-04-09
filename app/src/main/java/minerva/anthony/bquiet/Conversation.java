package minerva.anthony.bquiet;

import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity
public class Conversation {

    @ColumnInfo @PrimaryKey @NonNull
    String CID;
    @ColumnInfo
    String groupName;
    @ColumnInfo
    String receivers;

    public Conversation(){}

    public Conversation(String groupName, ArrayList<User> rs) {
        CID = UUID.randomUUID().toString();
        this.groupName = groupName;
        StringBuilder sBuilder = new StringBuilder();
        for(User r : rs){
            sBuilder.append(r.UserID +" ");
        }
        this.receivers = sBuilder.toString();
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers){ this.receivers = receivers; }

    public void setReceiverArray(ArrayList<User> rs) {
        StringBuilder sBuilder = new StringBuilder();
        for(User r : rs){
            sBuilder.append(r.UserID +" ");
        }
        this.receivers = sBuilder.toString();
    }
}
