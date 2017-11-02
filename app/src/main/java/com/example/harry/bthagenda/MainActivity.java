package com.example.harry.bthagenda;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button exampleButton;
    TextView exampleTextview;
    private String ab = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to button and textview
        exampleButton = (Button) findViewById(R.id.editbutton);

        // Set Button Listener
        exampleButton.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        CreateAlertDialog(exampleTextview);
                    }
                }
        );

    }
    // Create an alert dialog as the edit screen.
    public void CreateAlertDialog( TextView textV){
        // Build an alertDialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("editDialogBox");
        dialog.setMessage("Placeholder todo task");
        dialog.setCancelable(true);

        // The textview we want to set with user input.
        exampleTextview = (TextView) findViewById(R.id.textView1);

        // Edit text setup
        final EditText input = new EditText(this);
        // Specify input type expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        dialog.setView(input);

        // Button setup
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                exampleTextview.setText(input.getText().toString());
                dialog.cancel();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert1 = dialog.create();
        dialog.show();
    }
}
