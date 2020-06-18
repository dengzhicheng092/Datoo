package com.angopapo.datoo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.angopapo.datoo.R;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.modules.camera.ImagePro;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static com.angopapo.datoo.modules.camera.ImagePro.CAMERA_CODE;

public class uploadAc extends AppCompatActivity {


    private ArrayList<Bitmap> bitmapArrayList;
    private ArrayList<String> arrayList = new ArrayList<>() ;

    int count = 0;
    boolean isUploading = false;
    boolean isShowing = false;

    public void uploadPhotos(){

        bitmapArrayList = new ArrayList<>(getBitmapFromURLsOrPath(arrayList));
        for (int i = 0; i < bitmapArrayList.size(); i++) {

            // All Images
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapArrayList.get(i).compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] image = stream.toByteArray();

            // Only first image to save in avatar
            ByteArrayOutputStream streamAvatar = new ByteArrayOutputStream();
            bitmapArrayList.get(0).compress(Bitmap.CompressFormat.JPEG, 60, streamAvatar);
            byte[] imageAvatar = streamAvatar.toByteArray();

            ParseFile fileAvatar  = new ParseFile("avatar.jpg", imageAvatar, "jpeg");

            ParseFile file  = new ParseFile("picture.jpg", image, "jpeg");

            ArrayList<ParseFile> parseFileArrayList  = new ArrayList<>();
            parseFileArrayList.add(file);

            file.saveInBackground((SaveCallback) e -> {

                if (e == null){

                    post.setProfilePhotos(parseFileArrayList);
                    post.setProfilePhoto(fileAvatar);

                    post.saveInBackground(e1 -> {

                        if (e1 == null){

                            count = count+1;

                            Log.v("SAVED_IMAGES", String.valueOf(count));
                            Log.v("SENT_IMAGES", String.valueOf(bitmapArrayList.size()));

                            if (count >= bitmapArrayList.size()){

                                isUploading = false;
                                QuickHelp.hideLoading();
                                QuickHelp.showToast(this, getString(R.string.phot_uploa), false);
                                finish();


                            }

                        } else {

                            count = count+1;

                            Log.v("ERROR_IMAGES", String.valueOf(count));
                            Log.v("SENT_IMAGES", String.valueOf(bitmapArrayList.size()));

                            if (count >= bitmapArrayList.size()){

                                isUploading = false;

                                QuickHelp.hideLoading();
                            }
                        }
                    });
                }
            });

        }
    }

    public static ArrayList<Bitmap> getBitmapFromURLsOrPath (ArrayList<String> UrlsOrPath) {

        //has to be an ArrayList
        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
        //convert from paths to Android friendly Parcelable Uri's
        for (String urlOrPath : UrlsOrPath)
        {


            try {

                if (urlOrPath.contains("http")){

                    URL url = new URL(urlOrPath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);

                    bitmapArrayList.add(myBitmap);

                } else {

                    File file = new File(urlOrPath);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = ARGB_8888;

                    try {
                        bitmapArrayList.add(BitmapFactory.decodeStream(new FileInputStream(file), null, options));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                //return myBitmap;
            } catch (IOException e) {
                // Log exception
                //return null;
            }
        }

        return bitmapArrayList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == RESULT_OK)

            if (requestCode == GALLERY_REQUEST_CODE) {//data.getData return the content URI for the selected Image

                Log.v("GALLEY", "GOT NEW PHOTO");

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePaths = cursor.getString(columnIndex);


                if (filePaths != null && !filePaths.isEmpty()){

                    arrayList.add(filePaths);
                }

                cursor.close();

            }
    }

}
