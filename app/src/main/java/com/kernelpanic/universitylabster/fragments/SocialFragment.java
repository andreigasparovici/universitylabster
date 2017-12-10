package com.kernelpanic.universitylabster.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andrei on 09.12.2017.
 */

public class SocialFragment extends Fragment {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

    @BindView(R.id.userList)
    ListView userList;

    List<String> users = new ArrayList<>();
    List<String> userContact = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_fragment, container, false);
        ButterKnife.bind(this, view);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new MaterialDialog.Builder(SocialFragment.this.getContext())
                    .title(users.get(i))
                    .content(userContact.get(i))
                    .positiveText("OK")
                    .show();
            }
        });

        reference.child("year").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String year = dataSnapshot.getValue(String.class);

                FirebaseDatabase.getInstance().getReference("users").orderByChild("year").equalTo(year)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            users = new ArrayList<>();
                            userContact = new ArrayList<>();
                            for(DataSnapshot shot: dataSnapshot.getChildren()) {
                                if(!shot.getKey().equals(user.getUid())
                                        && (shot.child("name").getValue(String.class) != null) &&
                                        (user.getDisplayName() != null) &&
                                        !shot.child("name").getValue(String.class).equals(user.getDisplayName())) {
                                    users.add(shot.child("name").getValue(String.class));
                                    userContact.add(shot.child("contact").getValue(String.class));
                                }
                            }
                            ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    users.toArray(new String[users.size()]));
                            userList.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {  }
                    });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {  }
        });

        return view;
    }

}
