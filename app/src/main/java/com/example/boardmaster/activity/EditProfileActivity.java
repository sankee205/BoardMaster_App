package com.example.boardmaster.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boardmaster.CurrentUser;
import com.example.boardmaster.R;
import com.example.boardmaster.retrofit.ApiClient;
import com.example.boardmaster.retrofit.ExifUtil;
import com.example.boardmaster.retrofit.JsonPlaceHolderApi;
import com.example.boardmaster.retrofit.Utility;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.boardmaster.activity.RegisterProfileActivity.isValidEmail;

/**
 * this class edits an already existing users profile details
 */
public class EditProfileActivity extends AppCompatActivity {
    JsonPlaceHolderApi api = ApiClient.getClient().create(JsonPlaceHolderApi.class);
    TextView backButton, mProfilePictureText;
    EditText mFirstname, mLastname, mUsername, mEmail, mPassword;

    ImageView imageView;
    Button addBook;
    ImageButton takePhoto;

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    String userChoosenTask;

    private static String imagePath;

    List<File> photoFiles = new ArrayList<>();
    File currentPhoto;

    private CurrentUser currentUser = CurrentUser.getInstance();

    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);

        mFirstname = findViewById(R.id.editProfileFirstName);
        mLastname = findViewById(R.id.editProfileLastName);
        mUsername = findViewById(R.id.editProfileUserName);
        mEmail = findViewById(R.id.editProfileEmail);
        mPassword = findViewById((R.id.editProfilePassword));
        imageView = findViewById(R.id.editProfileImage);

        addBook = findViewById(R.id.editProfileSaveButton);
        takePhoto = findViewById(R.id.editProfileImageButton);
        backButton = findViewById(R.id.editProfileCancelButton);
        mProfilePictureText = findViewById(R.id.editProfilePictureText);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        getInfo();

        mFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!mFirstname.getText().toString().matches("^[A-Za-z]+$") ||  mFirstname.getText().toString().length()<=2){
                    mFirstname.setTextColor(getResources().getColor(R.color.red));
                }
                else{
                    mFirstname.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        mLastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!mLastname.getText().toString().matches("^[A-Za-z]+$") ||  mLastname.getText().toString().length()<=2){
                    mLastname.setTextColor(getResources().getColor(R.color.red));
                }
                else{
                    mLastname.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!mUsername.getText().toString().matches("^[A-Za-z]+$") ||  mUsername.getText().toString().length()<=6){
                    mUsername.setTextColor(getResources().getColor(R.color.red));

                }
                else{
                    mUsername.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!isValidEmail(mEmail.getText().toString())){
                    mEmail.setTextColor(getResources().getColor(R.color.red));

                }
                else{
                    mEmail.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!isValidPassword(mPassword.getText())){
                    mPassword.setTextColor(getResources().getColor(R.color.red));

                }
                else{
                    mPassword.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });


        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImagesMethod();
            }
        });

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String firstname = mFirstname.getText().toString();
                String lastname = mLastname.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(!validateInputs()){
                    editProfile(username, firstname, lastname, email, password, null);
                }
                else{
                    Toast.makeText(EditProfileActivity.this,"Not valid input in the form", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    /**
     * retrives the info of the current user
     */
    public void getInfo(){
        String userToken = CurrentUser.getInstance().getToken();
        Call<Object> call= api.currentUser(userToken);

        call.enqueue(new Callback<Object>(){
            @Override
            public void onResponse(Call<Object>call, Response<Object> response){
                if(!response.isSuccessful()){
                    System.out.println("code:"+response.code());
                    return;
                }
                String json=new Gson().toJson(response.body());
                try{
                    JSONObject jsonObject=new JSONObject(json);
                    String firstname = jsonObject.getString("firstname");
                    String lastname = jsonObject.getString("lastname") ;
                    String uname = jsonObject.getString("username");
                    String email = jsonObject.getString("email");
                    if(jsonObject.getJSONArray("profileImages").length()>0){
                        String id = jsonObject.getJSONArray("profileImages").getJSONObject(0).getString("id");
                        getFirebasePhoto(id);
                    }
                    else{
                        imageView.setImageResource(R.drawable.icon_profile_foreground);
                        mProfilePictureText.setText("Add profile picture");
                    }
                    mFirstname.setText(firstname);
                    mLastname.setText(lastname);
                    mUsername.setText(uname);
                    mEmail.setText(email);
                }catch(JSONException e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<Object>call,Throwable t){
            }
        });
    }


    /**
     * sends a request to the server with the new details of the current suser
     * @param username
     * @param firstname
     * @param lastname
     * @param email
     * @param password
     * @param imagePath
     */
    public void editProfile(String username,String firstname, String lastname, String email, String password, String imagePath) {
        Call<ResponseBody> call;
        Map<String, RequestBody> itemsData = new HashMap<>();

        itemsData.put("firstname", createPartFromString(firstname));
        itemsData.put("lastname", createPartFromString(lastname));
        itemsData.put("username", createPartFromString(username));
        itemsData.put("email", createPartFromString(email));
        if(password.isEmpty()){
        }
        else{
            itemsData.put("password", createPartFromString(password));
        }
        if (imagePath == null) {
            call = api.editUser(currentUser.getToken(), itemsData);
        }
        else {
            File file = new File(imagePath);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);

            call = api.editUserAndPhoto(currentUser.getToken(), itemsData, body);
        }

        call.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                if(!response.isSuccessful()){
                    System.out.println("code:"+response.code());
                    Toast.makeText(EditProfileActivity.this,"Saving changes failed", Toast.LENGTH_SHORT).show();

                }
                if(response.isSuccessful()){
                    String somResponse = response.body().toString();
                    Toast.makeText(EditProfileActivity.this,"Changes successfully saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call,Throwable t){
            }
        });
    }
    private RequestBody createPartFromString (String partString) {
        return RequestBody.create(MultipartBody.FORM, partString);
    }



    private void selectImagesMethod() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(EditProfileActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE){
                onSelectFromGalleryResult(data);
            }
            else if (requestCode == REQUEST_CAMERA){
                onCaptureImageResult(data);

            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(bm);
    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        Uri tempUri = getImageUri(getApplicationContext(), thumbnail);

        imagePath = getRealPathFromURI(tempUri);
        File finalFile = new File(imagePath);

        Bitmap finalimg = ExifUtil.rotateBitmap(finalFile.toString(), thumbnail);
        imageView.setImageBitmap(thumbnail);
    }







    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }




    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void getFirebasePhoto(String id){
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageReference image = mStorageRef.child("images/" + id);

        image.getBytes(1024*1024*5).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * checks if the email is valid
     * @param target
     * @return
     */
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    /**
     * checks if the password is valid
     * @param target
     * @return
     */
    public static boolean isValidPassword(CharSequence target) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(target);

        return matcher.matches();
    }

    /**
     * checks if all the inputs are valid
     * if and only if all are valid, it returns false.
     * if one or more of the inputs are unvalid it returns true
     * @return
     */
    public boolean validateInputs(){
        boolean errors = false;
        if(!isValidEmail(mEmail.getText())){
            errors = true;
        }

        if(!isValidPassword(mPassword.getText()) && mPassword.getText().toString().length() > 8){
            errors = true;
        }

        if(!mFirstname.getText().toString().matches("^[A-Za-z]+$") && mFirstname.getText().toString().length()>=2){
            errors = true;
        }

        if(!mLastname.getText().toString().matches("^[A-Za-z]+$") && mLastname.getText().toString().length()>=2){
            errors = true;
        }

        if(!mUsername.getText().toString().matches("^[A-Za-z]+$") && mUsername.getText().toString().length()>=6){
            errors = true;
        }

        return errors;
    }
}