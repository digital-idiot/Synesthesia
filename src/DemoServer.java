import representation.Cartesian2D;
import representation.Quaternion;
import representation.Vector3f;

/**
 * Created by abhisek on 2/12/17.
 */
public class DemoServer {
	private Quaternion quaternion;
	//Sensitivity of Mouse Movement [Experimental Value]
	private float sensitivity;
	private double hcos, hsin;
	private double bli;

	DemoServer(Quaternion q, float s) {
		updateSensitivity(s);
		initFix(q);
	}

	//Baseline Initialization
	private final void initFix(Quaternion q) {
		this.quaternion = q;
		Vector3f xvec = rotateVector(new Vector3f(1.0f,0.0f,0.0f),quaternion);
		Vector3f zvec = rotateVector(new Vector3f(0.0f,0.0f,1.0f),quaternion);
		double blh = -1.0f*Math.atan2(xvec.getY(),xvec.getX());
		hcos = Math.cos(blh);
		hsin = Math.sin(blh);
		bli = Math.asin(zvec.getY());
	}

	//Get Pointer Location from Quaternion
	public Cartesian2D pointerUpdate(Quaternion currentQuaternion) {
		Vector3f cxv = rotateVector(new Vector3f(1.0f, 0.0f, 0.0f), currentQuaternion);
		Vector3f czv = rotateVector(new Vector3f(0.0f, 0.0f, 1.0f), currentQuaternion);
		double xeff = (hcos * cxv.getX()) - (hsin * cxv.getY());
		double yeff = (hsin * cxv.getX()) - (hcos * cxv.getY());
		double hdel = Math.atan2(yeff, xeff);
		double cli = Math.asin(czv.getY());
		double yEst = sensitivity * Math.tan(cli - bli);
		initFix(currentQuaternion);
		double xEst = sensitivity * Math.tan(hdel);
		return new Cartesian2D(xEst,yEst);
	}

	//Rotate a Vector
	private final Vector3f rotateVector(Vector3f v, Quaternion q) {
		Quaternion qt = new Quaternion();
		qt.copyFromVec3(v,0.0f);
		Quaternion cqt = new Quaternion();
		cqt.setX(-1.0f*q.getX());
		cqt.setY(-1.0f*q.getY());
		cqt.setZ(-1.0f*q.getZ());
		cqt.setW(q.getW());
		q.multiplyByQuat(qt,qt);
		qt.multiplyByQuat(cqt,qt);
		Vector3f vec = new Vector3f(qt.getX(),qt.getY(),qt.getZ());
		return vec;
	}

	//Update Sensitivity Constant
	public final void updateSensitivity(float s) {
		this.sensitivity = s;
	}
}
