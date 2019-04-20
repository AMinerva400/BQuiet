package minerva.anthony.bquiet;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class ConversationFragment extends DialogFragment {

    private static List<User> contacts = new ArrayList<>();
    private static User user;
    private ListView lvUsers;
    ArrayList<User> users = new ArrayList<>();

    public interface ConversationListener {
        void addConversation(Conversation c);
    }

    public ConversationFragment() {
        // Required empty public constructor
    }

    public static ConversationFragment newInstance(List<User> c, User thisUser) {
        contacts = c;
        user = thisUser;
        ConversationFragment fragment = new ConversationFragment();
        Bundle args = new Bundle();
        args.putString("TITLE", "Create a Conversation");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getArguments().getString("TITLE");
        getDialog().setTitle(title);
        final EditText etName = getView().findViewById(R.id.etName);
        etName.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        //TODO: Populate Spinner from contacts
        //UserAdapter uAdapter = new UserAdapter(getContext(), contacts);
        //uAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lvUsers = view.findViewById(R.id.lvUsers);
        List<ListViewItem> initItemList = this.getInitListViewItems();
        UserAdapter cbAdapter = new UserAdapter(getContext(), initItemList);
        //cbAdapter.notifyDataSetChanged();
        lvUsers.setAdapter(cbAdapter);
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long l) {
                ListViewItem item = (ListViewItem)adapterView.getAdapter().getItem(itemIndex);
                CheckBox itemCheckbox = view.findViewById(R.id.list_view_item_checkbox);
                if(item.isChecked())
                {
                    itemCheckbox.setChecked(false);
                    item.setChecked(false);
                    users.remove(item.getUser());
                }else
                {
                    itemCheckbox.setChecked(true);
                    item.setChecked(true);
                    users.add(item.getUser());
                }
            }
        });

        Button btnSave = getView().findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationListener mListener = (ConversationListener) getActivity();
                users.add(user);
                Conversation c = new Conversation(etName.getText().toString(), users);
                mListener.addConversation(c);
                dismiss();
            }
        });
    }

    private List<ListViewItem> getInitListViewItems(){
        List<ListViewItem> items = new ArrayList<>();
        for(User u : contacts){
            ListViewItem item = new ListViewItem();
            item.setChecked(false);
            item.setUser(u);
            item.setItemText(u.name);
            items.add(item);
        }
        return items;
    }
}
