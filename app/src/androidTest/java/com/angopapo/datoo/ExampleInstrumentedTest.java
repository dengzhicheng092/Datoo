package com.angopapo.datoo;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.parse.ParseException;
import com.parse.SaveCallback;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.angopapo.datoo", appContext.getPackageName());
    }

    // Method to save multiple images

    gameScore.addAllUnique("photos", Arrays.asList(parsefilephotoobject1, parsefilephotoobject2,parsefilephotoobject3));
    gameScore.saveInBackground();

    // Example

    if (destination_profile != null) {
        Glide.with(getActivity()).load(destination_profile.getAbsolutePath()).asBitmap().toBytes().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(new SimpleTarget<byte[]>() {
            @Override
            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {


                final ParseFile parseFile = new ParseFile(destination_profile.getName(), resource);
                parseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        gameScore.addUnique("Photos", parseFile);
                        gameScore.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                showToast("Profile image upload success");
                            }
                        });
                    }
                });


            }
        });
    }

    // In model to get Photos list
    public List<ParseFile> getParseFilePhotos() {
        return getList("Photos");
    }


    ///////////////////////////////////////////////////////////////////////////////////////

    //This is how I store ParseFile List into ParseObject

    ParseObject pObject = new ParseObject();
    ArrayList<ParseFile> pFileList = new ArrayList<ParseFile>();
    for (String thumbPath : thumbList) {
        byte[] imgData = convertFileToByteArray(thumbPath);
        ParseFile pFile = new ParseFile("mediaFiles",imgData);
        pFileList.add(pFile);
    }

       pObject.addAll("mediaFiles", pFileList);
       pObject.saveEventually();

   // This is how i retrieve it and get the first image from the list

    List<ParseFile> pFileList = (ArrayList<ParseFile>) pObject.get("mediaFiles");
    if (!pFileList.isEmpty()) {
        ParseFile pFile = pFileList.get(0);
        byte[] bitmapdata = pFile.getData();  // here it throws error
        bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
    }
}
