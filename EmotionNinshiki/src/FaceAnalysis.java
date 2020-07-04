import java.io.File;
import java.net.URI;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

// 顔分析を行うクラス
public class FaceAnalysis {
	// 分析結果保存クラス
	private static AnalysisResult result;
	// 顔画像のパス
	private static String faceImagePath;

	// APIのサブスクリプションキー
	private final static String subscriptionKey = "5cb1ee9694e2487f8b2b1031f11c1984";

	// url
	private final static String uriBase = "https://emortionninshiki.cognitiveservices.azure.com/face/v1.0/detect";

	// returnされたJSONをString化したもの
	private static String jsonString;

	// Constructor
	public FaceAnalysis(String path) {
		// 画像パスを指定し、分析結果クラスをインスタンス化
		FaceAnalysis.faceImagePath = path;
		result = new AnalysisResult();
	}

	// 画像を送信する
	public static void sentPic() {
		// APIに送信する手法はMicrosoft Azureのチュートリアルを参考にして書きました
		HttpClient httpclient = HttpClientBuilder.create().build();
		try {
			URIBuilder builder = new URIBuilder(uriBase);
			// 感情値のみを返すように指定
			builder.setParameter("returnFaceAttributes", "emotion");
			File img = new File(faceImagePath);
			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
			FileEntity reqEntity = new FileEntity(img);
			request.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				jsonString = EntityUtils.toString(entity).trim();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// 結果を取得し、データとして格納する
		getResult(jsonString);
	}

	// 結果を取得し、データとして格納
	private static void getResult(String json) {
		if (json.charAt(0) != '[') {
			// 予期される結果以外がreturnされた場合の動作
			if(json.charAt(2) == 'e') {
				// error:（API制限）がreturnされてきた場合の処理
				// StringをjsonObjectに変換
				JSONObject jsonObj = new JSONObject(json);
				// その中から"error:{....}"という形式のjsonObjectを取り出す
				JSONObject errorObj = jsonObj.getJSONObject("error");
				// さらにその中の"message"という項目を取り出す
				String msgString = errorObj.getString("message");
				// 取り出したStringを"."（ピリオド）で分割し、配列にする
				String[] errSplit = msgString.split(Pattern.quote("."));
				// それをもとにエラーメッセージを作成。
				String msg = "API rate limit is exceeded." + errSplit[2] + ".";
				// 分析結果格納クラスにメッセージとしてエラーメッセージを格納する
				result.setMsg(msg);
				return;
			}
		} else {
			// StringをJsonArray形式に変換
			JSONArray jsonArray = new JSONArray(json);
			if (jsonArray.length() != 0) {
				// jsonArrayの長さが0ではない場合の処理
				// jsonArray[0]からjsonObjectを取り出す
				JSONObject jsonObj = jsonArray.getJSONObject(0);
				// さらにその中から"faceAttributes:{...}"というjsonObjectを取り出す
				JSONObject attribObj = jsonObj.getJSONObject("faceAttributes");
				// その中から"emotion:{...}"というjsonObjectを取り出す
				JSONObject emortionObj = attribObj.getJSONObject("emotion");
				// setterを用いて値をセットする
				result.setAnger((Double) emortionObj.get("anger"));
				result.setContempt((Double) emortionObj.get("contempt"));
				result.setDisgust((Double) emortionObj.get("disgust"));
				result.setFear((Double) emortionObj.get("fear"));
				result.setHappiness((Double) emortionObj.get("happiness"));
				result.setNeutral((Double) emortionObj.get("neutral"));
				result.setSadness((Double) emortionObj.get("sadness"));
				result.setSurprise((Double) emortionObj.get("surprise"));
			}
		}
	}

	// 結果の中で一番大きな値を取得する
	public static String getMaxValue() {
		return result.getMaxEmotion();
	}
}
