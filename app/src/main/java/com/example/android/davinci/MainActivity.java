package com.example.android.davinci;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.android.davinci.view.DaVinciView;

public class MainActivity extends AppCompatActivity {

    //private PikassoView daVinciView;
    private DaVinciView daVinciView;
    private AlertDialog.Builder currentAlerDialog;
    private ImageView widthImageView;
    private AlertDialog dialogLineWidth;
    private AlertDialog colorDialog;

    private SeekBar aplhaSeekBar;
    private SeekBar redSeekBar ;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar ;
    private View colorView;

    // Rectangle
    private AlertDialog rectangleDialog;
    private SeekBar rectWidthSeekBar;
    private SeekBar rectFilledSeekBar;

    // Circle
    private AlertDialog circtangleDialog;
    private SeekBar circWidthSeekBar;
    private SeekBar circFilledSeekBar;

    // Shake Detector
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        daVinciView = findViewById(R.id.view);

        // com.example.android.davinci.ShakeDetector
        // com.example.android.davinci.ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */

                daVinciView.clear();
                //Toast.makeText(getBaseContext(),"Shaking",Toast.LENGTH_LONG).show();
            }


        });

    }

    // Part of the shake-detection process
    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        return true;



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.clearId:
                daVinciView.clear();
                break;

            case R.id.saveId:
                daVinciView.saveToInternalStorage();
                break;

            case R.id.colorId:
                showColorDialog();
                break;

            case R.id.lineWidth:
                showLineWidthDialog();
                break;

            case R.id.eraseId:
                break;

            case R.id.loadId:
                daVinciView.loadImageFromStorage();
                break;

            case R.id.rectId:
                showRectangleDialog();
                break;

            case R.id.circId:
                showCircleDialog();
                break;




        }

        if (item.getItemId() == R.id.clearId) {
            daVinciView.clear();
        }
        return super.onOptionsItemSelected(item);
    }


    void showColorDialog() {

        currentAlerDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog, null);
        aplhaSeekBar = view.findViewById(R.id.alphaSeekBar);
        redSeekBar = view.findViewById(R.id.redSeekBar);
        greenSeekBar = view.findViewById(R.id.greenSeekBar);
        blueSeekBar = view.findViewById(R.id.blueSeekBar);
        colorView = view.findViewById(R.id.colorView);


        //register SeekBar event Listeners
        aplhaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);


        int color = daVinciView.getDrawingColor();
        aplhaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));


        Button setColorButton = view.findViewById(R.id.setColorButton);
        setColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daVinciView.setDrawingColor(Color.argb(
                        aplhaSeekBar.getProgress(),
                        redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress()
                ));

                colorDialog.dismiss();

            }
        });

        currentAlerDialog.setView(view);
        currentAlerDialog.setTitle("Choose Color");
        colorDialog =  currentAlerDialog.create();
        colorDialog.show();



    }


    void showLineWidthDialog() {
        currentAlerDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.width_dialog, null);
        final SeekBar widthSeekbar = view.findViewById(R.id.widthDSeekBar);
        Button setLineWidthButton = view.findViewById(R.id.widthDialogButton);
        widthImageView = view.findViewById(R.id.imageViewId);
        setLineWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daVinciView.setLineWidth(widthSeekbar.getProgress());
                dialogLineWidth.dismiss();
                currentAlerDialog = null;

            }
        });



        widthSeekbar.setOnSeekBarChangeListener(widthSeekBarChange);
        widthSeekbar.setProgress(daVinciView.getLineWidth());


        currentAlerDialog.setView(view);
        dialogLineWidth =  currentAlerDialog.create();
        dialogLineWidth.setTitle("Set Line Width");

        dialogLineWidth.show();

    }


    void showRectangleDialog() {

        currentAlerDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.rectangle_dialog, null);
        aplhaSeekBar = view.findViewById(R.id.alphaSeekBar_rect);
        redSeekBar = view.findViewById(R.id.redSeekBar_rect);
        greenSeekBar = view.findViewById(R.id.greenSeekBar_rect);
        blueSeekBar = view.findViewById(R.id.blueSeekBar_rect);
        rectWidthSeekBar = view.findViewById(R.id.widthSeekbar_rect);
        rectFilledSeekBar = view.findViewById(R.id.filledSeekbar_rect);
        colorView = view.findViewById(R.id.colorView_rect);
        widthImageView = view.findViewById(R.id.imageViewId_rect);


        //register SeekBar event Listeners
        aplhaSeekBar.setOnSeekBarChangeListener(rectSeekBarChange);
        redSeekBar.setOnSeekBarChangeListener(rectSeekBarChange);
        greenSeekBar.setOnSeekBarChangeListener(rectSeekBarChange);
        blueSeekBar.setOnSeekBarChangeListener(rectSeekBarChange);
        rectWidthSeekBar.setOnSeekBarChangeListener(rectSeekBarChange);
        rectFilledSeekBar.setOnSeekBarChangeListener(rectSeekBarChange);
        final SeekBar widthSeekbar = view.findViewById(R.id.widthSeekbar_rect);


        int color = daVinciView.getDrawingColor();
        aplhaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        Button setColorButton = view.findViewById(R.id.setColorButton_rect);
        setColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daVinciView.setRectangle(
                        rectWidthSeekBar.getProgress(),
                        rectFilledSeekBar.getProgress(),
                        aplhaSeekBar.getProgress(),
                        redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress()
                );
                rectangleDialog.dismiss();
                currentAlerDialog = null;

            }
        });

        currentAlerDialog.setView(view);
        currentAlerDialog.setTitle("Define your Rectangle");
        rectangleDialog =  currentAlerDialog.create();
        rectangleDialog.show();

    }


    void showCircleDialog() {

        currentAlerDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.circle_dialog, null);
        aplhaSeekBar = view.findViewById(R.id.alphaSeekBar_circ);
        redSeekBar = view.findViewById(R.id.redSeekBar_circ);
        greenSeekBar = view.findViewById(R.id.greenSeekBar_circ);
        blueSeekBar = view.findViewById(R.id.blueSeekBar_circ);
        circWidthSeekBar = view.findViewById(R.id.widthSeekbar_circ);
        circFilledSeekBar = view.findViewById(R.id.filledSeekbar_circ);
        colorView = view.findViewById(R.id.colorView_circ);
        widthImageView = view.findViewById(R.id.imageViewId_circ);


        //register SeekBar event Listeners
        aplhaSeekBar.setOnSeekBarChangeListener(circSeekBarChange);
        redSeekBar.setOnSeekBarChangeListener(circSeekBarChange);
        greenSeekBar.setOnSeekBarChangeListener(circSeekBarChange);
        blueSeekBar.setOnSeekBarChangeListener(circSeekBarChange);
        circWidthSeekBar.setOnSeekBarChangeListener(circSeekBarChange);
        circFilledSeekBar.setOnSeekBarChangeListener(circSeekBarChange);
        final SeekBar widthSeekbar = view.findViewById(R.id.widthSeekbar_circ);


        int color = daVinciView.getDrawingColor();
        aplhaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        Button setColorButton = view.findViewById(R.id.setColorButton_circ);
        setColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daVinciView.setCircle(
                        circWidthSeekBar.getProgress(),
                        circFilledSeekBar.getProgress(),
                        aplhaSeekBar.getProgress(),
                        redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress()
                );
                circtangleDialog.dismiss();
                currentAlerDialog = null;

            }
        });

        currentAlerDialog.setView(view);
        currentAlerDialog.setTitle("Define your Circle");
        circtangleDialog =  currentAlerDialog.create();
        circtangleDialog.show();

    }



    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            /*daVinciView.setBackgroundColor(Color.argb(
                    aplhaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));*/

            //display the current color
            colorView.setBackgroundColor(Color.argb(
                    aplhaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener widthSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            Paint p = new Paint();
            p.setColor(daVinciView.getDrawingColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);


            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30, 50, 370, 50, p);
            widthImageView.setImageBitmap(bitmap);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    private SeekBar.OnSeekBarChangeListener rectSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Paint p = new Paint();

            colorView.setBackgroundColor(Color.argb(
                    aplhaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));

            p.setColor(Color.argb(
                    aplhaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));

            // Being filled
            int filledNumber = rectFilledSeekBar.getProgress();
            if (filledNumber > 128){
                p.setStyle(Paint.Style.FILL_AND_STROKE);
            } else {
                p.setStyle(Paint.Style.STROKE);
            }

            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(rectWidthSeekBar.getProgress());
            //p.setStyle(Paint.Style.STROKE);

            bitmap.eraseColor(Color.WHITE);
            //canvas.drawLine(30, 50, 370, 50, p);
            canvas.drawRect(100,200,200,100,p);
            widthImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    private SeekBar.OnSeekBarChangeListener circSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Paint p = new Paint();

            colorView.setBackgroundColor(Color.argb(
                    aplhaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));

            p.setColor(Color.argb(
                    aplhaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));

            // Being filled
            int filledNumber = circFilledSeekBar.getProgress();
            if (filledNumber > 128){
                p.setStyle(Paint.Style.FILL_AND_STROKE);
            } else {
                p.setStyle(Paint.Style.STROKE);
            }

            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(circWidthSeekBar.getProgress());
            //p.setStyle(Paint.Style.STROKE);

            bitmap.eraseColor(Color.WHITE);
            //canvas.drawLine(30, 50, 370, 50, p);
            //canvas.drawRect(100,200,200,100,p);
            canvas.drawCircle(150,150,50,p);
            widthImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


}
