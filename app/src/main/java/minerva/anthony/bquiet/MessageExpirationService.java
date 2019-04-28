package minerva.anthony.bquiet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MessageExpirationService extends Service {

    long expireTime;
    String CID = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("EXPIRE", "MESSAGE_EXPIRATION STARTING");
        if(intent != null){
            expireTime = intent.getLongExtra("EXPIRE_TIME", -1);
            CID = intent.getStringExtra("CID");
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyDatabase mDatabase = new MyDatabase(getApplicationContext());
        mDatabase.expireMessages(CID, System.currentTimeMillis(), expireTime);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
