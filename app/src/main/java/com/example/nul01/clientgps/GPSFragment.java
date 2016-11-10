package com.example.nul01.clientgps;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GPSFragment extends Fragment {
    Tcp_Client tcp;
    String bf_lat, bf_log;


    private LocationManager mLocationManager;
    private TextView lat;
    private TextView log;
    private TextView tt_time;
    private TextView ip_addr;
    private EditText device_key;

    private static final String TAG = "MainActivity";
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date now;
    String str_date;
    Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gps, null);


        Button button_start = (Button) view.findViewById(R.id.button_start);
        Button button_stop = (Button) view.findViewById(R.id.button_stop);
        Button button_send = (Button) view.findViewById(R.id.button_send);
        bf_log = "null";
        bf_lat = "null";


        lat = (TextView) view.findViewById(R.id.lat);
        log = (TextView) view.findViewById(R.id.log);
        tt_time = (TextView) view.findViewById(R.id.tt_time);
        ip_addr = (TextView) view.findViewById(R.id.ip_addr);
        device_key = (EditText) view.findViewById(R.id.device_key);


        button_start.setOnClickListener(new btnstartonclicklistener()); //스타트 버튼 선언
        button_stop.setOnClickListener(new btnstoponclicklistener()); //종료 버튼 선언
        button_send.setOnClickListener(new btnsendonclicklistener()); //전송 버튼 선언




        return view;
    }

    public class btnstartonclicklistener implements View.OnClickListener {
        public void onClick(View v) {
            if (!gpsIsOpen())
                return;

            get_gps();


            Tcp_Client tcpClient = new Tcp_Client();
            tcpClient.execute();

            timer = new Timer(true);
            timer.schedule(task, 0, 1000);
        }
    }

    public class btnsendonclicklistener implements View.OnClickListener {
        public void onClick(View v) {
            //mLocationManager.removeUpdates(locationListener);
            tcp = new Tcp_Client();
            tcp.execute(this);
        }
    }

    public class btnstoponclicklistener implements View.OnClickListener {
        public void onClick(View v) {
            //mLocationManager.removeUpdates(locationListener);
            System.exit(0);
        }
    }


    //GPS상태
    private boolean gpsIsOpen() {
        boolean bRet = true;

        LocationManager alm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity() , "Please, turn on GPS!", Toast.LENGTH_SHORT).show();
            bRet = false;
        } else {
            Toast.makeText(getActivity() , "GPS has turned on!", Toast.LENGTH_SHORT).show();
        }

        return bRet;
    }

    public void get_gps() {

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        String provider = mLocationManager.getBestProvider(new Criteria(), true); //GPS 정보
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        //mLocationManager.requestLocationUpdates(provider, 3 * 1000, 0, locationListener);
    }


    //GPS 정보 업데이트
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            if (location != null) {
                if (bf_lat.equals(String.valueOf(location.getLatitude())) && bf_log.equals(String.valueOf(location.getLongitude()))) {
                    //tcpip 사용 선언

                } else {
                    if(bf_lat.equals("null") || bf_lat.equals("")){
                        updateToNewView(location);
                        Tcp_Client tcpClient = new Tcp_Client();
                        tcpClient.execute();
                    }
                    else {
                        updateToNewView(location);
                        Tcp_Client tcpClient = new Tcp_Client();
                        tcpClient.execute();
//                        double num = calDistance(Double.parseDouble(bf_lat), Double.parseDouble(bf_log), location.getLatitude(), location.getLongitude());
//                        Toast.makeText(getActivity(), "거리 : " + num + "m", Toast.LENGTH_SHORT).show();

                    }
                }
            }

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    //레이아웃에 뿌리는 기능
    private void updateToNewView(Location location) {


        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            lat.setText(String.valueOf(latitude));
            log.setText(String.valueOf(longitude));


        } else {
            Toast.makeText(getActivity(), "Can't obtain the data!", Toast.LENGTH_SHORT).show();
        }
    }

    //tcpip 클라이언트 클래스
    public class Tcp_Client extends AsyncTask {

        protected String SERV_IP = ip_addr.getText().toString(); //server ip
        protected int PORT = 4000;
        final Socket socket = new Socket();

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                SocketAddress serverAddr = new InetSocketAddress(SERV_IP, PORT);
                socket.connect(serverAddr);
                DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
                try {
                    Log.i(TAG, "connect success");
                    WriteSocket(outToServer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outToServer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void WriteSocket(DataOutputStream data) throws IOException {
            //여기에 좌표 전송 코드 작성
//            data.writeBytes("Design System of Marine Structures based on Information Technology");

            String current_lat, current_log, device;
            current_lat = lat.getText().toString();
            current_log = log.getText().toString();


            if(bf_lat.equals("null") || bf_lat.equals("")){
                bf_lat = current_lat;
                bf_log = current_log;

                device = device_key.getText().toString();

                String time = tt_time.getText().toString();

                String query = current_lat + ',' + current_log + ',' + device + ',' + time;

                Log.i(TAG, query);

                data.writeBytes(current_lat + ',' + current_log + ',' + device);

            }else{
                if (calDistance(Double.parseDouble(bf_lat), Double.parseDouble(bf_log), Double.parseDouble(current_lat), Double.parseDouble(current_log)) < 10) {
                    bf_lat = current_lat;
                    bf_log = current_log;

                    device = device_key.getText().toString();

                    String time = tt_time.getText().toString();

                    String query = current_lat + ',' + current_log + ',' + device + ',' + time;

                    Log.i(TAG, query);

                    data.writeBytes(current_lat + ',' + current_log + ',' + device);

//            data.writeBytes(a + ',' + b);
//            data.writeDouble(2.1);
                    //전송내용 : 숫자 or 영어
                }
                else{
                    bf_lat = current_lat;
                    bf_log = current_log;
                }
            }




        }
    }


    //일정 간격으로 반복 실행하기 위한 용도
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    get_time();
                    break;
            }
        }
    };

    //반복 간격
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    // 시간 가져오기
    public void get_time() {

        now = new Date(System.currentTimeMillis());
        str_date = formatter.format(now);
        tt_time.setText(str_date);
    }

    double calculateDistancs(double lat1, double long1, double lat2,
                             double long2) {
        double earthRadius = 6371000; // meters

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(long2 - long1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        return dist;

    }

    public double calDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

