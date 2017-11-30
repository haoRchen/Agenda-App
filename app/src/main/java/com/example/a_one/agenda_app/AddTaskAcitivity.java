package com.example.a_one.agenda_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.a_one.agenda_app.models.Task;
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

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String key = database.getReference("taskList").push().getKey();


        String getTaskInput;

        final EditText editText = (EditText) findViewById(R.id.messageInput);

        final Spinner spinner = (Spinner) findViewById(R.id.priority_options);

        Button submitButton = (Button) findViewById(R.id.submit_task);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Task task = new Task();

                task.setMessage(editText.getText().toString());

                String text = spinner.getSelectedItem().toString();

                int priority_rating = Integer.parseInt(text);

                task.setPriority(priority_rating);

                Map<String, Object> childUpdates = new HashMap<>();

                childUpdates.put( key, task.toFirebaseObject());

                database.getReference("todoList").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            finish();
                        }
                    }
                });
            }
        });




    }
}
