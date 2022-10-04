package com.example.tickertracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private String broadcastString ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();

        if(extras == null && !FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        if(extras != null) {
            broadcastString = extras.getString("NEW");
            Intent myIntent = new Intent(MainActivity.this, CompanyView.class);
            myIntent.putExtra("ticker", broadcastString);
            DatabaseReference myRef = database.getReference(broadcastString);
            myRef.setValue(broadcastString);
            startActivity(myIntent);

        }



        /* First we will set up the button for the user to head to the company view activity.
        If they click this button it will not show any company data but they can still click on
        tickers from the bottom list. */
        Button companyViewButton = (Button) findViewById(R.id.companyButton);
        companyViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CompanyView.class);
                startActivity(myIntent);
            }
        });
        /* This is to clear the database of all tickers that have been added */
        Button clearDB = (Button) findViewById(R.id.clearButton);

        clearDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView lv = (ListView) findViewById(R.id.tickerList);
                ListAdapter arrayAdapter = lv.getAdapter();

                /* We loop through the array adapter and run a delete command through our
                content provider */


                database.getReference().get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap: dataSnapshot.getChildren()) {

                        }
                    }
                });

                /* Reload the activity to show the data has been deleted. */
                finish();
                startActivity(getIntent());
            }
        });
        /* Once this button is pressed it will run the checks to make sure the ticker given is
        valid. If it is, then it will start the company view activity. */
        Button addTicker = (Button) findViewById(R.id.addButton);
        addTicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rand = new Random();

                /* We need to make sure the ticker is comprised only of  letters and is between
                3 and 6 characters for length. If not, then the user is alerted. If it is, then
                we add it to the database as well*/
                EditText tickerInput = (EditText) findViewById(R.id.tickerInput);
                String ticker = tickerInput.getText().toString().toUpperCase();
                if (ticker.equals("") || !ticker.matches("^[a-zA-Z]*$") ||
                        ticker.trim().contains(" ") || ticker.length() > 6 ||
                        ticker.length() < 3) {
                    Toast.makeText(MainActivity.this, "That ticker is invalid",
                            Toast.LENGTH_LONG).show();
                    TextView tickerPrompt = (TextView) findViewById(R.id.tickerPrompt);
                    tickerPrompt.setTextColor(Color.RED);
                }
                else{
                    DatabaseReference myRef = database.getReference(ticker);
                    myRef.setValue(ticker);
                    Intent myIntent = new Intent(MainActivity.this,
                            CompanyView.class);
                    myIntent.putExtra("ticker", ticker.trim());
                    startActivity(myIntent);
                }

            }
        });
        /* For this part we will populate the listview at the bottom of the screen with tickers
        from our database. We will run a query for all entries and then use that. We will also
        override clicking in order to open up the company view for the ticker that was clicked in
        the listview. */
        ListView lv = (ListView) findViewById(R.id.tickerList);
        List<String> tickerList = new ArrayList<>();



            database.getReference().get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snap: dataSnapshot.getChildren()) {
                        tickerList.add(snap.getValue(String.class));
                    }
                }
            });



            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, tickerList);


            lv.setAdapter(arrayAdapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ticker = lv.getItemAtPosition(position).toString();
                Intent myIntent = new Intent(MainActivity.this, CompanyView.class);
                myIntent.putExtra("ticker", ticker);
                startActivity(myIntent);
            }
        });

    }
}