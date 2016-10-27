package ws.uname.testrgb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class Files {

    private static final String IMAGE_FOLDER = "images";
    private static final float SMALL_IMAGE_RATIO = 0.5f;
    private static final int MAX_LENGTH = 2000;

    private static String filesDirPath;

    public static void setPaths(Context context) {
        Files.filesDirPath = context.getApplicationContext().getExternalFilesDir(null).getAbsolutePath();
    }

    public static File getFilesDir() {
        return new File(filesDirPath);
    }

    public static File newImageFile() {
        return newImageFile(UUID.randomUUID().toString());
    }

    public static File newImageFile(String imageName) {
        return newFileInFolder(imageName, IMAGE_FOLDER);
    }

    private static File newFileInFolder(String imageName, String folderName) {
        String dirPath = getFilesFolderPath(folderName);
        File file = new File(dirPath + File.separator + imageName);
        try {
            createParentDirs(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static String getFilesFolderPath(String internalFolder) {
        return getFilesDir().getAbsolutePath() + File.separator + internalFolder;
    }

    public static void createParentDirs(File file) throws IOException {
        File parent = file.getCanonicalFile().getParentFile();
        if (parent == null) {
      /*
       * The given directory is a filesystem root. All zero of its ancestors
       * exist. This doesn't mean that the root itself exists -- consider x:\ on
       * a Windows machine without such a drive -- or even that the caller can
       * create it, but this method makes no such guarantees even for non-root
       * files.
       */
            return;
        }
        parent.mkdirs();
        if (!parent.isDirectory()) {
            throw new IOException("Unable to create parent directories of " + file);
        }
    }

    public static File saveImage(SaveType saveType, String filePath, String name) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        ExifHelper helper = new ExifHelper();
        helper.createInFile(filePath);
        helper.readExifData();

        File file = newImageFile(name);
        switch (saveType) {
            case JPEG:
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                break;
            case ARGB:
                bitmap = replaceWith(Bitmap.Config.ARGB_8888, bitmap);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                break;
            case RGB:
                bitmap = replaceWith(Bitmap.Config.RGB_565, bitmap);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                break;
        }
        bitmap.recycle();
        helper.resetOrientation();

        helper.createOutFile(file.getAbsolutePath());
        helper.writeExifData();

        return file;
    }

    private static Bitmap replaceWith(Bitmap.Config config, Bitmap bitmap) {
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
        bitmap.recycle();
        return outputBitmap;
    }

    public static File saveSmallImage(Context context, File file) throws IOException {
//        int height = context.getResources().getDisplayMetrics().heightPixels;
//        int width = context.getResources().getDisplayMetrics().widthPixels;
//        int smallSide = width > height ? height : width;
//        Bitmap bitmap = decodeSampledBitmap(file, (int) (smallSide * SMALL_IMAGE_RATIO), (int) (smallSide * SMALL_IMAGE_RATIO));
        Bitmap bitmap = decodeSampledBitmap(file, MAX_LENGTH, MAX_LENGTH);

        ExifHelper helper = new ExifHelper();
        helper.createInFile(file.getAbsolutePath());
        helper.readExifData();

        File smallFile = newImageFile();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(smallFile));
        bitmap.recycle();
        helper.resetOrientation();

        helper.createOutFile(smallFile.getAbsolutePath());
        helper.writeExifData();

        return smallFile;
    }

    public static Bitmap decodeSampledBitmap(File file, int reqWidth, int reqHeight) throws FileNotFoundException {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(file), null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(new FileInputStream(file), null, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public enum SaveType {
        JPEG, ARGB, RGB
    }
}
