package ie.ensure.keepontrack.keepontrack_c_login;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sam on 11/11/15.
 */
public class ResultsActivity extends Activity {

    private static final String TAG = ResultsActivity.class.getSimpleName();

    private TextView txtTopScore;
    private TextView txtAvgScore;
    private TextView txtJourneyCount;

    private ResultsHelper resdb;
    Cursor rescursor;
    private SQLiteHandler db;

    Button DisplayResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        txtTopScore = (TextView) findViewById(R.id.TopScore);
        txtAvgScore = (TextView) findViewById(R.id.AvgScore);
        txtJourneyCount = (TextView) findViewById(R.id.JourneyCount);

        getresults();
        Log.d(TAG, "Results captured: Step 1");

        resdb = new ResultsHelper(getApplicationContext()); // Journey Results DB
        db = new SQLiteHandler(getApplicationContext());    // User Details DB

        DisplayResults = (Button) findViewById(R.id.DisplayResultsButton);

        // ------------------ Button Control ------------------ //

        // Start Journey button click event
        DisplayResults.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Results Button Pressed");

                txtTopScore = (TextView) findViewById(R.id.TopScore);
                txtAvgScore = (TextView) findViewById(R.id.AvgScore);

                // Launching the login activity
                int TopScore = 0;
                int journeycount = 0;
                int accumscore = 0;

                rescursor = resdb.getAllResults();  // Put Results into Journey Results DB

                StringBuffer buffer = new StringBuffer();
                rescursor.moveToFirst();  // Move to first
                rescursor.moveToNext();   // Move to second -> Getting past create journey entry
                while (rescursor.moveToNext()) {
                    buffer.append("JOURNEY ID :" + rescursor.getString(0) + "\n");
                    buffer.append("SCORE :" + rescursor.getString(1) + "\n");
                    buffer.append("STATUS :" + rescursor.getString(2) + "\n");
                    buffer.append("DATE :" + rescursor.getString(3) + "\n\n");

                    if(rescursor.getInt(1) > TopScore)
                        TopScore = rescursor.getInt(1);
                    journeycount = journeycount + 1;
                    accumscore = accumscore + rescursor.getInt(1);
                }
                showMessage("Results Summary", buffer.toString());

                float averagescore = accumscore / journeycount;

                txtTopScore.setText(String.valueOf(TopScore));
                txtAvgScore.setText(String.valueOf(averagescore));
                txtJourneyCount.setText(String.valueOf(journeycount));

                Toast.makeText(getApplicationContext(), "Gathering Results: ", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getresults() {
        Log.d(TAG, "Getting Results");
        String SENDTO = AppConfig.URL_RESULTS;

        StringRequest strReq = new StringRequest(Request.Method.GET, SENDTO, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG, "Results Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // find length of results array
                        int index_i = 0;
                        boolean valid = true;
                        int index_length = jObj.getJSONArray("journey").length();
                        // Now store the results in SQLite
                        while(valid)
                        {
                            // Capture
                            String jRjourney = jObj.getJSONArray("journey").getJSONObject(index_i).getString("id");
                            String jRscore = jObj.getJSONArray("journey").getJSONObject(index_i).getString("journey_score");
                            String jRstatus = jObj.getJSONArray("journey").getJSONObject(index_i).getString("status");
                            String jRdate = jObj.getJSONArray("journey").getJSONObject(index_i).getString("createdAt");

                            Log.d(TAG, "Results captured: Step 2");
                            // Inserting row in users table
                            if (jRscore != "-9999") {
                                resdb.addResult(jRjourney, jRscore, jRstatus, jRdate);
                            }
                            index_i = index_i + 1;
                            Log.d(TAG, "Results Journey Added:" + index_i);

                            if (index_i == index_length ) {
                                valid = false;
                                Log.d(TAG, "Results Journey Ended @: " + index_i);
                            }
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Results Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        ) {
            // Configure Header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorisation", db.getUserDetails().get("apiKey"));
                Log.d(TAG, "Results apikey:" + db.getUserDetails().get("apiKey"));
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "Results Query");
        Log.d(TAG, "Results Query Complete");
        Log.d(TAG, strReq.toString());
    }

    /* --  Show Results Message -- */
    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
