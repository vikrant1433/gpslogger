package logger;

import sensor.Acceleration;
import util.K;

/**
 * Created by vikrant on 7/10/16.
 */

public class AccelerometerLogger extends LogWriter {

    public AccelerometerLogger(String fileNamePrefix) {
        super(fileNamePrefix);
        setHeader(K.ACCELERATION_LOGGER_DEFAULT_HEADER);
    }

    public void writeAcceleration(Acceleration acceleration) {
        write(System.currentTimeMillis() + K.FIELD_SEPARATOR + acceleration.toString());
    }
}
