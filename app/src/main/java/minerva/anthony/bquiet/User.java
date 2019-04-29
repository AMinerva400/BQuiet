package minerva.anthony.bquiet;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    @ColumnInfo
    String name;
    @ColumnInfo @PrimaryKey @NonNull
    String UserID;
    String pubKey;

    public void setName(String name) {
        this.name = name;
    }

    public void setUserID(@NonNull String userID) {
        UserID = userID;
    }

    public User(){
    }

    public User(String n){
        name = n;
        UserID = UUID.randomUUID().toString();
        pubKey = Security.keyGenerator();
    }

    public User(String n, String UID){
        name = n;
        UserID = UID;
    }

    String getName(){
        return name;
    }

    String getUserID(){
        return UserID;
    }

    String getPublicKey() { return pubKey; }
}
