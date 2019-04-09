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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class ConversationFragment extends DialogFragment {

    private static List<User> contacts = new ArrayList<>();
    private static User user;
    private Spinner spinner;

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
        UserAdapter uAdapter = new UserAdapter(getContext(), contacts);
        uAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = getView().findViewById(R.id.spnUsers);
        spinner.setAdapter(uAdapter);
        Button btnSave = getView().findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationListener mListener = (ConversationListener) getActivity();
                //TODO: Create User from View Data
                User u = (User) spinner.getSelectedItem();
                ArrayList<User> users = new ArrayList<>();
                users.add(u);
                users.add(user);
                Conversation c = new Conversation(etName.getText().toString(), users);
                mListener.addConversation(c);
                dismiss();
            }
        });
    }
}
