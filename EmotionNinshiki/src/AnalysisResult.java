// 分析結果を保存するクラス

public class AnalysisResult {
	// 感情の値
	private Double contempt;
	private Double surprise;
	private Double happiness;
	private Double neutral;
	private Double sadness;
	private Double disgust;
	private Double anger;
	private Double fear;

	// 最大値を保存する変数。[0]に名前/メッセージ、[1]に数値を入れている
	private Object[] maxEmotion;

	// Constructor
	// 最大値保存変数の初期化
	public AnalysisResult() {
		this.maxEmotion = new Object[2];
		maxEmotion[0] = "emotion";
		maxEmotion[1] = -1.0;
	}

	public void setContempt(Double contempt) {
		this.contempt = contempt;
		// 値を入れたのちに、それが最大値となるかを確認する
		isMaxEmotion("contempt", this.contempt);
	}
	public void setSurprise(Double surprise) {
		this.surprise = surprise;
		// 値を入れたのちに、それが最大値となるかを確認する
		isMaxEmotion("surprise", this.surprise);
	}
	public void setHappiness(Double happiness) {
		this.happiness = happiness;
		// 値を入れたのちに、それが最大値となるかを確認する
		isMaxEmotion("happiness", this.happiness);
	}
	public void setNeutral(Double neutral) {
		this.neutral = neutral;
		// 値を入れたのちに、それが最大値となるかを確認する
		isMaxEmotion("neutral", this.neutral);
	}
	public void setSadness(Double sadness) {
		this.sadness = sadness;
		// 値を入れたのちに、それが最大値となるかを確認する
		isMaxEmotion("sadness", this.sadness);
	}
	public void setDisgust(Double disgust) {
		this.disgust = disgust;
		// 値を入れたのちに、それが最大値となるかを確認する
		isMaxEmotion("disgust", this.disgust);
	}
	public void setAnger(Double anger) {
		this.anger = anger;
		// 値を入れたのちに、それが最大値となるかを確認する
		isMaxEmotion("anger", this.anger);
	}
	public void setFear(double fear) {
		this.fear = fear;
		// 値を入れたのちに、それが最大値となるかを確認する
		isMaxEmotion("fear", this.fear);
	}

	// 最大値確認メソッド
	private void isMaxEmotion(String emo, Double val) {
		// emoが現在の最大値より大きければ、最大値を更新する
		if(val>(Double)maxEmotion[1]) {
			maxEmotion[0] = emo;
			maxEmotion[1] = val;
		}
	}

	// 最大値/メッセージをreturnするメソッド。String値のみreturnする
	public String getMaxEmotion() {
		return maxEmotion[0].toString();
	}

	// メッセージを最大値用の変数に入れるメソッド
	public void setMsg(String msg) {
		maxEmotion[0] = msg;
	}
}
