package com.mchapagai.simplepaint;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

/**
 * @author mchapagai
 *         Handles the drawing events.
 */
public class MainActivity extends AppCompatActivity implements OnClickListener {

    private ImageView eraser;
    private Button chooseImageButton;
    private ImageButton btnClear, btnSave;
    private PaintingView paintingView;
    private static final int SELECT_PHOTO = 100;

    /**
     * @param savedInstanceState Get XML attributes from "res/layout" directory
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_main);
            paintingView = (PaintingView) findViewById(R.id.painting);

            chooseImageButton = (Button) findViewById(R.id.btnChoose);
            chooseImageButton.setOnClickListener(this);

            btnClear = (ImageButton) findViewById(R.id.btnClear);
            btnClear.setOnClickListener(this);

            btnSave = (ImageButton) findViewById(R.id.btnSave);
            btnSave.setOnClickListener(this);

            eraser = (ImageView) findViewById(R.id.eraser);
            eraser.setOnClickListener(this);

        }

    }

    /*
     * Inflate the menu
     * This adds items to the action bar if it is present
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * Override onOptionsItemSelected() method to achieve click events
     * Handling ActionBar icon click events
     * Take appropriate action for each action item click
     */


    @TargetApi(JELLY_BEAN)
    @Override
    public void onClick(View v) {
        if (v == eraser) {
            if (paintingView.isEraserActive()) {
                paintingView.deactivateEraser();
                eraser.setImageResource(R.drawable.eraser);
            } else {
                paintingView.activateEraser();
                eraser.setImageResource(R.drawable.pencil);
            }
        } else if (v == btnClear) {
            paintingView.reset();
            paintingView.setBackground(null);
        } else if (v == btnSave) {
            saveImage();
        } else if (v == chooseImageButton) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PHOTO);
        }
    }

    public File saveImage() {
        paintingView.setDrawingCacheEnabled(true);
        Bitmap bitmap = paintingView.getDrawingCache();

        File filePath = Environment.getExternalStorageDirectory();
        File file = new File(filePath, UUID.randomUUID().toString() + ".png");

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            Toast.makeText(getApplicationContext(), "Successfully saved image.", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return file;
    }


    // select picture from the device for paint
    @TargetApi(JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);

                paintingView.setBackground(ob);
                paintingView.setBackgroundDrawable(ob);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

}