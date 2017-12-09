package com.kernelpanic.universitylabster.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kernelpanic.universitylabster.CoursesActivity;
import com.kernelpanic.universitylabster.R;
import com.kernelpanic.universitylabster.WeekAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andrei on 08.12.2017.
 */

public class WeekFragment extends Fragment {

    @BindView(R.id.courseList)
    ListView courseList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

//    DatabaseReference reference = database.getReference("courses");

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.week_fragment, container, false);

        ButterKnife.bind(this, root);

        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter,View v, int position, long x){
                String item = adapter.getItemAtPosition(position).toString();

                Intent intent = new Intent(WeekFragment.this.getActivity(), CoursesActivity.class);
                Bundle b = new Bundle();
                b.putString("day", item);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        final WeekAdapter adapter = new WeekAdapter(WeekFragment.this.getContext());
        courseList.setAdapter(adapter);


        return root;
    }

}
