package ws.uname.testrgb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_APP_REQUEST_CODE = 1122;

    @ViewById Toolbar toolbar;
    @ViewById ImageView image;
    @ViewById TextView originalFileSize;
    @ViewById TextView fileSize;

    @InstanceState File imageFile;

    private File currentFile;
    private String outputFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            ImageLoader.getInstance().init(ImageHelper.getImageLoaderConfig(this));
            outputFileName = UUID.randomUUID().toString();
        }
    }

    @AfterViews
    protected void afterViews() {
        setSupportActionBar(toolbar);
        Files.setPaths(this);
    }

    @Click(R.id.fab)
    protected void onFabClick() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            imageFile = Files.newImageFile();
            if (imageFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(takePictureIntent, CAMERA_APP_REQUEST_CODE);
            }
        }
    }

    @Click
    protected void saveJpeg() {
        if (imageFile != null) {
            saveImage(Files.SaveType.JPEG, "JPEG_" + outputFileName);
        }
    }

    @Click
    protected void saveArgb() {
        if (imageFile != null) {
            saveImage(Files.SaveType.ARGB, "ARGB_" + outputFileName);
        }
    }

    @Click
    protected void saveRgb() {
        if (imageFile != null) {
            saveImage(Files.SaveType.RGB, "RGB_" + outputFileName);
        }
    }

    @Background
    protected void saveImage(final Files.SaveType saveType, String fileName) {
        try {
            currentFile = Files.saveImage(saveType, imageFile.getAbsolutePath(), fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        showInfo(currentFile);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, saveType.name() + " saved", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Click(R.id.image)
    protected void onImageClick() {
        if (currentFile != null) {
            ShowImageActivity_.intent(this).imagePath(Uri.fromFile(currentFile).toString()).start();
        } else if (imageFile != null) {
            ShowImageActivity_.intent(this).imagePath(Uri.fromFile(imageFile).toString()).start();
        }
    }

    @OnActivityResult(CAMERA_APP_REQUEST_CODE)
    protected void onCameraResult(int result, Intent data) {
        if (result == Activity.RESULT_OK) {
            originalFileSize.setText(getString(R.string.original_file_size, imageFile.length()));
            prepareImage();
        }
    }

    @Background
    protected void prepareImage() {
        try {
            File smallImage = Files.saveSmallImage(this, imageFile);
            showInfo(smallImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    protected void showInfo(File file) {
        ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), image);
        fileSize.setText(getString(R.string.file_size, file.length()));
    }


}
