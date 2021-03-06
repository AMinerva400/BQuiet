package minerva.anthony.bquiet;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities={Message.class}, version=4, exportSchema = false)
public abstract class MessagesDatabase extends RoomDatabase {

    public abstract MessageDao messageDao();

}
