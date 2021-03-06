package com.kernelpanic.universitylabster.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.ModifyEventActivity;
import com.kernelpanic.universitylabster.R;
import com.kernelpanic.universitylabster.viewmodels.DetailsViewModel;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andrei on 09.12.2017.
 */

public class DetailsFragment extends Fragment {

    @BindView(R.id.courseName)
    TextView courseName;

    @BindView(R.id.courseTeacher)
    TextView courseTeacher;

    @BindView(R.id.courseLocation)
    TextView courseLocation;

    @BindView(R.id.imageView)
    ImageView imageView;

    @OnClick(R.id.buttonModify)
    void modifyEvent(){
        startActivity(new Intent(DetailsFragment.this.getContext(), ModifyEventActivity.class));
    }

    String location;

    FirebaseUser user;

    @OnClick(R.id.imageView)
    void goToGmaps() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private DetailsViewModel viewModel;

    void populateData() {
        final String[] year = {""};
        final String[] section = {""};
        courseName.setText(String.format("Nume: %s", DetailsViewModel.course.name));
        courseTeacher.setText(String.format("Profesor: %s", DetailsViewModel.course.teacher));
        courseLocation.setText(String.format("Locaţie: %s", DetailsViewModel.course.location));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        Query query = ref.orderByKey().equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> data = (HashMap<String, Object>)dataSnapshot.getValue();
                //year[0] =data.get("year").toString();
                //section[0] =data.get("section").toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String path = "https://maps.googleapis.com/maps/api/staticmap?size=800x800&center=";
        //path += DetailsViewModel.course.location.spAula+Sergiu+Chiriacescu
        ArrayList<String> tokens = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(DetailsViewModel.course.location, ", ;");
        while(stringTokenizer.hasMoreTokens()) {
            tokens.add(stringTokenizer.nextToken());
        }

        String result = tokens.get(0);
        for(int i = 1; i < tokens.size(); i++)
            result = result.concat("+" + tokens.get(i));

        location = result;

        Log.d("DEBUG", path + result);

        Picasso.with(getContext()).load(path + result).into(imageView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.details_fragment, container, false);
        ButterKnife.bind(this, view);

        user = FirebaseAuth.getInstance().getCurrentUser();

        //viewModel = ViewModelProviders.of(getActivity()).get(DetailsViewModel.class);
        populateData();

        return view;
    }

}
