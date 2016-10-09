package sensor;

import util.K;

/**
 * Created by vikrant on 7/10/16.
 */

public class Acceleration {
    float ax;
    float ay;
    float az;

    public Acceleration(float ax, float ay, float az) {
        this.ax = ax;
        this.ay = ay;
        this.az = az;
    }

    @Override
    public String toString() {
        return ax + K.FIELD_SEPARATOR + ay + K.FIELD_SEPARATOR + az ;
    }
}
