package com.androboot.heyhungryinternship2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    Button button_filePicker;
    Intent accessFileManagerIntent;
    ImageView image_viewer;
    AlertDialog dialog;
    StorageReference storageReference;
    private static final int PICK_IMAGE_CODE=1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         button_filePicker = findViewById(R.id.button_filePicker);

         dialog = new SpotsDialog.Builder().setContext(this).build();
         image_viewer = findViewById(R.id.image_viewer);

         storageReference= FirebaseStorage.getInstance().getReference("image_upload");


         button_filePicker.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 accessFileManagerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                 accessFileManagerIntent.setType("*/*");
                 startActivityForResult(accessFileManagerIntent,PICK_IMAGE_CODE);

             }
         });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case PICK_IMAGE_CODE:
                if(requestCode==PICK_IMAGE_CODE){
                    dialog.show();
                    String path= data.getData().getPath();
                    UploadTask uploadTask = storageReference.putFile(data.getData());
                    Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Failed",  Toast.LENGTH_SHORT).show();}
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                String url = task.getResult().toString();  //Downloaded direct url to string
                                Log.d("DirectLink",url);
                            }
                        }
                    });
                }else{
                    Toast.makeText(this, "Failed at 1", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
