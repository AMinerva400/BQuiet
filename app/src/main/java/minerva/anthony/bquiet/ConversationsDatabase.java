package minerva.anthony.bquiet;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities={Conversation.class}, version=4, exportSchema = false)
public abstract class ConversationsDatabase extends RoomDatabase {

    public abstract ConversationDao conversationDao();

}
