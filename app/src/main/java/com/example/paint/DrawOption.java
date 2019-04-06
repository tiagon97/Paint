package com.example.paint;

public class DrawOption {
    public String drawOpt;

    public DrawOption(){
        drawOpt="BRUSH";
    }

    public void setBrush_active() {
        this.drawOpt="BRUSH";
    }

    public void setLine_active() {
        this.drawOpt="LINE";
    }
    public void setRect_active(){
        this.drawOpt="RECTANGLE";
    }
    public void setSquare_active(){
        this.drawOpt="SQUARE";
    }

    public String getDrawOpt() {
        return drawOpt;
    }
}
