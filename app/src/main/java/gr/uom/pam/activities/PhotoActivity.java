package gr.uom.pam.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import gr.uom.pam.App;
import gr.uom.pam.R;

public class PhotoActivity extends AppCompatActivity {
    private static final String PHOTO = App.NAMESPACE + ".photo";
    private static final int IMAGE_CAPTURE_REQUEST = 329;
    private ImageView _image;
    private CoordinatorLayout _coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        //set up view needed for snackbars
        _coordinator = findViewById(R.id.coordinator);
        //set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        toolbar.setOnMenuItemClickListener(this::do_continue);
        //set up take photo listener
        findViewById(R.id.fab).setOnClickListener(this::take_photo);
        //set up image preview
        _image = findViewById(R.id.image);
        //restore image state
        try_load_thumbnail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_continue,menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if we are exiting, either through finishing or moving back, delete any temp image
        if (isFinishing() && App.IMAGE != null && App.IMAGE.exists())
            //noinspection ResultOfMethodCallIgnored
            App.IMAGE.delete();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }


    //region methods used in taking a photo and displaying it
    private void take_photo(@SuppressWarnings("unused") View view) {
        //take the photo
        Intent image_capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //we add the file as an extra output through a _temp_image_file provider, to grant permission to the camera application to save in the internal path
        Uri file_uri = FileProvider.getUriForFile(this, "gr.uom.pam.fileprovider", App.IMAGE);
        image_capture.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);//pass it as a parameter
        if (image_capture.resolveActivity(getPackageManager()) == null) {
            Snackbar.make(_coordinator, R.string.error_no_camera, Snackbar.LENGTH_LONG).show();
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
        if (!App.IMAGE.exists()) {
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
            BitmapFactory.decodeFile(App.IMAGE.getPath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(App.IMAGE.getPath(), bmOptions);
            _image.setImageBitmap(bitmap);
        } catch (Exception e) {
            Snackbar.make(_coordinator, R.string.error_loading_file, Snackbar.LENGTH_LONG).show();
        }
    }
    //endregion


    private boolean do_continue(MenuItem item) {
        if (item == null || item.getItemId() != R.id.menu_action)
            return false;
        if (!App.IMAGE.exists()) {
            Snackbar.make(_coordinator, R.string.error_no_photo_taken, Snackbar.LENGTH_LONG);
        } else {
            startActivity(
                    new Intent(this, FinishActivity.class)
                            .putExtras(this.getIntent())
                            .putExtra(PHOTO, true));
        }
        return true;
    }

}
