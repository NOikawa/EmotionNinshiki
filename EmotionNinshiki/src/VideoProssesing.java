import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

// 映像処理クラス

public class VideoProssesing {
	// 映像処理系モジュール　ほぼサンプルプログラムを流用
	private static VideoCaptureModule videoCaptureModule;
	// 顔検出クラス
	private static FaceDetection faceDetection;
	// 顔分析クラス
	private static FaceAnalysis faceAnalysis;
	// 描画する画像
	private static Mat m_imposeImg;

	// main
	public static void main(String[] args) {
		// ほぼサンプルプログラムと同じ
		System.out.println("pless any key.");
		String buf = "n";
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		try {
			buf = br.readLine();
			System.out.println("input key is " + buf);
		} catch (IOException e) {
			System.out.println("Quit program");
			System.exit(0);
		}

		//*****************************
		//処理開始
		//*****************************

		//OpenCVを使うために必ず入れる一行
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// インスタンス化
		videoCaptureModule = new VideoCaptureModule(buf);
		faceDetection = new FaceDetection();

		while (videoCaptureModule.isCameraOpened()) {
			Mat image = videoCaptureModule.getFrameFromCamera(); //カメラ映像から画像を一枚取り出す

			//カメラ画像の縮小（処理高速化のため）
			double ratio = 0.5; //入力画像を縮小する割合
			Imgproc.resize(image, image, new Size(0, 0), ratio, ratio);

			// 顔検出を行う
			MatOfRect mor = faceDetection.execFaceDetection(image);
			// 顔が存在した場合の処理
			if (mor.toArray().length != 0) {
				// 画像を保存し分析へ
				videoCaptureModule.saveImage(image);
				faceAnalysis = new FaceAnalysis("savedImage.jpg");
				FaceAnalysis.sentPic();

				// 顔位置に矩形描画
				drawSquare(image, mor);

				// 結果表示
				drawAnalysisResult(image, mor, FaceAnalysis.getMaxValue());
			}else{
				// 顔が存在しない場合、画面上に"No Face"と出力する
				drawNoFace(image);
			}

			videoCaptureModule.showImage(image); //取り込んだ画像を表示

			//キーボード入力の取得
			//表示している画面をアクティブにするとキー入力を受け付ける
			int key = videoCaptureModule.getInputKey();

			//qが押されたらwhileループを抜ける
			if (key == 81)
				break;

		}

		//映像取得を終了する（プログラムを終了する）メソッド
		videoCaptureModule.stopVideoCapture();
		System.exit(0);
	}


	// 顔位置に矩形を描画。サンプルプログラムから変更なし
	public static void drawSquare(Mat img, MatOfRect mor) {
		for (Rect rect : mor.toArray()) {
			Imgproc.rectangle(img, new Point(rect.x, rect.y),
					new Point(rect.x + rect.width, rect.y + rect.height),
					new Scalar(0, 255, 0), 1);
		}
	}

	// Nofaceと出力
	public static void drawNoFace(Mat img) {
		// テキストを描画
		Point pt = new Point(190, 180);
		Scalar col = new Scalar(0, 0, 255);
		Imgproc.putText(img, "No Face",pt,Imgproc.FONT_HERSHEY_DUPLEX, 2.0, col);
	}

	// returnされた結果によって処理を変更する。
	// 感情の値がreturnされた場合、貼り付ける画像のパスを該当のものに指定
	// API制限に引っかかった場合は、その旨と残り何秒待てば処理を再開できるかをテキストで出力する。
	public static void drawAnalysisResult(Mat img, MatOfRect mor, String maxEmortion) {
		if (maxEmortion.equals("emotion")) {
			// 値が何もreturnされなかった場合（分析/APIへの送信がそもそも行われなかった）は、何も行わずにreturnする
			return;
		} else {
			switch (maxEmortion) {
			// 感情の値が正しくreturnされている場合は、該当する画像パスを指定、貼り付け処理を行う
			// （機能追加）どの感情が推定されたか、文字でも記載する。
			default:
				// 基本的にはAPI制限に引っかかった際に残り何秒で制限解除されるかを出力する
				Point pt = new Point(180, 350);
				Scalar col = new Scalar(255, 0, 0);
				Imgproc.putText(img, maxEmortion, pt, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, col);
			case ("contempt"):
				m_imposeImg = Imgcodecs.imread("img/contempt.png");
				imposeImage(img, mor);
				break;
			case ("surprise"):
				m_imposeImg = Imgcodecs.imread("img/surprise.png");
				imposeImage(img, mor);
				break;
			case ("happiness"):
				m_imposeImg = Imgcodecs.imread("img/happiness.png");
				imposeImage(img, mor);
				break;
			case ("neutral"):
				m_imposeImg = Imgcodecs.imread("img/neutral.png");
				imposeImage(img, mor);
				break;
			case ("sadness"):
				m_imposeImg = Imgcodecs.imread("img/sadness.png");
				imposeImage(img, mor);
				break;
			case ("disgust"):
				m_imposeImg = Imgcodecs.imread("img/disgust.png");
				imposeImage(img, mor);
				break;
			case ("anger"):
				m_imposeImg = Imgcodecs.imread("img/anger.png");
				imposeImage(img, mor);
				break;
			case ("fear"):
				m_imposeImg = Imgcodecs.imread("img/fear.png");
				imposeImage(img, mor);
				break;
			}
		}
	}

	// 画像を貼り付ける
	private static void imposeImage(Mat img, MatOfRect mor) {
		//検出されたすべての顔画像に対するループ
		for (Rect rect : mor.toArray()) {

			// サイズの変更
			double ratio = 0.1; //拡大/縮小倍率
			Mat resizedImg = new Mat();

			// マスクの作成
			Mat mask = Imgcodecs.imread("img/mask.png");
			Mat maskResized = new Mat();

			// 貼り付ける画像、マスク共にリサイズ
			Imgproc.resize(m_imposeImg, resizedImg, new Size(0, 0), ratio, ratio);
			Imgproc.resize(mask, maskResized, new Size(0, 0), ratio, ratio);


			// 領域の決定
			Rect mrect = new Rect();
			// 貼り付ける画像が検出された顔領域の右あたりに来るように指定
			mrect.x = rect.x + rect.width + 10;
			mrect.y = rect.height/2 + rect.y/2;
			//画像サイズは重畳する画像（リサイズ済み）のサイズに指定
			mrect.width = resizedImg.cols();
			mrect.height = resizedImg.rows();

			// 画像の重畳

			//重畳する画像がカメラ画像の範囲外にならないようにする
			if (mrect.x + mrect.width < img.cols() && mrect.y + mrect.height < img.rows()) {
				Mat roiImg = img.submat(mrect); //カメラ画像中の重畳領域の指定
				resizedImg.copyTo(roiImg,maskResized); //その領域に画像を重畳（コピー）
			}
		}
	}
}
