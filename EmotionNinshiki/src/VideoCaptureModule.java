import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;


//******************************
//カメラからの映像処理クラス
//
// 用意している処理：
//
// - カメラデバイスの検出
// - 映像の取り込み
// - 映像の表示
// - 映像保存
// - 処理終了
//
//******************************

public class VideoCaptureModule {

	//******************************
	//フィールド
	//******************************
	private VideoCapture m_capture; //カメラを開くための
	private HighGui m_highGui;
	private VideoWriter m_writer;


	//******************************
	//メソッド
	//******************************

	//------------------------------
	//コンストラクタ
	//@param key モードを選択する文字列。メインメソッドより渡される
	//------------------------------
	public VideoCaptureModule(String key) {

		m_writer = null;
		m_capture = new VideoCapture (0); //カメラを開く命令
		try {
			Thread.sleep (1000);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//正しくカメラが検出された場合
		if(m_capture.isOpened())
		{
			m_highGui = new HighGui(); //PC画面上にカメラからの映像を表示するための処理

			//video saveのための処理
			//.mp4ファイルを作成する
			if(key.equals(new String("s")))
			{
				//カメラパラメータの取得
				int imageWidth = (int)m_capture.get(Videoio.CAP_PROP_FRAME_WIDTH); //画像の幅
				int imageHeight = (int)m_capture.get(Videoio.CAP_PROP_FRAME_HEIGHT); //画像の高さ
				int fourcc = (int)m_capture.get(Videoio.CAP_PROP_FOURCC); //コーデック
				int fps = (int)m_capture.get(Videoio.CAP_PROP_FPS); //フレームレート
				m_writer = new VideoWriter("test.avi",
						fourcc, fps/3., new Size(imageWidth, imageHeight)); //.mp4（ビデオファイル）の作成
			}
		}
		//カメラ検出が失敗した場合
		else
		{
			System.out.println("No camera detected.");
			System.exit(0);
		}
	}

	//------------------------------
	//処理を停止するメソッド
	//------------------------------
	public void stopVideoCapture()
	{
		HighGui.destroyAllWindows();

		if(m_writer != null)
			m_writer.release();

		System.out.println("Exit program.");
		System.exit(0);
	}

	//------------------------------
	//ビデオファイルに画像を保存するメソッド
	//@param image ビデオファイルに保存する画像
	//------------------------------
	public void saveVideo(Mat image)
	{
		if(m_writer != null)
			m_writer.write(image);
	}

	//------------------------------
	//画像一枚をjpgファイルとして保存するメソッド
	//@param image 保存する画像
	//------------------------------
	public void saveImage(Mat image)
	{
		Imgcodecs.imwrite("savedImage.jpg", image);
		// System.out.println("Image saved.");

	}

	//------------------------------
	//画像を表示するメソッド
	//@param image 表示する画像
	//------------------------------
	public void showImage(Mat image)
	{
		//"test"という名前のウインドウで画像表示
		HighGui.imshow("test", image);
	}

	//------------------------------
	//Getter
	//------------------------------

	//------------------------------
	//カメラが正しく開かれているか確認する
	//------------------------------
	public boolean isCameraOpened() {

		return m_capture.isOpened();
	}

	//------------------------------
	//入力キーを返す
	//------------------------------
	public int getInputKey() {

		return HighGui.waitKey(20); //wait 20 msec to get any key pushed

	}

	//------------------------------
	//カメラから取得された一枚フレーム（画像）を返す
	//------------------------------
	public Mat getFrameFromCamera() {

		Mat image = new Mat();
		m_capture.read(image);

		return image;
	}
}