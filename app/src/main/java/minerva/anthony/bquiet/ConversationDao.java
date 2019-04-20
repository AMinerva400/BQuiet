package minerva.anthony.bquiet;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertConversation(Conversation c);

    @Query("SELECT * FROM Conversation")
    List<Conversation> getAllConversations();

    @Delete
    void deleteConversation(Conversation c);

    @Query("SELECT * FROM Conversation WHERE Conversation.CID = :CID")
    Conversation getConversation(String CID);
}
