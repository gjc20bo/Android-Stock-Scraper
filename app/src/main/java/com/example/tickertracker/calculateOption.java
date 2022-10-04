package com.example.tickertracker;

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.w3c.dom.Text;

public class calculateOption extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_option);
        Button calculateButton = (Button) findViewById(R.id.calculateButton);

        EditText asset = (EditText) findViewById(R.id.currentAsset);
        EditText strike = (EditText) findViewById(R.id.strikePrice);
        EditText risk = (EditText) findViewById(R.id.riskRate);
        EditText time = (EditText) findViewById(R.id.timeLeft);
        EditText volatility = (EditText) findViewById(R.id.volatility);
        TextView finalValue = (TextView) findViewById(R.id.callPrice);

        NormalDistribution ndist = new NormalDistributionImpl();







        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double assetPrice = Double.valueOf(asset.getText().toString());
                Double strikePrice = Double.valueOf(strike.getText().toString());
                Double riskValue = Double.valueOf(risk.getText().toString());
                Double timeValue = Double.valueOf(time.getText().toString());
                Double volValue = Double.valueOf(volatility.getText().toString());
                Double d1 = (log(assetPrice/strikePrice) + (riskValue +
                        pow(volValue,2)/2)*timeValue) / (volValue * sqrt(timeValue));
                Double d2 = d1 - volValue * sqrt(timeValue);
                Double result = null;
                try {
                    result = assetPrice * ndist.cumulativeProbability(d1) - strikePrice *
                            exp(-1 * riskValue*timeValue) * ndist.cumulativeProbability(d2);
                } catch (MathException e) {
                    e.printStackTrace();
                }

                finalValue.setText(String.valueOf(result));
            }
        });
    }
}