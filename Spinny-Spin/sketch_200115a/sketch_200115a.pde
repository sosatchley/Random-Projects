import java.util.Arrays;


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
float SINCOS_PRECISION=1.0;
int SINCOS_LENGTH= int((360.0/SINCOS_PRECISION));

void setup() {
    size(1024, 768, P3D);
    background(255);
    colorMode(HSB, 100, 100, 100);

    state = 0;
    int currentArc = 0;
    Arrays.fill(arcMeasures, 0);
    Arrays.fill(arcRadii, 0);
    rotation = 0;
    gradient = 0.0001;
    lastColor = 100;

    // Fill the look-up tables
    sinLUT=new float[SINCOS_LENGTH];
    cosLUT=new float[SINCOS_LENGTH];
    for (int i = 0; i < SINCOS_LENGTH; i++) {
      sinLUT[i]= (float)Math.sin(i*DEG_TO_RAD*SINCOS_PRECISION);
      cosLUT[i]= (float)Math.cos(i*DEG_TO_RAD*SINCOS_PRECISION);
    }
}

void draw() {
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

void growArcs(float x, float y, int numberOfArcs,
              float degrees, float radius, float spacing, float thickness) {

    float threshold = 1.0;

    for (int i = 0; i < currentArc; i++) {
        // fill(color(hue, 0, 100));
        arc(x,y, degrees ,radius - (i * spacing),thickness);
    }

    if (arcMeasures[currentArc] > degrees - threshold &&
        arcRadii[currentArc] > (radius - (currentArc * spacing))-threshold) {
        if (currentArc >= numberOfArcs - 1) {
            curColor = int(random(100));
            lastColor = 0;
            state = 1;
        } else {
            currentArc++;
            println(currentArc);
        }
    } else {
        arcMeasures[currentArc] = lerp(arcMeasures[currentArc], degrees + threshold, .1);
        arcRadii[currentArc] = lerp(arcRadii[currentArc], (radius - (currentArc
                                * spacing)) + threshold, 0.1);
    }
    if (arcRadii[currentArc] < 0) {noFill();}
    arc(x,y, arcMeasures[currentArc] , arcRadii[currentArc], thickness);
}

void whirlyDirly(int dir, boolean strobe) {
    gradient = lerp(gradient, 0.05, 0.005);
    rotation = lerp(rotation, radians(360 * dir), gradient);
    curColor = (strobe) ? int(random(100)) : curColor;

    if (Math.abs(rotation) < radians(180)) {
        lastColor = lerp(lastColor, 100, gradient*2);
    } else {
        lastColor = lerp(lastColor, 0, gradient/2);
    }

    for (int j = 0; j <= currentArc; j++) {
        fill(color(curColor, lastColor, 100-(j*(lastColor/10))));
        pushMatrix();
        float r = rotation*(10.0-j);
        rotate(r);
        arc(0,0, 180 ,360 - (j * 30),10);
        popMatrix();
    }
    if (Math.abs(rotation) + gradient*.01 >= radians(360)) {
        gradient = 0.0001;
        rotation = 0;
        curColor = int(random(100));
        state = (state == 1) ? 2 : 1;
    }
}
// Draw solid arc
void arc(float x,float y,float deg,float rad,float w) {
  int a = int(min (deg/SINCOS_PRECISION,SINCOS_LENGTH-1));
  beginShape(QUAD_STRIP);
  for (int i = 0; i < a; i++) {
    vertex(cosLUT[i]*(rad)+x,sinLUT[i]*(rad)+y);
    vertex(cosLUT[i]*(rad+w)+x,sinLUT[i]*(rad+w)+y);
  }
  endShape();
}
