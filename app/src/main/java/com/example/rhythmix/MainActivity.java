package com.example.rhythmix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ListView listView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        Dexter.withContext(this)
//                .withPermission(Manifest.permission.READ_MEDIA_AUDIO)
                .withPermission(Manifest.permission.READ_MEDIA_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Runtime permission given", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs=fetchSongs(Environment.getExternalStorageDirectory());
                        String [] items=new String[mySongs.size()];
                        for(int i=0;i<mySongs.size();i++){
                            items[i]=mySongs.get(i).getName().replace(".mp3", "");
                        }
                        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent=new Intent(MainActivity.this, PlaySong.class);
                                String currentSong=listView.getItemAtPosition(i).toString();
                                intent.putExtra("songList", mySongs);
                                intent.putExtra("currentSong", currentSong);
                                intent.putExtra("position", i);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
        public ArrayList<File> fetchSongs(File file){
            ArrayList arrayList=new ArrayList<>();
            File[] songs =file.listFiles();
            if(songs !=null){
                for(File myFile:songs){
                    if(!myFile.isHidden() && myFile.isDirectory()){
                        arrayList.addAll(fetchSongs(myFile));
                    }
                    else{
                        if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                            arrayList.add(myFile);
                        }
                    }
                }
            }
            return arrayList;

        }
    }
