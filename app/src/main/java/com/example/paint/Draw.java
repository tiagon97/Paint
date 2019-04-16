package com.example.paint;

import android.graphics.Path;

public class Draw {

    public int color;
    public int strokeWidth;
    public Path path;
    public boolean emboss;
    public boolean blur;
    public boolean fill;

    public Draw(int color, int strokeWidth, Path path,boolean emboss,boolean blur,boolean fill) {

        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
        this.emboss=emboss;
        this.blur=blur;
        this.fill=fill;

    }

}