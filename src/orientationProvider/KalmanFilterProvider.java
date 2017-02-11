package orientationProvider;

/**
 * Created by abhisek on 1/11/17.
 */
import representation.Quaternion;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Warning: This file has not checked/compiled thoroughly
 */


public class KalmanFilterProvider extends OrientationProvider {


	private static final float NS2S = 1.0f / 1000000000.0f;
	private final Quaternion deltaQuaternion = new Quaternion();
	private long timestamp;
	private static final double EPSILON = 0.1f; //[Experimental value] Filter-threshold
	private double gyroscopeRotationVelocity = 0; //Value giving the total velocity of the gyroscope
	private Quaternion correctedQuaternion = new Quaternion();
	public KalmanFilterProvider(SensorManager sensorManager) {
		super(sensorManager);
		sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// it is a good practice to check that we received the proper event
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

			// This timestamps delta rotation to be multiplied by the current rotation
			if (timestamp != 0) {
				final float dT = (event.timestamp - timestamp) * NS2S;
				// Axis of the rotation sample, not normalized yet.
				float axisX = event.values[0];
				float axisY = event.values[1];
				float axisZ = event.values[2];

				// Calculate the angular speed of the sample
				gyroscopeRotationVelocity = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

				// Normalize the rotation vector if it's big enough to get the axis
				if (gyroscopeRotationVelocity > EPSILON) {
					axisX /= gyroscopeRotationVelocity;
					axisY /= gyroscopeRotationVelocity;
					axisZ /= gyroscopeRotationVelocity;
				}

				/**
				 * Integrate around this axis with the angular speed by the timestep
				 * in order to get a delta rotation from this sample over the timestep
				 */

				double thetaOverTwo = gyroscopeRotationVelocity * dT / 2.0f;
				double sinThetaOverTwo = Math.sin(thetaOverTwo);
				double cosThetaOverTwo = Math.cos(thetaOverTwo);
				deltaQuaternion.setX((float) (sinThetaOverTwo * axisX));
				deltaQuaternion.setY((float) (sinThetaOverTwo * axisY));
				deltaQuaternion.setZ((float) (sinThetaOverTwo * axisZ));
				deltaQuaternion.setW(-(float) cosThetaOverTwo);

				synchronized (synchronizationToken) {
					deltaQuaternion.multiplyByQuat(currentOrientationQuaternion, currentOrientationQuaternion);
				}

				correctedQuaternion.set(currentOrientationQuaternion);
				// We inverted w in the deltaQuaternion, because currentOrientationQuaternion required it.
				// Before converting it back to matrix representation, we need to revert this process
				correctedQuaternion.w(-correctedQuaternion.w());

				synchronized (synchronizationToken) {
					// Set the rotation matrix as well to have both representations
					SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix,
							correctedQuaternion.array());
				}
			}
			timestamp = event.timestamp;
		}
	}
}
