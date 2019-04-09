package minerva.anthony.bquiet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, List<User> users){
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User u = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user, parent, false);
        }
        TextView tvName = convertView.findViewById(R.id.tvName);
        tvName.setText(u.getName());
        return convertView;
    }
}
