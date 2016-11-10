package com.example.nul01.clientgps;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Launcher extends Activity {
	Intent intent2;
	String token;
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch);
		handler = new Handler();
























		final DBManager dbManager = new DBManager(getApplicationContext(), "member.db", null, 1);

//		final SharedPreferences setting;
//		final SharedPreferences.Editor editor;
//		setting = getSharedPreferences("setting", 0);
//		editor= setting.edit();



		final ImageView txt8 = (ImageView) findViewById(R.id.TextView08);
		final Button resister = (Button) findViewById(R.id.resister2);
		final Button login_bt = (Button) findViewById(R.id.login_bt2); // 일반

		resister.setVisibility(View.INVISIBLE);
		login_bt.setVisibility(View.INVISIBLE);

		View.OnClickListener listener2 = new View.OnClickListener() { //회원가입
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.resister_alert, null);
				final EditText id = (EditText) addLayout.findViewById(R.id.alert_id);
				final EditText pw = (EditText) addLayout.findViewById(R.id.alert_pw);
//			final EditText contents = (EditText) addLayout.findViewById(R.id.alert_contents);
				new AlertDialog.Builder(Launcher.this).setTitle("정보를 입력하세요.").setView(addLayout)
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								String _id = id.getText().toString();
								String _pw = pw.getText().toString();
								if(!_id.equals("")) {
									dbManager.insert("insert into member values(null, '" + _id + "', '" + _pw + "');");
//									editor.putString("ID", _id);
//									editor.putString("PW", _pw);
//									editor.commit();
								}
								else{
									Toast toast = Toast.makeText(getApplicationContext(),"id를 입력해주세요.", Toast.LENGTH_SHORT);
									toast.show();
								}
							}
						}).show();
			}


		};


		View.OnClickListener listener3 = new View.OnClickListener() { //로그인
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub




				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.resister_alert, null);
				final EditText id = (EditText) addLayout.findViewById(R.id.alert_id);
				final EditText pw = (EditText) addLayout.findViewById(R.id.alert_pw);
//			final EditText contents = (EditText) addLayout.findViewById(R.id.alert_contents);
				new AlertDialog.Builder(Launcher.this).setTitle("정보를 입력하세요.").setView(addLayout)
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								final String _id = id.getText().toString();
								final String _pw = pw.getText().toString();



								if(!_id.equals("")) {
									if(!dbManager.PrintData(_id, _pw).equals("null")){
										Toast.makeText(getApplicationContext(),"로그인성공", Toast.LENGTH_SHORT).show();

										Intent intent = new Intent(Launcher.this, MainActivity.class);
										intent.putExtra("id", _id);
										startActivity(intent);
										finish();


									}
									else{
										Toast.makeText(getApplicationContext(),"로그인실패", Toast.LENGTH_SHORT).show();
									}


								}
								else{
									Toast toast = Toast.makeText(getApplicationContext(),"id를 입력해주세요.", Toast.LENGTH_SHORT);
									toast.show();
								}
							}
						}).show();
			}


		};

		resister.setOnClickListener(listener2);
		login_bt.setOnClickListener(listener3);


		final AnimationSet animSet = new AnimationSet(true);
		final AnimationSet animSet2 = new AnimationSet(true);

		Animation scaleZoom = AnimationUtils.loadAnimation(Launcher.this, R.anim.fade2);
		Animation scaleZoom2 = AnimationUtils.loadAnimation(Launcher.this, R.anim.fade);




		animSet.addAnimation(scaleZoom);

		animSet2.addAnimation(scaleZoom2);






		final Handler handler = new Handler();

		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub

//						txt0.startAnimation(animSet);
//						txt0.setVisibility(View.VISIBLE);


						txt8.startAnimation(animSet);
						txt8.setVisibility(View.VISIBLE);

//						txt9.startAnimation(animSet);
//						txt9.setVisibility(View.VISIBLE);
					}
				});
				try {
					Thread.sleep(4000); //700���̷� ��簡��
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						login_bt.startAnimation(animSet2);
						resister.startAnimation(animSet2);
						resister.setVisibility(View.VISIBLE);
						login_bt.setVisibility(View.VISIBLE);

					}
				});




			}
		}.start();



	}




}
