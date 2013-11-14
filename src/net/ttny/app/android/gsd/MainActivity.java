package net.ttny.app.android.gsd;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText phoneSecEditText;
	private TextView resultView;
	private Button queryButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		phoneSecEditText = (EditText) findViewById(R.id.phone_sec);
		resultView = (TextView) findViewById(R.id.result_text);
		queryButton = (Button) findViewById(R.id.query_btn);

		queryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = cwjManager.getActiveNetworkInfo();
				
				if (info != null && info.isAvailable()){
					String phoneSec = phoneSecEditText.getText().toString().trim();
					if ("".equals(phoneSec)) {
						phoneSecEditText.setError("请输入手机号码（段）！");
						phoneSecEditText.requestFocus();
						resultView.setText("");
						return;
					}
					if (phoneSec.length() < 7) {
						phoneSecEditText.setError("输入的手机号码（段）最少是前7位！");
						phoneSecEditText.requestFocus();
						resultView.setText("");
						return;
					}
					getRemoteInfo(phoneSec);
				} else {
					Toast.makeText(MainActivity.this,"请连接Wifi或手机网络！",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void getRemoteInfo(String phoneSec) {
		String nameSpace = "http://WebXml.com.cn/";
		String methodName = "getMobileCodeInfo";
		String endPoint = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx";
		String soapAction = "http://WebXml.com.cn/getMobileCodeInfo";
		SoapObject rpc = new SoapObject(nameSpace, methodName);

		rpc.addProperty("mobileCode", phoneSec);
		//rpc.addProperty("userId", "");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);

		envelope.bodyOut = rpc;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		try {
			transport.call(soapAction, envelope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SoapObject object = (SoapObject) envelope.bodyIn;
		String result = object.getProperty(0).toString();

		resultView.setText(result);
	}
	
}
