package minerva.anthony.bquiet;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessage(Message m);

    @Query("SELECT * FROM Message WHERE Message.conversationID = :CID")
    List<Message> getMessages(String CID);

    @Delete
    void deleteMessage(Message m);
}
