package com.kernelpanic.universitylabster;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.models.Course;

/**
 * Created by DragosTrett on 08.12.2017.
 */

public class ActionReciver extends BroadcastReceiver {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    public void onReceive(final Context context, Intent intent) {

      //  Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();
        String action=intent.getStringExtra("action");

        final String id = intent.getStringExtra("id");

        if(action.equals("1")){

            firebaseDatabase.getReference("courses").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Course course = dataSnapshot.getValue(Course.class);
                    if(course == null) return;

                    firebaseDatabase.getReference("courses").child(id).child("up").setValue(Integer.valueOf(course.up)+ 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if(action.equals("decline")){
            declineCourse();
        }

        DashboardActivity.cancelNotification(id);

        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void declineCourse(){
        Log.e("course", "denied");
    }

}