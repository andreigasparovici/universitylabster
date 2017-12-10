package com.kernelpanic.universitylabster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import com.kernelpanic.universitylabster.models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesActivity extends AppCompatActivity {

    class NoteComparator implements Comparator<Message> {
        @Override
        public int compare(Message n1, Message n2) {
            return (int)(n1.time - n2.time) % Integer.MAX_VALUE;
        }
    }

    String courseId;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    EditText editMessage;

    @BindView(R.id.noteList)
    ListView noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editMessage = findViewById(R.id.editText);

        editMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Message message = new Message();
                message.message = editMessage.getText().toString();
                message.user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                message.time = System.currentTimeMillis();

                int randomShit = ThreadLocalRandom.current().nextInt(0, 10000000);

                reference.child(String.valueOf(randomShit)).setValue(message);
                return true;
            }
            return false;
        });

        Bundle b = getIntent().getExtras();

        if(b != null)
            courseId = b.getString("course_id");

        reference = database.getReference("notes").child(courseId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Message> notes = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    notes.add(child.getValue(Message.class));
                }
                Collections.sort(notes, new NoteComparator());

                final MessageAdapter noteAdapter = new MessageAdapter(NotesActivity.this, notes);
                noteList.setAdapter(noteAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
