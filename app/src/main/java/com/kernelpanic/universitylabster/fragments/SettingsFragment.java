package com.kernelpanic.universitylabster.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.kernelpanic.universitylabster.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andrei on 08.12.2017.
 */

public class SettingsFragment extends Fragment {

    @BindView(R.id.userName)
    TextView userName;

    @BindView(R.id.userEmail)
    TextView userEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, view);

        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        return view;
    }
}
