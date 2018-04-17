package com.example.athira.vedio;

import android.content.Intent;
import android.location.Location;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private   Button location;
  private EditText phn;
    private FirebaseUser user;
  FirebaseDatabase database;
  DatabaseReference myref;
    Calendar calander;
    SimpleDateFormat simpledateformat;
    String Date;

    //this is a comment to test githun

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_main);
        location =(Button) findViewById(R.id.loc);
        phn =(EditText) findViewById(R.id.phone);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calander = Calendar.getInstance();
                simpledateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date = simpledateformat.format(calander.getTime());

                GPStracker g= new GPStracker(getApplicationContext());
                Location l = g.getLocation();
                if(l!=null)
                {
                    String child = phn.getText().toString();
                    double lat = l.getLatitude();
                    double lon = l.getLongitude();
                    myref = database.getReference("users").child(child);
                    myref.child("latitude").setValue(lat);
                    myref.child("longitude").setValue(lon);
                    myref.child("date").setValue(Date);

                    
                    Toast.makeText(getApplicationContext(),"LAT:"+lat+"\n LONG:"+lon,Toast.LENGTH_LONG).show();

                }


            }
        });

    }
}
