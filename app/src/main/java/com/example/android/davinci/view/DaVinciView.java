package com.example.android.davinci.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class DaVinciView extends View{


    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;
    private OutputStream outputStream;

    // Rectangle Creation
    float xTouch;
    float yTouch;
    Boolean placeRectangle = false;
    int recWidth;
    int recFilled;
    int recColorAlpha;
    int recColorRed;
    int recColorGreen;
    int recColorBlue;

    // Circle Creation
    Boolean placeCircle = false;
    int circWidth;
    int circFilled;
    int circColorAlpha;
    int circColorRed;
    int circColorGreen;
    int circColorBlue;


    public DaVinciView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();


    }

    void init() {
        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(5);
        paintLine.setStrokeCap(Paint.Cap.ROUND);

        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();
    }

    public void setRectangle(int width, int filled, int colorAlpha, int colorRed, int colorGreen, int colorBlue) {

        recWidth = width;
        recFilled = filled;
        recColorAlpha = colorAlpha;
        recColorRed = colorRed;
        recColorGreen = colorGreen;
        recColorBlue = colorBlue;

        placeRectangle = true;

    }

    public void drawRectangle(float x, float y) {

        int width = recWidth;
        int filled = recFilled;
        int colorAlpha = recColorAlpha;
        int colorRed = recColorRed;
        int colorGreen = recColorGreen;
        int colorBlue = recColorBlue;

        Paint p = new Paint();
        // Color
        p.setColor(Color.argb(colorAlpha, colorRed, colorGreen, colorBlue));
        // Filling
        if (filled > 128) {
            p.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            p.setStyle(Paint.Style.STROKE);
        }
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(width);

        bitmapCanvas.drawRect(x - 50, y + 50, x + 50, y - 50, p);
        invalidate();
    }


    public void setCircle(int width, int filled, int colorAlpha, int colorRed, int colorGreen, int colorBlue) {

        circWidth = width;
        circFilled = filled;
        circColorAlpha = colorAlpha;
        circColorRed = colorRed;
        circColorGreen = colorGreen;
        circColorBlue = colorBlue;

        placeCircle = true;

    }

    public void drawCircle(float x, float y) {

        int width = circWidth;
        int filled = circFilled;
        int colorAlpha = circColorAlpha;
        int colorRed = circColorRed;
        int colorGreen = circColorGreen;
        int colorBlue = circColorBlue;

        Paint p = new Paint();
        // Color
        p.setColor(Color.argb(colorAlpha, colorRed, colorGreen, colorBlue));
        // Filling
        if (filled > 128) {
            p.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            p.setStyle(Paint.Style.STROKE);
        }
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(width);

        bitmapCanvas.drawCircle(x, y, 50, p);
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

        for (Integer key : pathMap.keySet()) {
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked(); // event type;
        int actionIndex = event.getActionIndex(); // pointer ( finger, mouse..)

        //Toast.makeText(getContext(),String.valueOf(xTouch),Toast.LENGTH_SHORT).show();

        //xTouch = event.getX(actionIndex);
        //yTouch = event.getY(actionIndex);

        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_UP) {

            touchStarted(event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(actionIndex));

            xTouch = event.getX(actionIndex);
            yTouch = event.getY(actionIndex);

            if (placeRectangle == true) {
                drawRectangle(xTouch, yTouch);
                placeRectangle = false;
            } else if (placeCircle == true) {
                drawCircle(xTouch, yTouch);
                placeCircle = false;
            }


        } else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP) {

            touchEnded(event.getPointerId(actionIndex));

        } else {
            touchMoved(event);
        }

        invalidate();// redraw the screen
        return true;
    }

    private void touchMoved(MotionEvent event) {

        for (int i = 0; i < event.getPointerCount(); i++) {

            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if (pathMap.containsKey(pointerId)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);


                // Calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);


                // if the distance is significant enough to be considered a movement then...
                if (deltaX >= TOUCH_TOLERANCE ||
                        deltaY >= TOUCH_TOLERANCE) {
                    // move the path to the new location
                    path.quadTo(point.x, point.y,
                            (newX + point.x) / 2,
                            (newY + point.y) / 2);


                    // store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }

    }

    public int getDrawingColor() {
        return paintLine.getColor();
    }

    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();

    }

    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    public void clear() {
        pathMap.clear(); // removes all of the paths
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();// refresh the screen
    }

    private void touchEnded(int pointerId) {
        Path path = pathMap.get(pointerId); // get the corresponding Path
        bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
        path.reset();

    }

    private void touchStarted(float x, float y, int pointerId) {
        Path path; // store the path for given touch
        Point point; // store the last point in path


        if (pathMap.containsKey(pointerId)) {
            path = pathMap.get(pointerId);
            point = previousPointMap.get(pointerId);
        } else {
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointMap.put(pointerId, point);

        }

        // move to the coordinates of the touch
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    public void saveImage() {
        String filename = "Pikasso" + System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");


        // get a URI for the location to save the file
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);


        try {
            OutputStream outputStream =
                    getContext().getContentResolver().openOutputStream(uri);

            // copy the bitmap to the outputstream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // this is our image


            try {
                outputStream.flush();
                outputStream.close();

                Toast message = Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                        message.getYOffset() / 2);
                message.show();


            } catch (IOException e) {

                Toast message = Toast.makeText(getContext(), "Image Not Saved", Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                        message.getYOffset() / 2);
                message.show();
            }
        } catch (FileNotFoundException e) {

            Toast message = Toast.makeText(getContext(), "Image Not Saved", Toast.LENGTH_LONG);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();

            // e.printStackTrace();
        }
    }


    public void saveImageToExternalStorage() {

        //Source: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
        String filename = "Pikasso" + System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");


        // get a URI for the location to save the file
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);


        try {
            outputStream =
                    getContext().getContentResolver().openOutputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + "test.jpeg");

        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);


        try {
            outputStream.flush();
            outputStream.close();

            Toast message = Toast.makeText(getContext(), "Image Saved", Toast.LENGTH_LONG);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getImagePath() {
        ContextWrapper cw = new ContextWrapper(getContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        return directory.getAbsolutePath();
    }

    public void saveToInternalStorage() {
        ContextWrapper cw = new ContextWrapper(getContext());
        String filename = "Pikasso" + System.currentTimeMillis();
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
                Log.d("Image:", directory.getAbsolutePath());
                Toast message = Toast.makeText(getContext(), "Image Saved +" + directory.getAbsolutePath(), Toast.LENGTH_LONG);
                message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                        message.getYOffset() / 2);
                message.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // return directory.getAbsolutePath();
    }

    //public void loadImageFromStorage(String path) {
    public void loadImageFromStorage() {

        try {
            String path = getImagePath();
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            Bitmap mutableB = b.copy(Bitmap.Config.ARGB_8888, true);
            bitmapCanvas.drawBitmap(mutableB, 0, 0, null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
