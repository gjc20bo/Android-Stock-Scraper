package com.example.tickertracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

public class CompanyView extends AppCompatActivity {
    String ticker = "";
    Cursor mCursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_view);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        AndroidNetworking.initialize(getApplicationContext());

        ListView lv = (ListView) findViewById(R.id.tickerList2);
        TextView companyView = (TextView) findViewById(R.id.nameInput);
        TextView exchangeView= (TextView) findViewById(R.id.exchangeInput);
        TextView descriptionView = (TextView) findViewById(R.id.descriptionInput);
        TextView ipoView = (TextView) findViewById(R.id.ipoInput);
        TextView industryView = (TextView) findViewById(R.id.industryInput);
        TextView sectorView = (TextView) findViewById(R.id.sectorInput);
        TextView ceoView = (TextView) findViewById(R.id.ceoInput);
        TextView websiteView = (TextView) findViewById(R.id.websiteInput);
        TextView currentView = (TextView) findViewById(R.id.currentInput);
        TextView betaView = (TextView) findViewById(R.id.betaInput);
        TextView volumeView = (TextView) findViewById(R.id.volumeInput);
        TextView marketView = (TextView) findViewById(R.id.marketInput);
        TextView dividendView = (TextView) findViewById(R.id.lastInput);
        TextView weekView = (TextView) findViewById(R.id.weekInput);
        TextView stockChangeView = (TextView) findViewById(R.id.stockInput);
        TextView stockPercentView = (TextView) findViewById(R.id.percentInput);
        ImageView logoView = (ImageView) findViewById(R.id.logoInput);
        Button goBackButton = (Button) findViewById(R.id.backToMain);

        Button optionButton = (Button) findViewById(R.id.calculateOption);


        /* This button will just return the user to the previous activity with the ticker input. */
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CompanyView.this, MainActivity.class);
                startActivity(myIntent);
            }
        });

        Bundle getExtras = getIntent().getExtras();
        /* If the user inserted a ticker then it will be sent here and then a request will be made
        to pull the company data. */
        if (getExtras != null) {
            ticker = getExtras.getString("ticker");
            companyView.setText(ticker);
            makeRequest(ticker);
        }
        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CompanyView.this, calculateOption.class);
                myIntent.putExtra("Ticker", ticker);
                startActivity(myIntent);
            }
        });


        Button clearButton = (Button) findViewById(R.id.clearCompany);
        /* This button will empty the company view of all data. */
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticker = "";
                companyView.setText("");
                exchangeView.setText("");
                descriptionView.setText("");
                ipoView .setText("");
                industryView.setText("");
                sectorView.setText("");
                ceoView.setText("");
                websiteView.setText("");
                currentView.setText("");
                betaView.setText("");
                volumeView.setText("");
                marketView.setText("");
                dividendView.setText("");
                weekView.setText("");
                stockChangeView.setText("");
                stockPercentView.setText("");
                logoView.setImageDrawable(null);

            }
        });

        /*  This section is to populate the list view at the bottom of the screen like in
        the main activity.*/
        List<String> tickerList = new ArrayList<>();
        database.getReference().get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()) {
                    tickerList.add(snap.getValue(String.class));
                }
            }
        });



        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CompanyView.this,
                android.R.layout.simple_list_item_1, tickerList);


        lv.setAdapter(arrayAdapter);
        /* Then we allow the user to click the ticker in the list and make an api request. */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ticker = lv.getItemAtPosition(position).toString();
                companyView.setText(ticker);

                makeRequest(ticker);
            }
        });
    }
    /* This function is to actually make the api call to financialmodelingprep.com. It will then
    populate the company view at the top with the respective data. */
    private void makeRequest(String ticker){
        /* First, we will build the request. This has the apikey, the ticker we want, and the
        website that we need to access. */
        ANRequest req = AndroidNetworking.get(
                        "https://financialmodelingprep.com/api/v3/profile/{ticker}")
                .addPathParameter("ticker", ticker)
                .addQueryParameter("apikey", "xxxxxxxxxxx")
                .setPriority(Priority.LOW)
                .build();

        /* With our built request, we can now pull the JSON data from the api and store it into
        our custom class. */
        req.getAsObjectList(CompanyData.class, new ParsedRequestListener<List<CompanyData>>() {
            @Override
            public void onResponse(List<CompanyData> bigdata) {
                String TAG = "FINANCIAL";

                /* Since we pull the data as a list of classes, we can cycle through them (if we
                pulled several) and store them into the company view. Since we will only be pulling
                one ticker information at a time, then the data will be in a single class. */
                for (CompanyData data : bigdata) {
                    ((TextView) findViewById(R.id.nameInput)).setText(data.getName());
                    ((TextView) findViewById(R.id.exchangeInput)).setText(data.getExchange());
                    ((TextView) findViewById(R.id.descriptionInput)).setText(data.getDescription());
                    ((TextView) findViewById(R.id.ipoInput)).setText(data.getIPO());
                    ((TextView) findViewById(R.id.industryInput)).setText(data.getindustry());
                    ((TextView) findViewById(R.id.sectorInput)).setText(data.getsector());
                    ((TextView) findViewById(R.id.ceoInput)).setText(data.getCEO());
                    ((TextView) findViewById(R.id.websiteInput)).setText(data.getwebsite());
                    ((TextView) findViewById(R.id.currentInput)).setText(data.getcurrentPrice());
                    ((TextView) findViewById(R.id.betaInput)).setText(data.getbeta());
                    ((TextView) findViewById(R.id.volumeInput)).setText(data.getvolume());
                    ((TextView) findViewById(R.id.marketInput)).setText(data.getmarket());
                    ((TextView) findViewById(R.id.lastInput)).setText(data.getdividend());
                    ((TextView) findViewById(R.id.weekInput)).setText(data.getweek());
                    ((TextView) findViewById(R.id.stockInput)).setText(data.getChanges());
                    ((TextView) findViewById(R.id.percentInput)).setText(String.valueOf(
                            100 * (Float.valueOf(data.getChanges()) /
                                    (Float.valueOf(data.getcurrentPrice()) -
                                            Float.valueOf(data.getChanges())))));
                /* This is to store the image from our api request. The request sends a URL to the
                image, and Picasso will load the image from that URL. */
                    ImageView logoView = (ImageView) findViewById(R.id.logoInput);
                    Picasso.get().load(data.getImageURL()).into(logoView);

                }

            }

            @Override
            public void onError(ANError anError) {
                // handle error
                Toast.makeText(getApplicationContext(), "Error on getting data ",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}