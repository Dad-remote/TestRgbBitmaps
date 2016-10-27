package ws.uname.testrgb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import uk.co.senab.photoview.PhotoViewAttacher;

@EActivity(R.layout.activity_show_image)
public class ShowImageActivity extends Activity {

    @Extra String imagePath;

    @ViewById ImageView image;

    private PhotoViewAttacher attacher;

    @AfterViews
    protected void afterViews() {
        attacher = new PhotoViewAttacher(image);
        ImageLoader.getInstance().displayImage(imagePath, image, new LoadingListener(attacher));
    }

    private static class LoadingListener implements ImageLoadingListener {

        private PhotoViewAttacher attacher;

        public LoadingListener(PhotoViewAttacher attacher) {
            this.attacher = attacher;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            attacher.update();
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
        }
    }
}
