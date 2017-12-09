package com.kernelpanic.universitylabster.fragments;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.adapters.MessageAdapter;
import com.kernelpanic.universitylabster.R;
import com.kernelpanic.universitylabster.models.Message;
import com.kernelpanic.universitylabster.viewmodels.DetailsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andrei on 09.12.2017.
 */

public class MaterialsFragment extends Fragment {

    class NoteComparator implements Comparator<Message> {
        @Override
        public int compare(Message n1, Message n2) {
            return (int)(n1.time - n2.time) % Integer.MAX_VALUE;
        }
    }

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notes");
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @BindView(R.id.userMessage)
    EditText userMessage;

    @BindView(R.id.noteList)
    ListView noteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.material_fragment, container, false);

        ButterKnife.bind(this, view);

        userMessage.setEnabled(false);

        if(DetailsViewModel.course.up >= 5)
            userMessage.setEnabled(true);

        userMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                Map<String, Object> message = new HashMap<>();
                message.put("time", System.currentTimeMillis());
                message.put("user", user.getDisplayName());
                message.put("message", userMessage.getText().toString());

                String id = String.valueOf(ThreadLocalRandom.current().nextInt(0, 2000000000));
                databaseReference.child(String.valueOf(DetailsViewModel.courseId)).child(id).setValue(message);
                userMessage.setText("");

                return true;
            }
            return false;
        });

        databaseReference.child(String.valueOf(DetailsViewModel.courseId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Message> notes = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    notes.add(child.getValue(Message.class));
                }
                Collections.sort(notes, new NoteComparator());
                Collections.reverse(notes);

                final MessageAdapter noteAdapter = new MessageAdapter(MaterialsFragment.this.getActivity(), notes);
                noteList.setAdapter(noteAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
    /*
    reference = database.getReference("notes").child(courseId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Note> notes = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    notes.add(child.getValue(Note.class));
                }
                Collections.sort(notes, new NoteComparator());

                final NoteAdapter noteAdapter = new NoteAdapter(NotesActivity.this, notes);
                noteList.setAdapter(noteAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
     */
}
