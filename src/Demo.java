import orientationProvider.KalmanFilterProvider;
import orientationProvider.OrientationProvider;
import representation.Quaternion;

/**
 * Created by abhisek on 2/12/17.
 */


public class Demo {
	private Quaternion quaternion = new Quaternion();
	private OrientationProvider currentOrientationProvider = new KalmanFilterProvider((SensorManager) getActivity().getSystemService(SensorSelectionActivity.SENSOR_SERVICE));
	if (orientationProvider != null) {
		// Get the rotation from the current orientationProvider as quaternion
		currentOrientationProvider.getQuaternion(quaternion);
		//Either send quaternion as object or use the following
		float w = (float) (2.0f * Math.acos(quaternion.getW()) * 180.0f / Math.PI),
		float x = quaternion.getX(),
		float y = quaternion.getY(),
		float z = quaternion.getZ();
	}
}
