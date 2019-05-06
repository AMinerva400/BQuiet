package minerva.anthony.bquiet;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User u);

    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    @Query("SELECT * FROM User WHERE User.UserID = :UID")
    User getUser(String UID);

    @Query("DELETE FROM User WHERE User.UserID = :CODE OR User.name = :CODE")
    void cleanUsers(String CODE);
}
