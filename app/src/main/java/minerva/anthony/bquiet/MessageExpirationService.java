package minerva.anthony.bquiet;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class MessageExpirationService extends IntentService {
    public MessageExpirationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MyDatabase mDatabase = new MyDatabase(getApplicationContext());
        long expireTime = intent!=null?intent.getLongExtra("EXPIRE_TIME", -1):-1;
        String CID = intent!=null?intent.getStringExtra("CID"):"";
        if(expireTime != -1){
            mDatabase.expireMessages(CID, System.currentTimeMillis(), expireTime);
        }
    }
}
