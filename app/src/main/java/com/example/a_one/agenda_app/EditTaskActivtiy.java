package com.example.a_one.agenda_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.a_one.agenda_app.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditTaskActivtiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_activtiy);


        final EditText editText = (EditText) findViewById(R.id.editMessage);
        final Spinner editPriority = (Spinner) findViewById(R.id.editPriority);
        Button confirmChange = (Button) findViewById(R.id.editButton);

        FirebaseAuth firebaseAuthentication = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuthentication.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String userId = firebaseUser.getUid();

        final String key  = getIntent().getStringExtra("taskID");

        final DatabaseReference taskToEdit = database.child("users").child(userId).child("taskList").child(key);
        taskToEdit.addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                editText.setText(task.getMessage());
                editPriority.setSelection(getIndex(editPriority, task.getPriority()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("AgendaApp", "getUser:onCancelled", databaseError.toException());
            }
        });

        confirmChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Task editedTask = new Task(key, editText.getText().toString(), editPriority.getSelectedItem().toString());
                taskToEdit.setValue(editedTask);
                finish();
            }
        });



    }

    // Find index of matching drop down content.
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
}
