package gr.uom.pam.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gr.uom.pam.R;
import gr.uom.pam.model.Category;
import gr.uom.pam.model.Store;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PhotoActivity extends AppCompatActivity {
    private static final String IMAGE_NAME = "image.jpg";
    private static final int PERMISSION_REQUEST_CODE = 690;
    private static final int IMAGE_CAPTURE_REQUEST = 329;
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM", Locale.getDefault());
    File _temp_image_file;
    private ImageView _image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //set up take photo listener
        findViewById(R.id.photo_take).setOnClickListener(this::take_photo);
        //set up finish listener
        findViewById(R.id.photo_finish).setOnClickListener(this::do_finish);
        //set up image preview
        _image = findViewById(R.id.photo_image);
        //locate a file on the application's internal storage to make a temporary save of the image from the camera
        _temp_image_file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), IMAGE_NAME);
        //restore image state
        try_load_thumbnail();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if we are exiting, either through finishing or moving back, delete any temp image
        if (isFinishing() && _temp_image_file != null && _temp_image_file.exists())
            _temp_image_file.delete();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                do_finish(null);
            } else {
                Toast.makeText(this, R.string.error_no_photo_permission, Toast.LENGTH_LONG).show();
            }
        }
    }

    //region methods used in taking a photo and displaying it
    private void take_photo(View view) {
        //take the photo
        Intent image_capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        //if the _temp_image_file already exists try to delete it
//        if (_temp_image_file.exists())
//            _temp_image_file.delete();
        //we add the file as an extra output through a _temp_image_file provider, to grant permission to the camera application to save in the internal path
        Uri file_uri = FileProvider.getUriForFile(this, "gr.uom.pam.fileprovider", _temp_image_file);
        image_capture.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);//pass it as a parameter
        if (image_capture.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, R.string.error_no_camera, Toast.LENGTH_LONG).show();
        } else {
            startActivityForResult(image_capture, IMAGE_CAPTURE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK) {
            try_load_thumbnail();
        }
    }

    private void try_load_thumbnail() {
        if (!_temp_image_file.exists()) {
            return;

        }

        if (_image.getWidth() == 0 || _image.getHeight() == 0) {//this means the thumbnail view is not ready
            //so we attach a listener to show the image when it is actually ready
            _image.getViewTreeObserver().addOnGlobalLayoutListener(this::load_thumbnail);
        } else {
            load_thumbnail();
        }
    }

    void load_thumbnail() {
        try {
            //remove the listener otherwise there may be problems
            _image.getViewTreeObserver().addOnGlobalLayoutListener(this::load_thumbnail);
            // Get the dimensions of the View
            int targetW = _image.getWidth();
            int targetH = _image.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(_temp_image_file.getPath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(_temp_image_file.getPath(), bmOptions);
            _image.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_loading_file, Toast.LENGTH_SHORT).show();
        }
    }
    //endregion

    private void do_finish(View view) {
        //check for access to external
        if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return;
        }

        try {
            if (!_temp_image_file.exists())
                throw new Exception(getString(R.string.error_no_photo_taken));
            File file = build_file_name();
            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(_temp_image_file);
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
            Toast.makeText(this, getString(R.string.error_create_file, ex.getMessage()), Toast.LENGTH_SHORT).show();
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
        if (!file.exists())
        if (!file.mkdirs())
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
