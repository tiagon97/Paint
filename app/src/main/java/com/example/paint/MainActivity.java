package com.example.paint;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PaintView paintView;
    private int defaultColor;
    private int STORAGE_PERMISSION_CODE = 1;
    private int seekBarProgress=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button button;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = findViewById(R.id.paintView);
        button = findViewById(R.id.change_color_button);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        SeekBar seekBar = findViewById(R.id.seekBar);
        final TextView textView = findViewById(R.id.current_pen_size);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        paintView.initialise(displayMetrics);
        if(seekBar.getProgress() == 0){seekBarProgress=1; }else{seekBarProgress=seekBar.getProgress();}
        textView.setText("Rozmiar: " + seekBarProgress);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openColourPicker();

            }

        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar.getProgress()==0){seekBarProgress=1;}else{seekBarProgress=seekBar.getProgress();}
                paintView.setStrokeWidth(seekBarProgress);
                textView.setText("Rozmiar: " + seekBarProgress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ImageButton clear = findViewById(R.id.ClearButton);
        clear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ClearButton:{
                paintView.clear();
                break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);

    }

    private void requestStoragePermission () {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Needed to save image")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }

                    })
                    .create().show();

        } else {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Dostep przyznany", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "Dostep odrzucony", Toast.LENGTH_LONG).show();

            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.clear_button:
                paintView.clear();
                return true;
            case R.id.save_button:

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestStoragePermission();

                }
                paintView.saveImage();
                return true;

            case R.id.undo_button:
                paintView.undo();
                return true;
            case R.id.redo_button:
                paintView.redo();
                return true;
            case R.id.brush_button:
                paintView.drawOption.setBrush_active();
                return true;
            case R.id.action_line:
                paintView.drawOption.setLine_active();
                return true;
            case R.id.action_rect:
                paintView.drawOption.setRect_active();
                return true;
            case R.id.action_square:
                paintView.drawOption.setSquare_active();
                return true;
            case R.id.action_circle:
                paintView.drawOption.setCircle_active();
                return true;
            case R.id.Styl1:
                paintView.normal();
                return true;
            case R.id.Styl2:
                paintView.emboss();
                return true;
            case R.id.Styl3:
                paintView.blur();
                return true;
            case R.id.action_style:
                paintView.setStyle();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void openColourPicker () {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

                Toast.makeText(MainActivity.this, "Nie pozwoliles mi zmienic koloru", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                defaultColor = color;

                paintView.setColor(color);

            }

        });

        ambilWarnaDialog.show();

    }
}
