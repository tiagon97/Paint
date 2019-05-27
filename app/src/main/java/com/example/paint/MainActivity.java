package com.example.paint;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 1;
    private Bitmap currentImage;
    private PaintView paintView;
    private int defaultColor;
    private int STORAGE_PERMISSION_CODE = 1;
    private int seekBarProgress=0;
    boolean fill=false;
    int width,height;
    ImageButton viewBrushOption,brush,line,rect,square,circle,fillbtn,redo, undo, clear,gallery,colour,pen_size;
    TextView text;

    boolean visibility=false;
    String[] style = {"Zwykly", "Blur", "Emboss"};
    ArrayAdapter<String> adapter;
    Spinner sp;
    class MyGlobalListenerClass implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            View v = (View) findViewById(R.id.paintView);
            width = v.getWidth();
            height = v.getHeight();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = findViewById(R.id.paintView);
        pen_size = findViewById(R.id.pensizeButton);
        text = findViewById(R.id.current_pen_size);
        text.setVisibility(View.GONE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        final SeekBar seekBar = findViewById(R.id.seekBar);
        final TextView textView = findViewById(R.id.current_pen_size);
        seekBar.setVisibility(View.GONE);
        paintView.getViewTreeObserver().addOnGlobalLayoutListener(new MyGlobalListenerClass());

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        paintView.initialise(displayMetrics);
        if(seekBar.getProgress() == 0){seekBarProgress=1; }else{seekBarProgress=seekBar.getProgress();}
        textView.setText("" + seekBarProgress);


        sp = findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, style);
        sp.setAdapter(adapter);


        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_menu));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT)
                .build();



        final SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        final ImageView menuOption1 = new ImageView(this);
        final ImageView menuOption2 = new ImageView(this);
        final ImageView menuOption3 = new ImageView(this);



        menuOption1.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gallery));
        menuOption2.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.action_save));
        menuOption3.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.action_share));




        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(menuOption1).build())
                .addSubActionView(rLSubBuilder.setContentView(menuOption2).build())
                .addSubActionView(rLSubBuilder.setContentView(menuOption3).build())
                .attachTo(rightLowerButton)
                .build();



        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

        menuOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();

            }
        });

        menuOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    requestStoragePermission();

                }

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_save,null);
                final EditText eSave = mView.findViewById(R.id.zapiszA);
                Button bZapis = mView.findViewById(R.id.btZapis);
                Button bAnuluj = mView.findViewById(R.id.btAnuluj);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                bZapis.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!eSave.getText().toString().isEmpty())
                            paintView.saveImage(eSave.getText().toString());
                            dialog.dismiss();
                    }
                });

                bAnuluj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();


                //paintView.saveImage();
            }
        });

        menuOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_SHORT).show();

            }
        });




        paintView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if(paintView.fill!=fill) {
                            paintView.setFill();
                        }
                        hideUI();
                        seekBar.setVisibility(View.GONE);
                        text.setVisibility(View.GONE);
                        rightLowerButton.setVisibility(View.GONE);
                        rightLowerMenu.close(true);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        showUI();
                        rightLowerButton.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                return false;
            }
        });

        pen_size.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                if (seekBar.getVisibility() == View.VISIBLE) {
                    seekBar.setVisibility(View.INVISIBLE);
                    text.setVisibility(View.INVISIBLE);

                } else {
                    seekBar.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                }
            }
        });

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN: {
                        seekBar.setVisibility(View.VISIBLE);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        seekBar.setVisibility(View.GONE);
                        text.setVisibility(View.GONE);
                        break;
                    }
                }
                return false;
            }
        });




        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
                  ((TextView)view).setText("");
                switch (i){
                    case 0:
                        paintView.normal();
                        break;
                    case 1:
                        paintView.blur();
                        break;
                    case 2:
                        paintView.emboss();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar.getProgress()==0){seekBarProgress=1;}else{seekBarProgress=seekBar.getProgress();}
                paintView.setStrokeWidth(seekBarProgress);
                textView.setText("" + seekBarProgress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if(visibility){
            if(paintView.isTouch){
                noVisibility();
            }
        }





        viewBrushOption = findViewById(R.id.viewBrushOption);
        brush=findViewById(R.id.brush_btn);
        line=findViewById(R.id.line_btn);
        rect=findViewById(R.id.rect_btn);
        square=findViewById(R.id.square_btn);
        circle=findViewById(R.id.circle_btn);
        fillbtn=findViewById(R.id.fillButton);
        redo=findViewById(R.id.RedoButton);
        undo=findViewById(R.id.UndoButton);
        clear=findViewById(R.id.ClearButton);
        gallery=findViewById(R.id.galleryButton);
        colour = findViewById(R.id.change_color_button);
        View viewVisibility=findViewById(R.id.paintView);
        viewVisibility.setOnClickListener(this);
    }

    public void hideUI()
    {
        viewBrushOption.setVisibility(View.GONE);
        fillbtn.setVisibility(View.GONE);
        redo.setVisibility(View.GONE);
        undo.setVisibility(View.GONE);
        sp.setVisibility(View.GONE);
        clear.setVisibility(View.GONE);
        gallery.setVisibility(View.GONE);
        colour.setVisibility(View.GONE);
        pen_size.setVisibility(View.GONE);

    }

    public void showUI()
    {
        viewBrushOption.setVisibility(View.VISIBLE);
        fillbtn.setVisibility(View.VISIBLE);
        redo.setVisibility(View.VISIBLE);
        undo.setVisibility(View.VISIBLE);
        sp.setVisibility(View.VISIBLE);
        clear.setVisibility(View.VISIBLE);
        gallery.setVisibility(View.VISIBLE);
        colour.setVisibility(View.VISIBLE);
        pen_size.setVisibility(View.VISIBLE);
    }



    public void setVisibility(){
        brush.setVisibility(View.VISIBLE);
        circle.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        rect.setVisibility(View.VISIBLE);
        square.setVisibility(View.VISIBLE);
    }
    public void setActive(){
        visibility=true;
        switch (paintView.drawOption.getDrawOpt()){
            case "BRUSH":{
                circle.setImageResource(R.drawable.circle);
                brush.setImageResource(R.drawable.brush_used);
                line.setImageResource(R.drawable.line);
                rect.setImageResource(R.drawable.rectangle);
                square.setImageResource(R.drawable.square);
                break;
            }
            case "LINE":{
                circle.setImageResource(R.drawable.circle);
                brush.setImageResource(R.drawable.brush);
                line.setImageResource(R.drawable.line_used);
                rect.setImageResource(R.drawable.rectangle);
                square.setImageResource(R.drawable.square);
                break;
            }
            case "RECTANGLE":{
                circle.setImageResource(R.drawable.circle);
                brush.setImageResource(R.drawable.brush);
                line.setImageResource(R.drawable.line);
                rect.setImageResource(R.drawable.rectangle_used);
                square.setImageResource(R.drawable.square);
                break;
            }
            case "SQUARE":{
                circle.setImageResource(R.drawable.circle);
                brush.setImageResource(R.drawable.brush);
                line.setImageResource(R.drawable.line);
                rect.setImageResource(R.drawable.rectangle);
                square.setImageResource(R.drawable.square_used);
                break;
            }
            case "CIRCLE":{
                circle.setImageResource(R.drawable.circle_used);
                brush.setImageResource(R.drawable.brush);
                line.setImageResource(R.drawable.line);
                rect.setImageResource(R.drawable.rectangle);
                square.setImageResource(R.drawable.square);
                break;
            }
        }
    }
    public void noVisibility(){
        brush.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        rect.setVisibility(View.GONE);
        square.setVisibility(View.GONE);
        circle.setVisibility(View.GONE);
        visibility=false;
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ClearButton:{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Czy na pewno chcesz wyczyścić ekran?")
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                paintView.clear();
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

                break;
            }
            case R.id.UndoButton:{
                paintView.undo();
                break;
            }
            case R.id.RedoButton:{
                paintView.redo();
                break;
            }
            case R.id.fillButton:{
                if(paintView.drawOption.getDrawOpt()!="BRUSH"){
                if(fill){
                    fillbtn.setImageResource(R.drawable.no_fill);
                    fill=false;
                }else{
                    fillbtn.setImageResource(R.drawable.fill);
                    fill=true;
                }}
                break;
            }
            case R.id.viewBrushOption:{
                if(visibility==false){
                    setVisibility();
                    setActive(); }
                else{
                    noVisibility(); }
                break;
            }
            case R.id.brush_btn:{
                paintView.drawOption.setBrush_active();
                viewBrushOption.setImageResource(R.drawable.brush_used);
                noVisibility();
                break;
            }
            case R.id.line_btn:{
                paintView.drawOption.setLine_active();
                viewBrushOption.setImageResource(R.drawable.line_used);
                noVisibility();
                break;
            }
            case R.id.rect_btn:{
                paintView.drawOption.setRect_active();
                viewBrushOption.setImageResource(R.drawable.rectangle_used);
                noVisibility();
                break;
            }
            case R.id.square_btn:{
                paintView.drawOption.setSquare_active();
                viewBrushOption.setImageResource(R.drawable.square_used);
                noVisibility();
                break;
            }
            case R.id.circle_btn:{
                paintView.drawOption.setCircle_active();
                viewBrushOption.setImageResource(R.drawable.circle_used);
                noVisibility();
                break;
            }
            case R.id.galleryButton:{
                pickFromGallery();
                break;
            }
            case R.id.change_color_button:{
                openColourPicker();
                break;
            }

        }
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        if((height>newHeight)|| (width>newWidth)) {
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            if ((newWidth < width) && (newHeight > height)) {
                matrix.postScale(scaleWidth, scaleWidth);
            } else if ((newHeight < height) && (newWidth > width)) {
                matrix.postScale(scaleHeight, scaleHeight);
            } else if ((newHeight < height) && (newWidth < width)) {
                if (scaleHeight < scaleWidth) {
                    matrix.postScale(scaleHeight, scaleHeight);
                } else {
                    matrix.postScale(scaleWidth, scaleWidth);
                }
            }

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }else return bm;
    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                try {
                    currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    currentImage=getResizedBitmap(currentImage,width,height);
                    paintView.setBackgroundImg(currentImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
