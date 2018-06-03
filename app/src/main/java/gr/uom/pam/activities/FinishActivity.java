package gr.uom.pam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gr.uom.pam.App;
import gr.uom.pam.R;
import gr.uom.pam.model.Category;
import gr.uom.pam.model.Store;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class FinishActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 690;
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM", Locale.getDefault());
    private CoordinatorLayout _coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        //set up view needed for snackbars
        _coordinator = findViewById(R.id.coordinator);
        //set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        toolbar.setOnMenuItemClickListener(this::save);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return true;
    }

    private boolean save(MenuItem item) {
        if (item == null || item.getItemId() != R.id.menu_action)
            return false;
        do_save();
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                do_save();
            } else {
                Snackbar.make(_coordinator, R.string.error_no_photo_permission, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    void do_save() {
        //check for access to external
        if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return;
        }
        try {
            if (!App.IMAGE.exists())
                throw new Exception(getString(R.string.error_no_photo_taken));
            File file = build_file_name();
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(App.IMAGE);
                out = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes);
                }
            } finally {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
        } catch (Exception ex) {
            Snackbar.make(_coordinator, getString(R.string.error_create_file, ex.getMessage()), Snackbar.LENGTH_SHORT).show();
            return;
        }

        startActivity(
                new Intent(this, StoreActivity.class)
                        // this flag will remove all current activities on top of our starting activity
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );
    }


    private File build_file_name() throws Exception {
        File file = Environment.getExternalStorageDirectory();
        if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) || file == null || !file.exists())
            throw new IOException(getString(R.string.error_no_sd_card));
        if (getIntent().getExtras() == null)
            throw new Exception(getString(R.string.error_invalid_intent));
        //region get store from the path
        Store store = getIntent().getExtras().getParcelable(StoreActivity.STORE);
        if (store == null)
            throw new Exception(getString(R.string.error_intent_store));
        //endregion
        //region get category
        Category category = getIntent().getExtras().getParcelable(CategoryActivity.CATEGORY);
        if (category == null)
            throw new Exception(getString(R.string.error_intent_category));
        //endregion
        //region get dates
        Calendar date_start = (Calendar) getIntent().getExtras().get(DateActivity.DATE_START);
        Calendar date_end = (Calendar) getIntent().getExtras().get(DateActivity.DATE_END);
        if (date_start == null || date_end == null)
            throw new Exception(getString(R.string.error_intent_dates));
        //endregion
        //region get address
        String address = getIntent().getExtras().getString(AddressActivity.ADDRESS, null);
        if (address == null)
            throw new Exception(getString(R.string.error_intent_address));
        //endregion
        //region get comment
        String comment = getIntent().getExtras().getString(CommentActivity.COMMENT, null);
        if (comment == null)
            throw new Exception(getString(R.string.error_intent_comment));
        //endregion
        file = new File(file, "KAs + C&Cs"); //base directory
        file = new File(file, store.get_path()); // attach store path
        file = new File(file, category.get_path());//attach category path
        file = new File(file, "ΠΡΟΩΘΗΤΙΚΕΣ ΕΝΕΡΓΕΙΕΣ"); //attach type name
        file = new File(file, get_month_name(date_start.get(Calendar.MONTH) + 1)); //attach month
        if (!file.exists() && !file.mkdirs())
            throw new Exception(getString(R.string.error_create_directories));

        StringBuilder file_name = new StringBuilder();
        file_name.append(FORMATTER.format(date_start.getTime()))
                .append("-")
                .append(FORMATTER.format(date_end.getTime()))
                .append(" ")
                .append(comment)
                .append(" ")
                .append(address)
                .append(".jpg");
        file = new File(file, file_name.toString());

        return file;
    }

    private String get_month_name(int month) throws Exception {
        switch (month) {
            case 1:
                return "01.ΙΑΝΟΥΑΡΙΟΣ";
            case 2:
                return "02.ΦΕΒΡΟΥΑΡΙΟΣ";
            case 3:
                return "03.ΜΑΡΤΙΟΣ";
            case 4:
                return "04.ΑΠΡΙΛΙΟΣ";
            case 5:
                return "05.ΜΑΙΟΣ";
            case 6:
                return "06.ΙΟΥΝΙΟΣ";
            case 7:
                return "07.ΙΟΥΛΙΟΣ";
            case 8:
                return "08.ΑΥΓΟΥΣΤΟΣ";
            case 9:
                return "09.ΣΕΠΤΕΜΒΡΙΟΣ";
            case 10:
                return "10.ΟΚΤΩΒΡΙΟΣ";
            case 11:
                return "11.ΝΟΕΜΒΡΙΟΣ";
            case 12:
                return "12.ΔΕΚΕΜΒΡΙΟΣ";
            default:
                throw new Exception("Invalid value for MonthOfYear: " + month);
        }
    }

}
