package minerva.anthony.bquiet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserAdapter extends BaseAdapter {
    private List<ListViewItem> items = null;

    private Context context = null;

    public UserAdapter(Context c, List<ListViewItem> i){
        this.context = c;
        this.items = i;
    }

    @Override
    public int getCount() {
        return items!=null?items.size():0;
    }

    @Override
    public Object getItem(int itemIndex) {
        return items!=null?items.get(itemIndex):null;
    }

    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    @Override
    public View getView(int itemIndex, View convertView, ViewGroup viewGroup) {
        UserHolder uHolder;
        if(convertView!=null){
            uHolder = (UserHolder) convertView.getTag();
        }else{
            convertView = View.inflate(context, R.layout.checkbox_item, null);
            CheckBox listItemCheckbox = convertView.findViewById(R.id.list_view_item_checkbox);
            TextView listItemText = convertView.findViewById(R.id.list_view_item_text);
            uHolder = new UserHolder(convertView);
            uHolder.setItemCheckbox(listItemCheckbox);
            uHolder.setItemTextView(listItemText);
            convertView.setTag(uHolder);
        }
        ListViewItem item = items.get(itemIndex);
        //uHolder.getItemCheckbox().setPadding(32, 128, 0, 0);
        //uHolder.getItemTextView().setPadding(32, 64, 0, 0);
        uHolder.getItemCheckbox().setChecked(item.isChecked());
        uHolder.getItemTextView().setText(item.getItemText());
        return convertView;
    }
}
