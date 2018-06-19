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
import android.widget.ImageView;
import android.widget.TextView;

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
    private static final String SAVED = App.NAMESPACE + ".saved";
    private CoordinatorLayout _coordinator;
    private boolean _saved = false;
    private Toolbar _toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        //set up view needed for snackbars
        _coordinator = findViewById(R.id.coordinator);
        //get state
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED))
            _saved = savedInstanceState.getBoolean(SAVED, false);
        //set up _toolbar
        _toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        _toolbar.setOnMenuItemClickListener(this::save);
        //update the view data
        update_view_data();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED, _saved);
    }

    private void update_view_data() {

        if (getIntent() == null || getIntent().getExtras() == null)
            return;
        ((TextView) findViewById(R.id.comment)).setText(getIntent().getExtras().getString(CommentActivity.COMMENT));
        ((TextView) findViewById(R.id.address)).setText(getIntent().getExtras().getString(AddressActivity.ADDRESS));
        Category category = getIntent().getExtras().getParcelable(CategoryActivity.CATEGORY);
        if (category != null)
            ((TextView) findViewById(R.id.category)).setText(category.get_name());
        Store store = getIntent().getExtras().getParcelable(StoreActivity.STORE);
        if (store != null)
            ((TextView) findViewById(R.id.store)).setText(store.get_name());

        Calendar start = (Calendar) getIntent().getExtras().getSerializable(DateActivity.DATE_START);
        Calendar end = (Calendar) getIntent().getExtras().getSerializable(DateActivity.DATE_END);
        if (start != null && end != null) {
            String dates = DateActivity.FORMATTER.format(start.getTime()) + " - " + DateActivity.FORMATTER.format(end.getTime());
            ((TextView) findViewById(R.id.date)).setText(dates);
        }
        if (!App.IMAGE.exists())
            return;
        ImageView image = findViewById(R.id.photo);
        if (image.getWidth() == 0 || image.getHeight() == 0)
            image.getViewTreeObserver().addOnGlobalLayoutListener(this::load_thumbnail);
        else
            load_thumbnail();
    }

    private void load_thumbnail() {
        try {
            ImageView image = findViewById(R.id.photo);
            //remove the listener otherwise there may be problems
            image.getViewTreeObserver().removeOnGlobalLayoutListener(this::load_thumbnail);
            App.LoadImageToView(image, App.IMAGE);
        } catch (Exception e) {
            Snackbar.make(_coordinator, R.string.error_loading_file, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        update_menu();
        return true;
    }

    private boolean save(MenuItem item) {
        if (item == null)
            return false;
        if (item.getItemId() == R.id.menu_save) {
            do_save();
            return true;
        } else if (item.getItemId() == R.id.menu_restart) {
            do_restart();
            return true;
        }
        return false;
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
                _saved = true;
                update_menu();
                Snackbar.make(_coordinator, getString(R.string.file_created, file.getAbsolutePath()), Snackbar.LENGTH_INDEFINITE).show();
            } finally {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
        } catch (Exception ex) {
            Snackbar.make(_coordinator, getString(R.string.error_create_file, ex.getMessage()), Snackbar.LENGTH_SHORT).show();
        }
    }

    private void update_menu() {
        _toolbar.getMenu().findItem(R.id.menu_save).setVisible(!_saved);
        _toolbar.getMenu().findItem(R.id.menu_restart).setVisible(_saved);
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

    void do_restart() {
        startActivity(
                new Intent(this, StoreActivity.class)
                        // this flag will remove all current activities on top of our starting activity
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );
    }
}
