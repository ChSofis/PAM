package gr.uom.pam;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;

public class App extends Application {
    public static final String NAMESPACE = "gr.uom.pam";
    static final String VALID_CHARACTERS = "abcdefghijklmnopqrstuvwxyz1234567890,αάβγδεέζηήθίικλμνξόοπρστύυφχψώω !;()QWERTYUIOPLKJHGFDSAZXCVBNMςΕΡΤΥΘΙΟΠΛΚΞΗΓΦΔΣΑΖΧΨΩΒΝΜΆΈΎΊΌΉΏ";
//    static final String INVALID_CHARACTERS = "<:/\\|?\">*";
    public static File IMAGE;

    public static String CheckInvalid(String string) {
        StringBuilder reply = new StringBuilder();
        string = string.toLowerCase();
        for (char chr : string.toCharArray()) {
            if (!VALID_CHARACTERS.contains((String.valueOf(chr)))) {
                reply.append(chr).append(" ");
            }
        }
        return reply.length() > 0 ? reply.deleteCharAt(reply.length() - 1).toString() : null;
    }

    public static void LoadImageToView(ImageView image, File file) {
        // Get the dimensions of the View
        int targetW = image.getWidth();
        int targetH = image.getHeight();

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
        image.setImageBitmap(bitmap);
    }

    public static void RefreshFile(File file, Context ctx) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IMAGE = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg");
    }
}
