package minerva.anthony.bquiet;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities={User.class}, version=1, exportSchema = false)
public abstract class ContactsDatabase extends RoomDatabase {

    public abstract UserDao userDao();

}
