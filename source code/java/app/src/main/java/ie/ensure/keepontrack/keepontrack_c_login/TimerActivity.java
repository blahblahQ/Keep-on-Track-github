package ie.ensure.keepontrack.keepontrack_c_login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimerActivity extends Activity implements OnClickListener,
        SensorEventListener {

    private static final String TAG = TimerActivity.class.getSimpleName();

    DatabaseHelper myDb;

    Button start, pause;
    TextView time;
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    int samplenumber = 0;

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    boolean stopTimer = false;

    GPSTracker gps = new GPSTracker(this);

    String GPSLatitude;
    String GPSLongitude;

    Boolean validjourney;

    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        validjourney = false;
        myDb = new DatabaseHelper(this);

        db = new SQLiteHandler(getApplicationContext());

        start = (Button) findViewById(R.id.startButton);
        pause = (Button) findViewById(R.id.pauseButton);
        time = (TextView) findViewById(R.id.timerValue);

        start.setOnClickListener(this);
        pause.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.startButton:
                start.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);
                validjourney = true;
                createJourney(); // Send a "Create Journey" message to the server

                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
                startAccelCapture();
                /* ------ GPS DATA START------ */
                TextView longitudeCapture = (TextView) findViewById(R.id.longitudeCap);
                TextView latitudeCapture = (TextView) findViewById(R.id.latitudeCap);

                if (gps.canGetLocation()) {
                    Log.d(TAG,"Getting Location Start"  + GPSLatitude);
                    gps.getLocation();                                  ///********** gpsT
                    GPSLatitude = String.valueOf(gps.getLatitude());
                    latitudeCapture.setText(GPSLatitude);
                    GPSLongitude = String.valueOf(gps.getLongitude());
                    longitudeCapture.setText(GPSLongitude);
                } else {
                    gps.showSettingsAlert();
                }
                /* ------- GPS DATA END ------- */
                break;

            case R.id.pauseButton:
                pause.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);

                if (validjourney)
                {
                    timeSwapBuff += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);

                    /* -- Get Data From Database -- */

                    Cursor res = myDb.getAllData();
                    //myDb.close();
                    if (res.getCount() == 0) {
                        // show message
                        showMessage("Error 404", "No Data Found, Please press Start 'Journey' to begin");
                        return;
                    }

                    endJourney();   // Send Journey Close

                    StringBuffer buffer = new StringBuffer();
                    while (res.moveToNext()) {
                        buffer.append("JOURNEY ID :" + res.getString(0) + "\n");
                        buffer.append("SAMPLENO :" + res.getString(1) + "\n");
                        buffer.append("TIMESTAMP :" + res.getString(2) + "\n");
                        buffer.append("LATITUDE :" + res.getString(3) + "\n");
                        buffer.append("LONGITUDE :" + res.getString(4) + "\n");
                        buffer.append("MAXx :" + res.getString(5) + "\n");
                        buffer.append("MAXy :" + res.getString(6) + "\n");
                        buffer.append("MAXz :" + res.getString(7) + "\n\n");
                    }
                    showMessage("Journey Summary", buffer.toString());
                    myDb.deleteJourneyData();
                    samplenumber = 0;
                }
                else if (!validjourney)
                {
                    Toast.makeText(getApplicationContext(), "No Journey Created:", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //
    /* --  Show Results Message -- */
    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    /* -- Update Timer -- */
    private Runnable updateTimerThread = new Runnable() {

        int noCapture = 0; // Flag to help capture

        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;

            int milliseconds = (int) (updatedTime % 1000);
            String localtime = "" + mins + ":" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds);
            time.setText(localtime);

            if (((secs % 10) == 0)) {   // Every Ten Seconds
                // -- Enter Data to local storage -- //
                if (noCapture == 0 && timeInMilliseconds >= 1000) {
                    String IDofJOURNEY = myDb.getJourneyId();

                    String currentDateTimeString = toFormat.format(new Date());//getDateTimeInstance().format(new Date());
                    Log.d(TAG, currentDateTimeString);

                    samplenumber = samplenumber + 1;
                    // -- GPS Query
                    Log.d(TAG,"Getting Location Time");
                    gps.getLocation();                                  // Query for new location   ///**********
                    GPSLatitude = String.valueOf(gps.getLatitude());    // Store Location Latitude
                    GPSLongitude = String.valueOf(gps.getLongitude());  // Store Location Longitude
                    Log.d(TAG,"LATITUDE T:"  + GPSLatitude);
                    Log.d(TAG,"LONGITUDE T:" + GPSLongitude);
                    updateJourney(IDofJOURNEY, GPSLatitude, GPSLongitude, "0", String.valueOf(deltaXMax), String.valueOf(deltaYMax), String.valueOf(deltaZMax), currentDateTimeString, String.valueOf(samplenumber));
                    myDb.insertData(currentDateTimeString, GPSLatitude, GPSLongitude, deltaXMax, deltaYMax, deltaZMax);

                    // -- Clear Display and Max values -- //
                    resetDisplayValues(); // Reset the display values
                    noCapture = 1;
                }
            }
            // -- No Capture required -- //
            else {
                noCapture = 0;  // Reset for next capture
            }
            // -- Not Stopped -- //
            if (!stopTimer)
                customHandler.postDelayed(this, 0);
        }
    };

    /*------------------------------------------------*/

    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float minXV = 0;
    private float maxXV = 0;
    private float minYV = 0;
    private float maxYV = 0;
    private float minZV= 0;
    private float maxZV= 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private TextView maxX, maxY, maxZ;


    private void startAccelCapture() {
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
        }
    }

    public void initializeViews() {
        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        startAccelCapture();
        gps.getLocation();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void resetDisplayValues() {
        maxX.setText("0.00");
        maxY.setText("0.00");
        maxZ.setText("0.00");
        deltaXMax = 0;
        deltaYMax = 0;
        deltaZMax = 0;
        deltaX = 0;
        deltaY = 0;
        deltaZ = 0;
        minXV = 0;
        minYV = 0;
        minZV = 0;
        maxXV = 0;
        maxYV = 0;
        maxZV = 0;
        lastX = 0;
        lastY = 0;
        lastZ = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        // X's
        if (event.values[0] < minXV)
            minXV = event.values[0];
        else if (event.values[0] > maxXV)
            maxXV = event.values[0];

        // Y's
        if (event.values[1] < maxYV)
            minYV = event.values[1];
        else if (event.values[1] > maxYV)
            maxYV = event.values[1];

        // Z's
        if (event.values[2] < maxZV)
            minZV = event.values[2];
        else if (event.values[2] > maxZV)
            maxZV = event.values[2];

        // X's
        if (Math.abs(minXV) > Math.abs(maxXV))
            deltaX = (lastX - minXV);
        else
            deltaX = (lastX - maxXV);
        // Y's
        if (Math.abs(minYV) > Math.abs(maxYV))
            deltaY = (lastY - minYV);
        else
            deltaY = (lastY - maxYV);
        // Z's
        if (Math.abs(minZV) > Math.abs(maxZV))
            deltaZ = (lastZ - minZV);
        else
            deltaZ = (lastZ - maxZV);

    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (Math.abs(deltaX) > Math.abs(deltaXMax)) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (Math.abs(deltaY) > Math.abs(deltaYMax)) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (Math.abs(deltaZ) > Math.abs(deltaZMax)) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }

    /* -------- API Communication -------- */

    /**
     * Function to store user in MySQL database will post params(tag, name, username, policy
     * email, password) to register url
     */

    private void createJourney() {

        Log.d(TAG, "Creating journey");
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_JOURNEY, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, "Journey Request: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // Store journeyid in SQLite
                        String journeyid = jObj.getString("journey_id");
                        Log.d(TAG, "Created Journey ID:" + journeyid);
                        // Inserting row in journey table
                        myDb.insertJourneyId(journeyid);
                    }
                }
                /* -- Error Message -- */ catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Create Journey Error: " + error.getMessage());
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
                Log.d(TAG, "Creating new journey apikey:" + db.getUserDetails().get("apiKey"));
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "new Journey added");
        Log.d(TAG, "Create new journey complete");
    }


    /**
     * Function to store user in MySQL database will post params(tag, name, username, policy
     * email, password) to register url
     */

    private boolean updateJourney(final String JOURNEYID, final String XGPS, final String YGPS,
                                  final String ZGPS, final String XACL, final String YACL,
                                  final String ZACL, final String TIMESTAMPS, final String SAMPLENOS) {

        boolean responsefound = false;

        Log.d(TAG, AppConfig.URL_JOURNEY_DATA + JOURNEYID);
        String SENDTO = AppConfig.URL_JOURNEY_DATA + JOURNEYID;
        Log.d(TAG, "SENT TO:" + SENDTO);
        StringRequest strReq = new StringRequest(Request.Method.POST, SENDTO, new Response.Listener<String>() {

            @Override
            public void onResponse(String s) {
                Log.d(TAG, "Response was: " + s.toString() + " For Journey " + JOURNEYID); // SAMPLENOS **************
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "Update Journey Error: " + volleyError.getMessage());
                Toast.makeText(getApplicationContext(),
                        volleyError.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            // Will be in Journeys v
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorisation", db.getUserDetails().get("apiKey"));
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {

                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("x_gps", XGPS);
                params.put("y_gps", YGPS);
                params.put("z_gps", ZGPS);
                params.put("x_acl", XACL);
                params.put("y_acl", YACL);
                params.put("z_acl", ZACL);
                params.put("timestamp", TIMESTAMPS);
                params.put("sample_no", SAMPLENOS);
                Log.d(TAG, "Request params: " + params);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "Journey Updated");
        Log.d(TAG, "Update journey data complete");
        Log.d(TAG, "Request header: " + strReq.getUrl());
        Log.d(TAG, "Request body: " + strReq.toString());
        return responsefound;
    }

    /**
     * Function to store user in MySQL database will post params(tag, name, username, policy
     * email, password) to register url
     */

    private void endJourney() {
        Log.d(TAG, "Ending journey");
        String JOURNEYID = myDb.getJourneyId();
        String SENDTO = AppConfig.URL_JOURNEY_CLOSE + JOURNEYID;

        StringRequest strReq = new StringRequest(Request.Method.PUT, SENDTO, new Response.Listener<String>() {
            public void onResponse(String response) {
                Log.d(TAG, "Journey End: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Create Journey Error: " + error.getMessage());
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
                Log.d(TAG, "Ending journey apikey:" + db.getUserDetails().get("apiKey"));
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "Journey Ended");
        Log.d(TAG, "End journey complete");
    }
}