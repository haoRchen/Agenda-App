package com.example.a_one.agenda_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditTaskActivtiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_activtiy);


        TextView editText = (TextView) findViewById(R.id.editMessage);
        Spinner editPriority = (Spinner) findViewById(R.id.editPriority);
        Button confirmChange = (Button) findViewById(R.id.editButton);

        FirebaseAuth firebaseAuthentication = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuthentication.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String userId = firebaseUser.getUid();

        String key  = getIntent().getStringExtra("taskID");

        DatabaseReference task = database.child("users").child(userId).child("taskList").child(key);

        editText.setText((task.child("message").toString()));
        editPriority.setSelection(getIndex(editPriority, task.child("priority").toString()));



        confirmChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Send the user to a new Activity to add a task
                Intent intent = new Intent(getBaseContext(), AddTaskAcitivity.class);
                startActivity(intent);
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
