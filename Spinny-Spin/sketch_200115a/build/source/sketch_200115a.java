import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Arrays; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch_200115a extends PApplet {




int state;
int i;
float[] x = new float[10];
int[] fills = new int[10];
float rotation;
float gradient;
float curColor;
float lastColor;

// Trig lookup tables borrowed from Toxi; cryptic but effective.
float sinLUT[];
float cosLUT[];
float SINCOS_PRECISION=1.0f;
int SINCOS_LENGTH= PApplet.parseInt((360.0f/SINCOS_PRECISION));

public void setup() {
    
    background(255);
    colorMode(HSB, 100, 100, 100);

    state = 0;
    i = 0;
    Arrays.fill(x,0);
    rotation = 0;
    gradient = 0.0001f;
    lastColor = 100;

    // Fill the look-up tables
    sinLUT=new float[SINCOS_LENGTH];
    cosLUT=new float[SINCOS_LENGTH];
    for (int i = 0; i < SINCOS_LENGTH; i++) {
      sinLUT[i]= (float)Math.sin(i*DEG_TO_RAD*SINCOS_PRECISION);
      cosLUT[i]= (float)Math.cos(i*DEG_TO_RAD*SINCOS_PRECISION);
    }
}

public void draw() {
    translate(width/2, height/2);
    rotate(radians(-180));
    background(0);

    if (state == 0) {
        for (int j = 0; j < i; j++) {
            fill(color(lastColor, 0, 100));
            arc(0,0, 180 ,360 - (j * 30),10);
        }

        if (x[i] > 179.5f) {
            if (i < 9) {
                i++;
            } else {
                curColor = PApplet.parseInt(random(100));
                lastColor = 0;
                state = 1;
            }
        } else {
            x[i] = lerp(x[i],181,.1f);
        }

        float diam = ((2*x[i]) - (i * 30));
        if (diam < 0) {
            noFill();
        } else {
            fill(color(lastColor, 0, 100));
        }
        arc(0,0, x[i] , diam,10);
    }

    if (state == 1) {
        gradient = lerp(gradient, 0.05f, 0.005f);
        rotation = lerp(rotation, radians(-360), gradient);
        if ((rotation*-1) < radians(180)) {
            lastColor = lerp(lastColor, 100, gradient*2);
        } else {
            lastColor = lerp(lastColor, 0, gradient/2);
        }
        println(lastColor);
        // color curColor = style[int(random(10))];

        for (int j = 0; j <= i; j++) {
            fill(color(curColor, lastColor, 100-(j*(lastColor/10))));
            pushMatrix();
            float r = rotation*(10.0f-j);
            rotate(r);
            arc(0,0, 270 ,360 - (j * 30),10);
            popMatrix();
        }
        if ((rotation*(-1) + gradient*0.01f) >= radians(360)) {
            state = 2;
            gradient = 0.0001f;
            rotation = 0;
            curColor = PApplet.parseInt(random(100));
        }
    }

    if (state == 2) {
        gradient = lerp(gradient, 0.05f, 0.005f);
        rotation = lerp(rotation, radians(360), gradient);
        if (rotation < radians(180)) {
            lastColor = lerp(lastColor, 100, gradient*2);
        } else {
            lastColor = lerp(lastColor, 0, gradient/2);
        }
        curColor = PApplet.parseInt(random(100));

        for (int j = 0; j <= i; j++) {
            fill(color(curColor, lastColor, 100-(j*(lastColor/10))));
            pushMatrix();
            float r = rotation*(10.0f-j);
            rotate(r);
            arc(0,0, 180 ,360 - (j * 30),10);
            popMatrix();
        }
        if ((rotation + gradient*.01f) >= radians(360)) {
            state = 1;
            gradient = 0.0001f;
            rotation = 0;
            curColor = PApplet.parseInt(random(100));
        }
    }
}

// Draw solid arc
public void arc(float x,float y,float deg,float rad,float w) {
  int a = PApplet.parseInt(min (deg/SINCOS_PRECISION,SINCOS_LENGTH-1));
  beginShape(QUAD_STRIP);
  for (int i = 0; i < a; i++) {
    vertex(cosLUT[i]*(rad)+x,sinLUT[i]*(rad)+y);
    vertex(cosLUT[i]*(rad+w)+x,sinLUT[i]*(rad+w)+y);
  }
  endShape();
}
  public void settings() {  size(1024, 768, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch_200115a" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
