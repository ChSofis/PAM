package gr.uom.pam.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import gr.uom.pam.App;
import gr.uom.pam.BuildConfig;
import gr.uom.pam.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AddressActivity extends AppCompatActivity {
    public static final String ADDRESS = App.NAMESPACE + ".address";
    private static final int PERMISSION_REQUEST_CODE = 742;
    private TextInputLayout _address;
    private FusedLocationProviderClient _location;
    private Geocoder _geocoder;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        //assign address text input to object
        _address = findViewById(R.id.address_text);
        //assign location access object
        _location = LocationServices.getFusedLocationProviderClient(this);
        //assign geocoder to retrieve address from location
        _geocoder = new Geocoder(this, Locale.getDefault());
        //attach auto resolve listener
        findViewById(R.id.address_auto).setOnClickListener(this::resolve_address);
        //attach continue listener
        findViewById(R.id.address_continue).setOnClickListener(this::do_continue);
        if (savedInstanceState != null && savedInstanceState.containsKey(ADDRESS))
            _address.getEditText().setText(savedInstanceState.getString(ADDRESS));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //noinspection ConstantConditions
        outState.putString(ADDRESS, _address.getEditText().getText().toString());
    }

    private void resolve_address(View view) {
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
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                resolve_address(null);
            } else {
                Toast.makeText(this, R.string.error_no_location_permission, Toast.LENGTH_LONG).show();
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
            _address.getEditText().setText(address.toString());
        } catch (IOException e) {
            location_failed(e);
        }
    }

    private void location_failed(Exception error) {
        Toast.makeText(this, getString(R.string.error_retrieving_location, error.getMessage()), Toast.LENGTH_LONG).show();
    }

    private void do_continue(View view) {
        //noinspection ConstantConditions
        String invalid_characters = App.CheckInvalid(_address.getEditText().getText().toString());
        if (_address.getEditText().getText().length() == 0) {
            Toast.makeText(this, R.string.error_no_address_selected, Toast.LENGTH_SHORT).show();
        } else if (invalid_characters != null) {
            Toast.makeText(this, getString(R.string.error_invalid_characters_in_address, invalid_characters), Toast.LENGTH_LONG).show();
        } else {
            ;
            startActivity(
                    new Intent(this, PhotoActivity.class)
                            .putExtras(this.getIntent())
                            .putExtra(ADDRESS, _address.getEditText().getText().toString())
            );
        }
    }


}
