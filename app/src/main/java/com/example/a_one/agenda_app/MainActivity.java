package com.example.a_one.agenda_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a_one.agenda_app.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuthentication;
    private FirebaseUser firebaseUser;
    private DatabaseReference database;
    private String userId;

    RecycleAdapter adapter;
    ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navigation drawer begin ***********************************

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation drawer end ***********************************

        // Variable declaration begin ***********************************
        taskList = new ArrayList<>();
        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.task_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        adapter = new RecycleAdapter();
        recyclerView.setAdapter(adapter);

        // Variable declaration End ***********************************


        // Retrieve floating button for adding new task
        FloatingActionButton addNewTodoButton = (FloatingActionButton) findViewById(R.id.addNewToDoButton);
        addNewTodoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Send the user to a new Activity to add a task
                Intent intent = new Intent(getBaseContext(), AddTaskAcitivity.class);
                startActivity(intent);
            }
        });

        // Handling click events for recyclerView items
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                Toast.makeText(MainActivity.this, "Single Click on position        :"+position,
                        Toast.LENGTH_SHORT).show();

                ImageButton deleteButton = (ImageButton)view.findViewById(R.id.closeTask);
                deleteButton.setOnClickListener(new View.OnClickListener() {

                Task taskClicked = taskList.get(position);
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(MainActivity.this, "Single Click on button :"+position,
                                Toast.LENGTH_SHORT).show();
                        DatabaseReference task = database.child("users").child(userId).child("taskList").child(taskClicked.getTask_id());
                        task.removeValue();
                    }
                });
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();
            }

        }));

        // Initialize Firebase Auth and Database Reference
        firebaseAuthentication = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuthentication.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        if (firebaseUser == null) {
            // User is not logged in, launch the Sign In activity
            LoadSignInView();
        } else {
            userId = firebaseUser.getUid();

//            // Use Firebase to populate the list.
            database.child("users").child(userId).child("taskList").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String original) {
                    Toast.makeText(MainActivity.this, "A task has been added in the database!",
                            Toast.LENGTH_SHORT).show();
                    taskList.add(dataSnapshot.getValue(Task.class));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String original) {
//                    TextView tx = (TextView) listView.getItemAtPosition( adapter.getPosition(s));
//                    tx.setText((String)dataSnapshot.child("title").getValue());

                    Toast.makeText(MainActivity.this, "A task has changed in the database!",
                            Toast.LENGTH_SHORT).show();
                    UpdateTaskList();

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                    adapter.remove((String) dataSnapshot.child("title").getValue());
                    Toast.makeText(MainActivity.this, "A task has been removed from the database!",
                            Toast.LENGTH_SHORT).show();
                    UpdateTaskList();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String original) {
                    Toast.makeText(MainActivity.this, "A task has been moved in the database!",
                            Toast.LENGTH_SHORT).show();
                    UpdateTaskList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            firebaseAuthentication.signOut();
            LoadSignInView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.agenda) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Gets called when the user logs out or on app load.
    private void LoadSignInView() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void EditToDoItem(String taskMessage, final ListView listView, final int position) {
        final EditText taskEditText = new EditText(this);
        taskEditText.setText(taskMessage);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("What should I change the task to?")
                .setView(taskEditText)
                .setPositiveButton("Update Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.child("users").child(userId).child("items")
                                .orderByChild("title")
                                .equalTo((String) listView.getItemAtPosition(position))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {
                                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                            firstChild.child("title").getRef().setValue(taskEditText.getText().toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    public void UpdateTaskList(){
        database.child("users").child(userId).child("taskList").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        taskList.clear();

                        Log.w("AgendaApp", "getUser:onCancelled " + dataSnapshot.toString());
                        Log.w("AgendaApp", "count = " + String.valueOf(dataSnapshot.getChildrenCount()) + " values " + dataSnapshot.getKey());
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Task task = data.getValue(Task.class);
                            taskList.add(task);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("AgendaApp", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();

        UpdateTaskList();

    }

    private class RecycleAdapter extends RecyclerView.Adapter {

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
            SimpleItemViewHolder pvh = new SimpleItemViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SimpleItemViewHolder viewHolder = (SimpleItemViewHolder) holder;
            viewHolder.position = position;
            Task task = taskList.get(position);
            ((SimpleItemViewHolder) holder).message.setText(task.getMessage());
        }

        public final  class SimpleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView message;
            public int position;
            public SimpleItemViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                message = (TextView) itemView.findViewById(R.id.task_message);
            }

            @Override
            public void onClick(View view) {

            }
        }
    }
    // Handling on click events for recyclerView BEGING --------------------------------------------

    // Handles single tap and long clicks
    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }

    // GestureDetector class is used to listen for various touch events
    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    // Handling on click events for recyclerView END --------------------------------------------


}
