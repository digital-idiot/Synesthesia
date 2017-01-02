package orientationProvider;

/**
 * Warning: This file has not checked thoroughly
 */

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import representation.MatrixF4x4;
import representation.Quaternion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhisek on 1/2/17.
 */
public abstract class OrientationProvider implements SensorEventListener {

	protected final Object synchronizationToken = new Object();

	protected List<Sensor> sensorList = new ArrayList<Sensor>();

	protected final MatrixF4x4 currentOrientationRotationMatrix;

	protected final Quaternion currentOrientationQuaternion;

	protected SensorManager sensorManager;

	public OrientationProvider(SensorManager sensorManager) {
		this.sensorManager = sensorManager;

		// Initialise with identity
		currentOrientationRotationMatrix = new MatrixF4x4();

		// Initialise with identity
		currentOrientationQuaternion = new Quaternion();
	}

	public void start() {
		for (Sensor sensor : sensorList) {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void stop() {
		for (Sensor sensor : sensorList) {
			sensorManager.unregisterListener(this, sensor);
		}
	}

	/**
	//@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Not doing anything
	}
	*/

	public void getRotationMatrix(MatrixF4x4 matrix) {
		synchronized (synchronizationToken) {
			matrix.set(currentOrientationRotationMatrix);
		}
	}

	public void getQuaternion(Quaternion quaternion) {
		synchronized (synchronizationToken) {
			quaternion.set(currentOrientationQuaternion);
		}
	}

	public void getEulerAngles(float angles[]) {
		synchronized (synchronizationToken) {
			SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles);
		}
	}
}
