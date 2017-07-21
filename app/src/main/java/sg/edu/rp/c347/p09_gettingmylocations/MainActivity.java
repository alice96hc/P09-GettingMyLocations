package sg.edu.rp.c347.p09_gettingmylocations;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    TextView tvTitle, tvLatitude, tvLongitude;
    EditText etLatitude, etLongitude;
    Button btnStart, btnStop, btnCheck;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String[] folderLocation = new String[1];

        tvTitle = (TextView) this.findViewById(R.id.tvTitle);
        tvLatitude = (TextView) this.findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) this.findViewById(R.id.tvLongitude);
        etLatitude = (EditText) this.findViewById(R.id.etLatitude);
        etLongitude = (EditText) this.findViewById(R.id.etLongitude);
        btnStart = (Button) this.findViewById(R.id.btnStart);
        btnStop = (Button) this.findViewById(R.id.btnStop);
        btnCheck = (Button) this.findViewById(R.id.btnCheck);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //create the folder
        folderLocation[0] = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Problem-Statement";

        File folder = new File(folderLocation[0]);
        if (folder.exists() == false) {
            boolean result = folder.mkdir();
            if (result == true) {
                Log.d("File Read/Write", "Folder created");
            }
        }
        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this,MyService.class);
                startService(i);

                Toast.makeText(MainActivity.this, "Service is running", Toast.LENGTH_SHORT).show();

                folderLocation[0] = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Problem-Statement";
                File targetFile = new File(folderLocation[0], "records.txt");

                try {
                    FileWriter writer = new FileWriter(targetFile, true);
                    writer.write("Hello world" + "\n");
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                File targetFile = new File(String.valueOf(folderLocation),"records.txt");

                if(targetFile.exists()==true){
                    String data = "";
                    try{
                        FileReader reader = new FileReader(targetFile);
                        

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        );
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setSmallestDisplacement(100);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);

        } else {
            mLocation = null;
            Toast.makeText(MainActivity.this, "Permission not granted to retrieve location info", Toast.LENGTH_SHORT).show();
        }
        if (mLocation != null) {
            etLatitude.setText((int) mLocation.getLatitude());
            etLongitude.setText((int) mLocation.getLongitude());

            // Toast.makeText(this, "Lat : " + mLocation.getLatitude() + " Lng : " + mLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        etLatitude.setText((int) location.getLatitude());
        etLongitude.setText((int) location.getLongitude());

        //the detected location is given by the variable location in the signature
        Toast.makeText(this, location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
