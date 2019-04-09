package minerva.anthony.bquiet;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


public class UserDataFragment extends DialogFragment {

    public interface UserDataListener {
        void setUserData(User user);
    }

    public UserDataFragment() {}

    public static UserDataFragment newInstance() {
        UserDataFragment fragment = new UserDataFragment();
        Bundle args = new Bundle();
        args.putString("TITLE", "Set Up Your User Account!");
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_data, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("TITLE");
        getDialog().setTitle(title);
        final EditText etName = getView().findViewById(R.id.etName);
        etName.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        Button btnSave = getView().findViewById(R.id.btnSaveUser);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataListener mListener = (UserDataListener) getActivity();
                User u = new User(etName.getText().toString());
                mListener.setUserData(u);
                dismiss();
            }
        });
    }
}
