package minerva.anthony.bquiet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ConversationAdapter extends ArrayAdapter<Conversation> {

    String UID;

    public ConversationAdapter(Context context, List<Conversation> convos, String UID){
        super(context, 0, convos);
        this.UID = UID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Conversation convo = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.conversation, parent, false);
        }
        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvUser = convertView.findViewById(R.id.tvUser);
        tvName.setText(convo.getGroupName());
        String[] receivers = convo.getReceivers().split(" ");
        StringBuilder sB = new StringBuilder();
        sB.append("Users: ");
        MyDatabase myDatabase = new MyDatabase(getContext());
        for(int i = 0; i < receivers.length; i++){
            if(!receivers[i].equals(UID) && myDatabase.getUser(receivers[i]) != null){
                sB.append(myDatabase.getUser(receivers[i]).getName() + " ");
            }
        }
        tvUser.setText(sB.toString());
        return convertView;
    }
}
