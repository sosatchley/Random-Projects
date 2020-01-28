import java.util.Arrays;


int state;
int i;
float[] x = new float[10];
color[] fills = new color[10];
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
    i = 0;
    Arrays.fill(x,0);
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
        for (int j = 0; j < i; j++) {
            fill(color(lastColor, 0, 100));
            arc(0,0, 180 ,360 - (j * 30),10);
        }

        if (x[i] > 179.5) {
            if (i < 9) {
                i++;
            } else {
                curColor = int(random(100));
                lastColor = 0;
                state = 1;
            }
        } else {
            x[i] = lerp(x[i],181,.1);
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
        gradient = lerp(gradient, 0.05, 0.005);
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
            float r = rotation*(10.0-j);
            rotate(r);
            arc(0,0, 180 ,360 - (j * 30),10);
            popMatrix();
        }
        if ((rotation*(-1) + gradient*0.01) >= radians(360)) {
            state = 2;
            gradient = 0.0001;
            rotation = 0;
            curColor = int(random(100));
        }
    }

    if (state == 2) {
        gradient = lerp(gradient, 0.05, 0.005);
        rotation = lerp(rotation, radians(360), gradient);
        if (rotation < radians(180)) {
            lastColor = lerp(lastColor, 100, gradient*2);
        } else {
            lastColor = lerp(lastColor, 0, gradient/2);
        }
        curColor = int(random(100));

        for (int j = 0; j <= i; j++) {
            fill(color(curColor, lastColor, 100-(j*(lastColor/10))));
            pushMatrix();
            float r = rotation*(10.0-j);
            rotate(r);
            arc(0,0, 180 ,360 - (j * 30),10);
            popMatrix();
        }
        if ((rotation + gradient*.01) >= radians(360)) {
            state = 1;
            gradient = 0.0001;
            rotation = 0;
            curColor = int(random(100));
        }
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
