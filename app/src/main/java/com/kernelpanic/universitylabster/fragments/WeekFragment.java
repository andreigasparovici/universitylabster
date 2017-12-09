package com.kernelpanic.universitylabster.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.FirebaseDatabase;
import com.kernelpanic.universitylabster.CoursesActivity;
import com.kernelpanic.universitylabster.R;
import com.kernelpanic.universitylabster.adapters.WeekAdapter;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andrei on 08.12.2017.
 */

public class WeekFragment extends Fragment {

    @BindView(R.id.courseList)
    ListView courseList;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.week_fragment, container, false);

        /*BoomMenuButton bmb = root.findViewById(R.id.bmb);

        String[] days = { "Luni", "Marţi", "Miercuri", "Joi", "Vineri", "Sâmbătă" };

        for (int i = 0; i < 3; i++) {
            HamButton.Builder builder = new HamButton.Builder()
                    .normalText(days[i]);
                   // .subNormalText("Little butter Doesn't fly, either!");
            bmb.addBuilder(builder);
        }*/


        ButterKnife.bind(this, root);

        courseList.setOnItemClickListener((adapter, v, position, x) -> {
            String item = adapter.getItemAtPosition(position).toString();

            Intent intent = new Intent(WeekFragment.this.getActivity(), CoursesActivity.class);
            Bundle b = new Bundle();
            b.putString("day", item);
            intent.putExtras(b);
            startActivity(intent);
        });

        final WeekAdapter adapter = new WeekAdapter(WeekFragment.this.getContext());
        courseList.setAdapter(adapter);

        return root;
    }

}
