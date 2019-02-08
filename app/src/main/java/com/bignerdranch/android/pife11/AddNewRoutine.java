package com.bignerdranch.android.pife11;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddNewRoutine extends AppCompatActivity {

    ArrayList<String> routine;
    ListView lv;
    ArrayAdapter<String> listOfGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routine = new ArrayList<String>();
        setContentView(R.layout.activity_add_new_routine);
        lv = (ListView) findViewById(R.id.listOfGoals);
        listOfGoals = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, routine);
        lv.setAdapter(listOfGoals);
    }

    public void addGoal(View view){
        String newestGoal;
        EditText input = (EditText) findViewById(R.id.newGoal);
        newestGoal = input.getText().toString();
        routine.add(newestGoal);
        lv.setAdapter(listOfGoals);
        input.setHint("");
        input.setText("");
        //Toast t = Toast.makeText(getApplicationContext(), newestGoal + " has been added to your routine", Toast.LENGTH_SHORT);
        //t.show();
    }

    public void finishRoutine(View view){
        //Please do!

        //Here, we assume that you are creating a routine to practice it; maybe add a button to immediately start practicing the routine before playing?
        EditText titleView = (EditText) findViewById(R.id.inputTitle);
        String title = titleView.getText().toString();
        routine.add(0, title);

        DataSingleton ds = DataSingleton.getInstance();
        ArrayList<ArrayList<String>> myRoutines = ds.getRoutinesList();
        myRoutines.add(routine);
        ds.setRoutinesList(myRoutines);

        Intent practice_intent = new Intent(AddNewRoutine.this, PracticeHiFi2.class);
        practice_intent.putExtra("SOURCE", "ADD NEW");
        practice_intent.putExtra("ROUTINE_NAME", title);
        startActivity(practice_intent);
    }
}