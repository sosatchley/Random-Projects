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
int currentArc;
int numberOfArcs = 10;
float[] arcMeasures = new float[numberOfArcs];
float[] arcRadii = new float[numberOfArcs];
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
    int currentArc = 0;
    Arrays.fill(arcMeasures, 0);
    Arrays.fill(arcRadii, 0);
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
        fill(color(100, 0, 100));
        growArcs(0, 0, 10, 180, 360, 30, 10);
    }

    if (state == 1) {
        whirlyDirly(-1, false);
    }

    if (state == 2) {
        whirlyDirly(1, true);
    }
}

public void growArcs(float x, float y, int numberOfArcs,
              float degrees, float radius, float spacing, float thickness) {

    float threshold = 1.0f;

    for (int i = 0; i < currentArc; i++) {
        // fill(color(hue, 0, 100));
        arc(x,y, degrees ,radius - (i * spacing),thickness);
    }

    if (arcMeasures[currentArc] > degrees - threshold &&
        arcRadii[currentArc] > (radius - (currentArc * spacing))-threshold) {
        if (currentArc >= numberOfArcs - 1) {
            curColor = PApplet.parseInt(random(100));
            lastColor = 0;
            state = 1;
        } else {
            currentArc++;
            println(currentArc);
        }
    } else {
        arcMeasures[currentArc] = lerp(arcMeasures[currentArc], degrees + threshold, .1f);
        arcRadii[currentArc] = lerp(arcRadii[currentArc], (radius - (currentArc
                                * spacing)) + threshold, 0.1f);
    }
    if (arcRadii[currentArc] < 0) {noFill();}
    arc(x,y, arcMeasures[currentArc] , arcRadii[currentArc], thickness);
}

public void whirlyDirly(int dir, boolean strobe) {
    gradient = lerp(gradient, 0.05f, 0.005f);
    rotation = lerp(rotation, radians(360 * dir), gradient);
    curColor = (strobe) ? PApplet.parseInt(random(100)) : curColor;

    if (Math.abs(rotation) < radians(180)) {
        lastColor = lerp(lastColor, 100, gradient*2);
    } else {
        lastColor = lerp(lastColor, 0, gradient/2);
    }

    for (int j = 0; j <= currentArc; j++) {
        fill(color(curColor, lastColor, 100-(j*(lastColor/10))));
        pushMatrix();
        float r = rotation*(10.0f-j);
        rotate(r);
        arc(0,0, 180 ,360 - (j * 30),10);
        popMatrix();
    }
    if (Math.abs(rotation) + gradient*.01f >= radians(360)) {
        gradient = 0.0001f;
        rotation = 0;
        curColor = PApplet.parseInt(random(100));
        state = (state == 1) ? 2 : 1;
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
