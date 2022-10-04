package com.example.tickertracker;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.webkit.URLUtil;

import android.widget.Toast;
import android.telephony.SmsMessage;


import java.io.IOException;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/* This is the class for our broadcast receiver. This will be listening for SMS messages and
then read them to see if there is a relevant ticker in the message. It will only search for
Ticker:<TICKER>. Anything before or after is fine, but that exact form must be in the message.
Then it will check the ticker itself. If TICKER is of the proper form, then it will pull the
information and send it back to our mainactivity. */
public class SMSReceiver extends BroadcastReceiver {
    /*  I setup a background task to handle async requests for the webpage. This will be for
    checking if the url is valid and if the response code is valid. Most response codes are going
    to be 301 because it is redirecting from http to https but I wanted to practice these concepts
    a little bit and make sure I can pull the code if something went wrong.*/
    class backgroundTask extends AsyncTask<String,Void,Integer> {
        /* First, we will have  */
        private String sTemp = "";
        public backgroundTask(String s) {
            sTemp = s;
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected Integer doInBackground(String... params) {
            int code = 0;

            if (sTemp.matches("^[a-zA-Z]*$")) {

                String testingURL = "https://www.seekingalpha.com/symbol/" + sTemp;
                try {
                    URL url = new URL(testingURL);

                    try {

                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        connection.setInstanceFollowRedirects(false);
                        if(connection.getErrorStream() == null) {
                            code = connection.getResponseCode();
                        }

                    } catch (IOException e) {

                    }
                } catch (MalformedURLException e) {

                }
            }
            return code;
        }
        @Override
        protected void onPostExecute(Integer result) {
        }
    }

    /* From this point on we have our functions for our broadcast receiver. */
    @Override
    public void onReceive(Context context, Intent intent) {
        /*  Build our intent to send data back to our main activity.*/
        Intent myIntent = new Intent(context, MainActivity.class);
        /* This is checking the intent from our receiver receives NOT the intent that we just
        created. Once a message is received, then we start checking our message. */
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                /* Looking at the developer.android.com website for SmsMessage we need to pull the
                raw PDU from the message and then from there we can create a SmsMessage object from
                it and from there we can pull the relevant information as a string.*/
                SmsMessage[] message = null;
                Object[] pdus = (Object[]) bundle.get("pdus");
                message = new SmsMessage[pdus.length];
                String s3 ="";
                /* From here we will take the message itself from the
                object and then place it into a string. */
                for(int i=0; i <message.length;i++) {
                    message[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    s3 = message[i].getMessageBody();
                }
                String sTemp = "";
                int code=0;

                /* Now, we will check the string for the format Ticker:<TICKER>. Anything before
                and after is irrelevant. If this fails, we set our "code" value to -1 to let the
                user know we could not find the our format. */
                if (s3.matches("(.*)Ticker:<(.*)>(.*)")) {
                    /* If we do have our format, then we pull the data inside the <>. The ticker
                    itself needs to be alphabetic characters only. The program will make them
                    uppercase so that is not required. If the check shows that the ticker itself
                    is invalid like if it has numbers or symbols then it will return a code to let
                    the user know. */
                    sTemp = s3.substring(s3.lastIndexOf("<") + 1, s3.lastIndexOf(">"));
                    if(sTemp.matches("^[a-zA-Z]*$")) {
                        sTemp = sTemp.toUpperCase(Locale.ROOT);
                        try {
                            code = new backgroundTask(sTemp).execute().get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        code = -2;
                    }
                }
                else {
                    code = -1;
                }

                /* We determine the url is valid again, extra checking that is a bit
                redundant but just to make sure. If the ticker was valid then we put
                it in the intent to send.*/
                if(URLUtil.isValidUrl("https://www.seekingalpha.com/symbol/" + sTemp)
                        && code > 0 && code != 404) {
                    myIntent.putExtra("NEW",sTemp);

                }
                else if(code == -1) {
                    Toast.makeText(context, "No valid watchlist entry was found.",
                            Toast.LENGTH_LONG).show();
                }
                else if (code == -2) {
                    Toast.makeText(context, sTemp +" is not a valid ticker",
                            Toast.LENGTH_LONG).show();
                }


            }
        }

        /* Now we will send the intent back to the main activity to restart. If  */
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);

    }

}