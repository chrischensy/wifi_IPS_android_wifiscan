package com.example.wifiscan2;

import com.example.wifiscan2.utils.Point;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.content.IntentFilter;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.net.wifi.WifiManager;  
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity {

	private Point point;
	private Button wifiScanButton;
	private EditText pointxText,pointyText;
	private TextView messageText;
	public WifiManager wifiManager;
	private WifiReceiver wifiReceiver;
	public String filePath;
	public Handler checkScanStateHandler = new Handler(){
    	public void handleMessage(Message massage) {  
            if(massage.what == 1){
            	unregisterReceiver(wifiReceiver);
            	messageText.setText("点("+point.x+","+point.y+")扫描完成！");
            	wifiScanButton.setEnabled(true);
            	wifiScanButton.setBackgroundColor(Color.GREEN);
            } 
        };  
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiScanButton = (Button) this.findViewById(R.id.wifiscan);
        wifiScanButton.setBackgroundColor(Color.GREEN);
       	pointxText = (EditText) this.findViewById(R.id.Pointx);
        pointyText = (EditText) this.findViewById(R.id.Pointy);
        messageText = (TextView) this.findViewById(R.id.Message);
        wifiScanButton.setOnClickListener(new ClickEvent());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    class ClickEvent implements OnClickListener{
        @Override
        public void onClick(View v) {
        	
        	if(pointxText.getText().toString().equals("")
        			||pointyText.getText().toString().equals("")){
        		messageText.setText("请输入坐标!");
        		return;
        	}
        	
        	int pointx = Integer.valueOf(pointxText.getText().toString());
        	int pointy = Integer.valueOf(pointyText.getText().toString());
        	point= new Point(pointx,pointy);
        	wifiScanButton.setEnabled(false);
        	wifiScanButton.setBackgroundColor(Color.RED);
        	wifiManager = (WifiManager) getSystemService("wifi");
    		if (!wifiManager.isWifiEnabled())
    			if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
    				wifiManager.setWifiEnabled(true);
    		wifiReceiver = new WifiReceiver(wifiManager,point,checkScanStateHandler);
    		registerReceiver(wifiReceiver, new IntentFilter(
    				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    		wifiManager.startScan();
    		messageText.setText("正在扫描。。。");
        }            
    }
}
