package com.bignerdranch.android.pife11;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddNewRoutine extends AppCompatActivity {

    ArrayList<String> routine;
    ListView lv;
    ArrayAdapter<String> listOfGoals;
    private AppCompatEditText newGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeCoins();
        routine = new ArrayList<String>();
        setContentView(R.layout.activity_add_new_routine);
        lv = (ListView) findViewById(R.id.listOfGoals);
        listOfGoals = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routine);

        lv.setAdapter(listOfGoals);


        EditText edit_txt = (EditText) findViewById(R.id.newGoal);
        newGoal = findViewById(R.id.newGoal);
        newGoal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    Log.d("location", "arrived here");
                    addGoal(null);
                }
                return false;
            }
        });
    }

    public void addGoal(View v){
        String newestGoal;
        EditText input = (EditText) findViewById(R.id.newGoal);
        newestGoal = input.getText().toString();
        routine.add(newestGoal);
        lv.setAdapter(listOfGoals);
        input.setHint("");
        input.setText("");

    }

    public void finishRoutine(View view){
        //Please do!

        //Here, we assume that you are creating a routine to practice it; maybe add a button to immediately start practicing the routine before playing?
        EditText titleView = (EditText) findViewById(R.id.inputTitle);
        String title = titleView.getText().toString();
        //System.out.println("Old Add:" + routine.toString());

        boolean missingData = false;

        if (TextUtils.isEmpty(title)) {
            titleView.setError("Please add a routine title.");
            missingData = true;
        }
        if (routine.isEmpty()) {
            EditText input = (EditText) findViewById(R.id.newGoal);
            input.setError("Please add at least 1 goal to your routine.");
            missingData = true;
        }
        if (missingData) return;


        DataSingleton ds = DataSingleton.getInstance();
        ArrayList<ArrayList<String>> myRoutines = ds.getRoutinesList();

        ArrayList<String> toBeAdded = new ArrayList<String>();
        toBeAdded.add(0, title);
        for (String str: routine) {
            toBeAdded.add(str);
        }

        System.out.println("Testing this one out...." + toBeAdded.toString());

        //System.out.println("New Add:" + routine.toString() + "|" + toBeAdded.toString());
        myRoutines.add(toBeAdded);
        ds.setRoutinesList(myRoutines);
        System.out.println("Entire List Here Add:" + myRoutines.toString());

        Intent practice_intent = new Intent(AddNewRoutine.this, PracticeHiFi2.class);

        System.out.println("Entire List Here:" + ds.getRoutinesList().toString());
        practice_intent.putExtra("SOURCE", "ADD NEW");
        practice_intent.putExtra("ROUTINE_NAME", title);
        startActivity(practice_intent);
    }

    public void goToStore(View view) {
        Intent practice_intent = new Intent(this, Store.class);
        startActivity(practice_intent);
    }

    public void changeCoins(){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Stats").child("xp");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String pifePoints = dataSnapshot.getValue().toString().trim();
                    TextView pointsDisplay = (TextView) findViewById(R.id.coins);
                    pointsDisplay.setText(pifePoints);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
