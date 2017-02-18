import representation.Cartesian2D;
import representation.Quaternion;
import representation.Vector3f;
import representation.Vector4f;

/**
 * Created by abhisek on 2/12/17.
 */
public class DemoServer {
	private Quaternion quaternion;
	//Sensitivity of Mouse Movement [Experimental Value]
	private float sensitivity;
	private Vector4f baseAxisAngle = new Vector4f();
	private double hcos, hsin;
	private double bli;

	DemoServer(Quaternion q, float s) {
		this.sensitivity = s;
		initFix(q);
	}

	//Baseline Initialization
	private void initFix(Quaternion quaternion) {
		Vector3f xvec = quaternion.rotateVector(new Vector3f(1.0f,0.0f,0.0f));
		Vector3f zvec = quaternion.rotateVector(new Vector3f(0.0f,0.0f,1.0f));
		double blh = -1.0f * Math.atan2(xvec.getY(),xvec.getX());
		hcos = Math.cos(blh);
		hsin = Math.sin(blh);
		bli = Math.asin(zvec.getY());
	}

	//Get Pointer Location from Quaternion
	public Cartesian2D pointerUpdate(Quaternion currentQuaternion) {
		Vector3f cxv = currentQuaternion.rotateVector(new Vector3f(1.0f, 0.0f, 0.0f));
		Vector3f czv = currentQuaternion.rotateVector(new Vector3f(0.0f, 0.0f, 1.0f));
		double xeff = (hcos * cxv.getX()) - (hsin * cxv.getY());
		double yeff = (hsin * cxv.getX()) + (hcos * cxv.getY());
		double hdel = Math.atan2(yeff, xeff);
		double cli = Math.asin(czv.getY());
		double yEst = sensitivity * Math.tan(cli - bli);
		double xEst = sensitivity * Math.tan(hdel);
		initFix(currentQuaternion);
		return new Cartesian2D(xEst,yEst);
	}
}
