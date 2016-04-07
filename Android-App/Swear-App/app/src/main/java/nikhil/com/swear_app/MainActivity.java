package nikhil.com.swear_app;

import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.AIService;
import ai.api.RequestExtras;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements AIListener {

    private Button listenButton;
    private TextView resultTextView;
    private EditText usernameET;

    private AIService aiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listenButton = (Button) findViewById(R.id.listenButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        usernameET = (EditText) findViewById(R.id.username);

        final AIConfiguration config = new AIConfiguration("16aba6053f994eda9586c30feeebe9e7",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        // request permission
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO}, 0);


        // listen initially
        aiService.startListening();

        // "http://d36bb8c4.ngrok.io/swear"
        // user login
//        new PostClass(this, "http://b7f2805d.ngrok.io/swear", "penis", "amrut").execute();

    }


//    public void postData() throws Exception {
//
//
//        URL url = new URL("http://d36bb8c4.ngrok.io/swear");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("POST");
////        connection.setRequestProperty("User-Agent", "Nadgir");
////        connection.setRequestProperty("Accept-Language", "en-US,en;0.5");
//        connection.setDoOutput(true);
//
//        // output data
//        DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
//        dStream.writeBytes("penis=assrut");
//        dStream.flush();
//        dStream.close();
//
//        Log.d("Sent post", "DO NOT READ");
//
//    }


    public void listenButtonOnClick(final View view) {
        resultTextView.setText("{New Query Pending}");
        aiService.startListening();

    }

    @Override
    public void onResult(final AIResponse response) {
        Result result = response.getResult();

        Log.d("Result", "Got result");

        Log.d("Query: ", Integer.toString(result.getResolvedQuery().length()));

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }


        } // end of if
        //check for Amrut Nadgir
        // check for curse words (haw)
        String[] badboy = new String[]{"truck", "sit", "pass", "Rishi", "Arthur","Amrut", "Nadgir"};

        for (int i = 0; i < badboy.length; i++) {

            String badword = badboy[i];

//            Log.d("Refreeze", badword);

            if (result.getResolvedQuery().toString().contains(badword)) {

                Log.d("Bad Word", "Wash your mouth boi");
                Toast.makeText(getApplicationContext(), "Wash your mouth!", Toast.LENGTH_SHORT).show();

                new PostClass(this, "http://106f9bf3.ngrok.io/swear", "user", usernameET.getText().toString()).execute();

            } // end of if

        } // end of foreach


        // Show results in TextView.
        resultTextView.setText(result.getResolvedQuery());

        // send another request
        aiService.startListening();


    } // end of onResult

    // postmaster general
    private class PostClass extends AsyncTask<String, Void, Void> {

        private final Context context;
        private String stringURL;
        private String key, value;

        public PostClass(Context c, String stringURL, String key, String value){
            this.context = c;
            this.stringURL = stringURL;
            this.key = key;
            this.value = value;
        }

//        "http://d36bb8c4.ngrok.io/swear"

        protected void onPreExecute(){
//            progress= new ProgressDialog(this.context);
//            progress.setMessage("Loading");
//            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

//                final TextView outputView = (TextView) findViewById(R.id.showOutput);
                URL url = new URL(stringURL);

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                String urlParameters = "fizz=buzz";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(key + "=" + value);
                dStream.flush();
                dStream.close();
                int responseCode = connection.getResponseCode();

                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters);
                output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);
                output.append(System.getProperty("line.separator")  + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());
//
//                MainActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        outputView.setText(output);
//                        progress.dismiss();
//                    }
//                });

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }



    // unimplemented implemented methods
    @Override
    public void onError(AIError error) {
        resultTextView.setText(error.toString());
        aiService.startListening();
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        Log.d("Listening", "Started");
    }

    @Override
    public void onListeningCanceled() {
        aiService.startListening();
    }

    @Override
    public void onListeningFinished() {
        Log.d("Listening", "Finished");
    }
}
