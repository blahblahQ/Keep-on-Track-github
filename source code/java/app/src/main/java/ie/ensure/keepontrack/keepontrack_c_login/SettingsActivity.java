package ie.ensure.keepontrack.keepontrack_c_login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Sam on 11/11/15.
 */
public class SettingsActivity extends Activity implements View.OnClickListener {

    private Button btnLogout;
    private SQLiteHandler db;
    private ResultsHelper resdb;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Results database handler
        resdb = new ResultsHelper(getApplicationContext());
        // Session Manager
        session = new SessionManager(getApplicationContext());
        // Find button
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);
    }


    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_logout:
                db.deleteUsers();
                resdb.deleteResults();
                session.setLogin(false);

                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                }
        }
}
