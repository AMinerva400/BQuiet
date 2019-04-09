package minerva.anthony.bquiet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageReceiver {
    String userID;
    Context myContext;

    public MessageReceiver(String UID, Context c) {
        // ** Init, start connected and retrieves database
        this.userID = UID;
        this.myContext = c;
    }

    public void checkInbox() {
        // loop that continues while app is running
        //TODO: Place this in background task
        Intent i = new Intent(myContext, InboxService.class);
        i.putExtra("UID", this.userID);
        myContext.startService(i);
    }
}
