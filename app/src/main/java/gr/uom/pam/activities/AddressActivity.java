package gr.uom.pam.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import gr.uom.pam.App;
import gr.uom.pam.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AddressActivity extends AppCompatActivity {
    public static final String ADDRESS = App.NAMESPACE + ".address";
    private static final int PERMISSION_REQUEST_CODE = 742;
    private EditText _address;
    private FusedLocationProviderClient _location;
    private Geocoder _geocoder;
    private CoordinatorLayout _coordinator;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        //set up view needed for snackbars
        _coordinator = findViewById(R.id.coordinator);
        //assign address text input to object
        _address = findViewById(R.id.text);
        //assign location access object
        _location = LocationServices.getFusedLocationProviderClient(this);
        //assign geocoder to retrieve address from location
        _geocoder = new Geocoder(this, Locale.getDefault());
        //attach auto resolve listener
        findViewById(R.id.fab).setOnClickListener(this::resolve_address);
        //set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        toolbar.setOnMenuItemClickListener(this::do_continue);
        //restore state
        if (savedInstanceState != null && savedInstanceState.containsKey(ADDRESS))
            _address.setText(savedInstanceState.getString(ADDRESS));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_continue,menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ADDRESS, _address.getText().toString());
    }


    private void resolve_address(@SuppressWarnings("unused") View view) {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        _location.getLastLocation()
                .addOnSuccessListener(this::location_success) //on success
                .addOnFailureListener(this::location_failed);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                resolve_address(null);
            } else {
                Snackbar.make(_coordinator, R.string.error_no_location_permission, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void location_success(Location location) {
        if (location == null) {
            location_failed(new Exception(getString(R.string.error_invalid_location_response)));
            return;
        }
        try {
            List<Address> addresses = _geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() == 0)
                location_failed(new Exception(getString(R.string.error_could_not_find_address)));
            StringBuilder address = new StringBuilder();
            Address found = addresses.get(0);
            for (int idx = 0; idx <= found.getMaxAddressLineIndex(); idx++)
                address.append(found.getAddressLine(idx)).append(" ");
            _address.setText(address.toString());
        } catch (IOException e) {
            location_failed(e);
        }
    }

    private void location_failed(Exception error) {
        Snackbar.make(_coordinator, getString(R.string.error_retrieving_location, error.getMessage()), Snackbar.LENGTH_INDEFINITE).show();
    }

    private boolean do_continue(MenuItem item) {
        if (item == null || item.getItemId() != R.id.menu_action)
            return false;
        String invalid_characters = App.CheckInvalid(_address.getText().toString());
        if (_address.length() == 0) {
            Snackbar.make(_coordinator, R.string.error_no_address_selected, Snackbar.LENGTH_LONG).show();
        } else if (invalid_characters != null) {
            Snackbar.make(_coordinator, getString(R.string.error_invalid_characters_in_address, invalid_characters), Snackbar.LENGTH_LONG).show();
        } else {
            if (App.IMAGE.exists())
                //noinspection ResultOfMethodCallIgnored
                App.IMAGE.delete();
            startActivity(
                    new Intent(this, PhotoActivity.class)
                            .putExtras(this.getIntent())
                            .putExtra(ADDRESS, _address.getText().toString())
            );
        }
        return true;
    }


}
