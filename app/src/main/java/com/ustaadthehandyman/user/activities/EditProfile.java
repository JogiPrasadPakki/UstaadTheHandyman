package com.ustaadthehandyman.user.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.exifinterface.media.ExifInterface;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.UpdateUser;
import com.ustaadthehandyman.user.util.EventHandler;
import com.ustaadthehandyman.user.util.GlobalFields;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {

    ImageView profileIcon;
    AlertDialog popup;
    Uri imageUri;
    String Imagepath, compressedImage = null;
    Button saveChanges;
    EditText profileName,prifieNumber;
    ProgressBar progressBar;
    UpdateUser updateUser;
    ProgressDialog progressDialog;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            finish();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();

        updateUser = APIInitialize.uploadeProfile();
        profileIcon = findViewById(R.id.img_edit_profile);
        saveChanges = findViewById(R.id.btn_edit_profile_save);
        profileName = findViewById(R.id.et_profile_edit_name);
        prifieNumber = findViewById(R.id.et_profile_edit_mobile);
        progressBar = findViewById(R.id.pb_update_user);

        String name = preferences.getString("user-name",null);
        String img = preferences.getString("user-image",null);

        if(name != null)
            profileName.setText(name);
        if(img != null){
            byte[] bytes = Base64.decode(img,Base64.DEFAULT);
            Bitmap bitmap =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            profileIcon.setImageBitmap(bitmap);
        }
        prifieNumber.setText(preferences.getString("user-mobile",null));
        prifieNumber.setFocusable(false);

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder popUpBuilder = new AlertDialog.Builder(EditProfile.this);
                View popUpLayout = getLayoutInflater().inflate(R.layout.img_selector_layout,null);
                CardView takeCamera = (CardView)popUpLayout.findViewById(R.id.btn_select_camera);
                CardView takeGallary = (CardView)popUpLayout.findViewById(R.id.btn_select_gallary);
                takeCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent galleryIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(galleryIntent,102);
                        popup.dismiss();

                    }
                });

                takeGallary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent cameraIntent = new Intent();
                        cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                        cameraIntent.setType("image/*");
                        startActivityForResult(cameraIntent,103);
                        popup.dismiss();
                    }
                });
                popUpBuilder.setView(popUpLayout);

                popup = popUpBuilder.create();
                popup.show();
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileName.getText().toString().trim() == ""){
                    profileName.setError("Please enter name");
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressDialog = new ProgressDialog(EditProfile.this,ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("Updating Profile");
                    progressDialog.show();
                }
                SharedPreferences  preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String tokenValue = preferences.getString("auth-token",null);
                final File file = new File(Uri.parse(compressedImage).getPath());
                String uid = FirebaseAuth.getInstance().getUid();
                String name = profileName.getText().toString().trim();
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image",file.getName(), requestFile);
                Map<String,String> token = new HashMap<>();
                token.put("authentication","barer "+tokenValue);
                updateUser.UpdateUser(token,uid,name,body).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream .toByteArray();
                            String encodedImg = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            editor.putString("user-image",encodedImg);
                            editor.putString("user-name",profileName.getText().toString().trim());
                            editor.apply();
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                progressBar.setVisibility(View.GONE);
                            }else {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(),"Profile Updated successfully",Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new EventHandler(GlobalFields.ProfileUpdated));
                        }else{
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                progressBar.setVisibility(View.GONE);
                            }else {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(),"Something went wrong try again",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case 103:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {

                    imageUri = data.getData();

                    CropImage.activity(imageUri)
                            .setActivityTitle(getString(R.string.edit_image))
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setAspectRatio(1,1)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                }
                break;

            case 102:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {

                    imageUri = data.getData();

                    CropImage.activity(imageUri)
                            .setActivityTitle(getString(R.string.edit_image))
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setAspectRatio(1,1)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            //.setBackgroundColor(getColor(R.color.black))
                            .start(this);
                }
                break;
        }

        //get uri from croped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageUri = resultUri;
                compressedImage =  compressImage(imageUri.toString());
                Log.d("IMG",compressedImage);
                Log.d("IMG",Uri.parse(compressedImage).toString());
                profileIcon.setImageURI(Uri.parse(compressedImage));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventHandler event){
        if(event.getEventId() == GlobalFields.LoginSuccess){

        }
    }

    //Image Compressar
    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//		by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//		you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//		max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//		width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) { 				imgRatio = maxHeight / actualHeight; 				actualWidth = (int) (imgRatio * actualWidth); 				actualHeight = (int) maxHeight; 			} else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//		setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//		inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//		this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//			load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//		check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//			write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }
    private String getFilename(){
        File file = new File(Environment.getExternalStorageDirectory().getPath(),"Ustaad/profileImages");
        if(!file.exists()){
            file.mkdirs();

        }
        String uriString = (file.getAbsolutePath()+"/"+ System.currentTimeMillis()+".jpg");
        Imagepath = uriString;
        return uriString;
    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}
