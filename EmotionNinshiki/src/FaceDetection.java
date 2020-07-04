import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

// 顔検出クラス　ほぼサンプルコードの流用
public class FaceDetection {


	 private String m_face_classifier_path = "/usr/local/Cellar/opencv/4.1.2/share/opencv4/haarcascades/haarcascade_frontalface_alt2.xml";


	// Constructor
	public FaceDetection() {
		// do nothing
	}

	//顔検出の実行する
	// 検出結果（画像上の座標位置）を返す
	//@param input_img 入力画像

	public MatOfRect execFaceDetection(Mat input_img) {

		CascadeClassifier faceDetector = new CascadeClassifier(m_face_classifier_path);
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(input_img, faceDetections);

		return faceDetections;
	}


}
