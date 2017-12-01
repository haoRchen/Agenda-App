package com.example.a_one.agenda_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.a_one.agenda_app.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddTaskAcitivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task_acitivity);

        // Grab database references
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserID = currentUser.getUid();
        // writing task endpoint each time will overwrite previous values(if exist)
        // We need the unique key for our task so it can be pushed to that key only.
        final String key = database.child("users").child(currentUserID).child("taskList").push().getKey();

        //Grab user input field
        final EditText editText = (EditText) findViewById(R.id.messageInput);
        final Spinner spinner = (Spinner) findViewById(R.id.priority_options);
        Button submitButton = (Button) findViewById(R.id.submit_task);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Populate the model task with user input.
                Task task = new Task();
                task.setMessage(editText.getText().toString());
                String rating = spinner.getSelectedItem().toString();
                task.setPriority(rating);

                // we need to convert our model into a Hashmap since Firebase cannot save custom classes.
                // String/ArrayList/Integer and Hashmap are the only supported types.
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put( key, task.toFirebaseObject());

                database.child("users").child(currentUserID).child("taskList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            // Return to previous activity
                            finish();
                        }
                    }
                });
            }
        });




    }
}
