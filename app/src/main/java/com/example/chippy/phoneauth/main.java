package com.example.chippy.phoneauth;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.content.Intent;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.io.IOException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import android.location.Location;
import android.app.ProgressDialog;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;

import java.util.Calendar;



import com.google.firebase.auth.FirebaseAuth;

public class main extends Activity{



    private Camera myCamera;
    private MyCameraSurfaceView myCameraSurfaceView;
    private MediaRecorder mediaRecorder;
    public FirebaseAuth mAuth;
    Button myButton;
    SurfaceHolder surfaceHolder;
    boolean recording;
    String need ="start";
    EditText editText;
    ImageButton button;

    //-------------------------------------

//    private Button mUploadBtn;
//    private final int VIDEO_REQUEST_CODE=100;
    Calendar calander;
    SimpleDateFormat simpledateformat;
    //private final int VIDEO_REQUEST_CODE=100;
    String Date;
     public String phone="9888888888",mFileName;
    String child;
    double lat,lon;
    Context mContext;
    Intent data;
    private StorageReference mStorage;

    DatabaseReference databaseRef;
    FirebaseDatabase fDatabase;
    GPStracker g;
    private ProgressDialog mProgress;

    //--------------------------------------
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recording = false;

        setContentView(R.layout.activity_main2);

        mStorage = FirebaseStorage.getInstance().getReference();
        fDatabase = FirebaseDatabase.getInstance();
        myButton = (Button) findViewById(R.id.mybutton);
        mProgress = new ProgressDialog(this);
        g = new GPStracker(getApplicationContext());
        //Get Camera for preview
        myCamera = getCameraInstance();

        if(myCamera == null){
            Toast.makeText(main.this,
                    "Fail to get Camera",
                    Toast.LENGTH_LONG).show();
        }

        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
        FrameLayout myCameraPreview = (FrameLayout)findViewById(R.id.videoview);
        myCameraPreview.addView(myCameraSurfaceView);

        myCamera.setDisplayOrientation(90);
     //   phone=mAuth.getCurrentUser().getPhoneNumber().toString();
        myButton = (Button)findViewById(R.id.mybutton);

        myButton.setOnClickListener(myButtonOnClickListener);
        final EditText editText = findViewById(R.id.editText);
        final ImageButton button = findViewById(R.id.button);
        editText.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);

        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    editText.setText(matches.get(0));
                String results = matches.get(0);
                Toast.makeText(main.this,results, Toast.LENGTH_LONG).show();
                if(matches.get(0).equals("start"))
                {
                    //Toast.makeText(main.this, "hai", Toast.LENGTH_LONG).show();
                    editText.setVisibility(View.INVISIBLE);



                    //function
                    myCamera.setDisplayOrientation(90);
                    try{
                        if(recording){
                            // stop recording and release camera
                            mediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object





                            //Exit after saved
                            //finish();
                          calander = Calendar.getInstance();
                            simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            Date = simpledateformat.format(calander.getTime());
                            // GPStracker g = new GPStracker(getApplicationContext());
                            Location l = g.getLocation();
                            if(l!=null) {
                                lat = l.getLatitude();
                                lon = l.getLongitude();

                            }
                            Toast.makeText(getApplicationContext(),"LAT:"+lat+"\n LONG:"+lon,Toast.LENGTH_LONG).show();

                            // release the MediaRecorder object
                            String b = String.valueOf(Environment.getExternalStorageDirectory());
                            final String path = b+"/"+mFileName;
                            Uri uri = Uri.fromFile(new File(path));

                            StorageReference filepath = mStorage.child(mFileName);
                            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                    mProgress.setMessage("Uploading video...");
                                    mProgress.show();
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    databaseRef = fDatabase.getReference("users").child(phone+Date);
                                    databaseRef.child("date").setValue(Date);
                                    databaseRef.child("latitude").setValue(lat);
                                    databaseRef.child("longitude").setValue(lon);
                                    databaseRef.child("phone").setValue(phone);
                                    databaseRef.child("videourl").setValue(String.valueOf(downloadUrl));
                                    mProgress.dismiss();}
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure( Exception exception) {
                                            // Handle unsuccessful uploads
                                            Toast.makeText(main.this,mFileName, Toast.LENGTH_LONG).show();
                                        }
                                    });




                            //   button.setVisibility(View.VISIBLE);
                            myButton.setText("Start");


                            recording = false;
                            //editText.setVisibility(View.VISIBLE);

                        }else{

                            //Release Camera before MediaRecorder start
                            releaseCamera();

                            if(!prepareMediaRecorder()){
                                Toast.makeText(main.this,
                                        "Fail in prepareMediaRecorder()!\n - Ended -",
                                        Toast.LENGTH_LONG).show();

                                finish();
                            }
                            //  button.setVisibility(View.VISIBLE);

                            mediaRecorder.start();
                            recording = true;
                            myButton.setText("Stop");
                            Toast.makeText(main.this, "Recording", Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }}



            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        Toast.makeText(main.this, "SAY START", Toast.LENGTH_LONG).show();
        new CountDownTimer(3000,10) {
            @Override

            public void onTick(long millisUntilFinished) {

                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                editText.setText("");
                editText.setHint("Listening...");

            }

            @Override
            public void onFinish() {

                mSpeechRecognizer.stopListening();
                editText.setHint("You will see input here");
            }
        }.start();


        //----------------------------------------------
    }



    Button.OnClickListener myButtonOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            myCamera.setDisplayOrientation(90);
            try{
                if(recording) {
                    // stop recording and release camera

                    //gps get location date and time
                    calander = Calendar.getInstance();
                    simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date = simpledateformat.format(calander.getTime());
                    // GPStracker g = new GPStracker(getApplicationContext());
                    Location l = g.getLocation();
                    if (l != null) {
                        lat = l.getLatitude();
                        lon = l.getLongitude();
                    }
                    Toast.makeText(getApplicationContext(), "LAT:" + lat + "\n LONG:" + lon, Toast.LENGTH_LONG).show();





                    mediaRecorder.stop();  // stop the recording
                    releaseMediaRecorder(); // release the MediaRecorder object
                   String b = String.valueOf(Environment.getExternalStorageDirectory());
                    final String path = b+"/"+mFileName;
                    Uri uri = Uri.fromFile(new File(path));

                   // StorageReference filepath = mStorage.child(uri.getLastPathSegment());


                    StorageReference filepath = mStorage.child(mFileName);
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            mProgress.setMessage("Uploading video...");
                            mProgress.show();
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            databaseRef = fDatabase.getReference("users").child(phone+Date);
                            databaseRef.child("date").setValue(Date);
                            databaseRef.child("latitude").setValue(lat);
                            databaseRef.child("longitude").setValue(lon);
                            databaseRef.child("phone").setValue(phone);
                            databaseRef.child("videourl").setValue(String.valueOf(downloadUrl));
                            mProgress.dismiss();}
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure( Exception exception) {
                                    // Handle unsuccessful uploads
                                    Toast.makeText(main.this,mFileName, Toast.LENGTH_LONG).show();
                                }
                            });

                    myButton.setText("Start");
                    Toast.makeText(main.this, "Recording saved", Toast.LENGTH_LONG).show();



                    recording = false;
                }else{

                    //Release Camera before MediaRecorder start
                    releaseCamera();


                    if(!prepareMediaRecorder()){
                        Toast.makeText(main.this,
                                "Fail in prepareMediaRecorder()!\n - Ended -",
                                Toast.LENGTH_LONG).show();
//
                        finish();
                    }


                    mediaRecorder.start();
                    recording = true;
                    myButton.setText("Stop");
                    Toast.makeText(main.this, "Recording", Toast.LENGTH_LONG).show();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }};


    private Camera getCameraInstance(){
        // TODO Auto-generated method stub
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private String getFileName_CustomFormat() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd__HH_mm_ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }


    private boolean prepareMediaRecorder(){
        myCamera = getCameraInstance();
        myCamera.setDisplayOrientation(90);
        mediaRecorder = new MediaRecorder();

        myCamera.unlock();
        mediaRecorder.setCamera(myCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        mFileName= getFileName_CustomFormat()+ ".mp4";

        mediaRecorder.setOutputFile("/sdcard/"+ mFileName);




      //  mediaRecorder.setOutputFile(mFileName);

        //mediaRecorder.setOutputFile("/sdcard/myvideo1.mp4");
        mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M

        mediaRecorder.setPreviewDisplay(myCameraSurfaceView.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = new MediaRecorder();
            myCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (myCamera != null){
            myCamera.release();
            // release the camera for other applications
            myCamera = null;
        }
    }

    public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

        private SurfaceHolder mHolder;
        private Camera mCamera;

        public MyCameraSurfaceView(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int weight,
                                   int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // make any resize, rotate or reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub

        }
    }
}