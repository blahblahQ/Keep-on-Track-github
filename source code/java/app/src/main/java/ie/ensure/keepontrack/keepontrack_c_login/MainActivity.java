package ie.ensure.keepontrack.keepontrack_c_login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends Activity {

    private TextView txtUsername;
    private TextView txtEmail;
    private Button btnJourney;
    private Button btnResults;
    private Button btnSettings;


    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUsername = (TextView) findViewById(R.id.username);
        txtEmail = (TextView) findViewById(R.id.email);

        // Declare Buttons
        btnJourney = (Button) findViewById(R.id.btnJourney);
        btnResults = (Button) findViewById(R.id.btnResults);
        btnSettings = (Button) findViewById(R.id.btnSettings);

        //btnLogout = (Button) findViewById(R.id.btnLogout);

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String username = user.get("username");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtUsername.setText(username);
        txtEmail.setText(email);

        // ------------------ Button Control ------------------ //

        // Start Journey button click event
        btnJourney.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Launching the login activity
                Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent);
            }
        });

        // Results button click event
        btnResults.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Launching the login activity
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                startActivity(intent);
            }
        });

        // Settings button click event
        btnSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Launching the login activity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
