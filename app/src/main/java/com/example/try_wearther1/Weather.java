package com.example.try_wearther1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather extends AppCompatActivity {
    private Button BTC, BTDC,Weather;
    private String address,newStr1,newStr2,newStr3,newStr4,newStr5;
    private BluetoothAdapter bluetoothAdapter;
    private final UUID serialPortUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private TextView today1,today2,today3,today4,today5,today6,today7,today8,today9,today10,Weak1,Weak2,Weak3,Weak4,Weak5,Weak6,tryText;
    private ImageView todayImage1,todayImage2,todayImage3,todayImage4,todayImage5,todayImage6,todayImage7;
    static String td1,td2,td3,td4,td5,wWeather;
    static int wWI;

    private final BroadcastReceiver Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                BTC.setEnabled(false);
                BTDC.setEnabled(true);
                Weather.setEnabled(true);
            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                BTC.setEnabled(true);
                BTDC.setEnabled(false);
                Weather.setEnabled(false);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect);
        BTC = findViewById(R.id.BTC);
        BTDC = findViewById(R.id.BTDC);
        Weather = findViewById(R.id.Navigation);
        today1 = findViewById(R.id.today1);
        today2 = findViewById(R.id.today2);
        today3 = findViewById(R.id.today3);
        today4 = findViewById(R.id.today4);
        today5 = findViewById(R.id.today5);
        today6 = findViewById(R.id.today6);
        today7 = findViewById(R.id.today7);
        today8 = findViewById(R.id.today8);
        today9 = findViewById(R.id.today9);
        today10 = findViewById(R.id.today10);

        Weak1 = findViewById(R.id.Weak1);
        Weak2 = findViewById(R.id.Weak2);
        Weak3 = findViewById(R.id.Weak3);
        Weak4 = findViewById(R.id.Weak4);
        Weak5 = findViewById(R.id.Weak5);
        Weak6 = findViewById(R.id.Weak6);
        todayImage1 = findViewById(R.id.todayImage1);
        todayImage2 = findViewById(R.id.todayImage2);
        todayImage3 = findViewById(R.id.todayImage3);
        todayImage4 = findViewById(R.id.todayImage4);
        todayImage5 = findViewById(R.id.todayImage5);
        todayImage6 = findViewById(R.id.todayImage6);
        todayImage7 = findViewById(R.id.todayImage7);

        tryText = findViewById(R.id.tryText);






        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        registerReceiver(Receiver, filter);

        address = getIntent().getStringExtra("DeviceAddress");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        BTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

                try {
                    socket = device.createRfcommSocketToServiceRecord(serialPortUUID);
                    socket.connect();
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        });


        BTDC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    socket.close();
                    socket = null;
                    inputStream = null;
                    outputStream = null;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        Weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    outputStream.write("weather".getBytes());
                    Thread.sleep(3000);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                read();

            }

        });

    }
    private void read() {

        try {

            byte[] bytes = new byte[4096];
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String Str = new String(bytes,"UTF-8");
/*
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss EEE");
            Date date = new Date();
            String dateToStr = dateFormat.format(date);

            //int NowTime = Integer.parseInt(dateToStr.substring(11,13));
            String NowTime = dateToStr.substring(6,10)+"-"+dateToStr.substring(3,5)+"-"+dateToStr.substring(0,2);
            */

            Weak2.setText("白天");
            Weak5.setText("晚上");

            Date tomorroDate=new Date();//取時間
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(tomorroDate);
            calendar.add(calendar.DATE,+1);//把日期往前減少一天，若想把日期向後推一天則將負數改為正數
            tomorroDate=calendar.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd EEE");
            String dateString = formatter.format(tomorroDate);


            Weak1.setText(dateString.substring(5,10));
            Weak4.setText(dateString.substring(11));


            if (Str.indexOf(dateString.substring(5,10))!=-1){
                int A=Str.indexOf(dateString.substring(5,10));
                Weak3.setText(Str.substring(A+24,A+26));

                if (Str.indexOf(dateString.substring(5,10),A+5)!=-1){
                    int B=Str.indexOf(dateString.substring(5,10),A+5);
                    Weak6.setText(Str.substring(B+24,B+26));

                    if (Str.indexOf(dateString.substring(5,10),B+5)!=-1) {
                        int C = Str.indexOf(dateString.substring(5, 10), B + 5);
                        Weak3.setText(Str.substring(C + 24, C + 26)+"°C~"+Str.substring(A+24,A+26)+"°C");

                        if (Str.indexOf(dateString.substring(5,10),C+5)!=-1) {
                            int D = Str.indexOf(dateString.substring(5, 10), C + 5);
                            Weak6.setText(Str.substring(D + 24, D + 26)+"°C~"+Str.substring(B+24,B+26)+"°C");
                        }
                    }

                }
            }

            if (Str.indexOf("value")!=-1) {
                wWI = Str.indexOf("value");
                wWeather = Str.substring(wWI+5, wWI + 20);

                if (wWeather.indexOf("多雲短暫陣雨或雷雨") != -1) {
                    todayImage6.setImageResource(R.drawable.osot);
                } else if (wWeather.indexOf("陰") != -1) {
                    todayImage6.setImageResource(R.drawable.cloudy);
                } else if (wWeather.indexOf("晴") != -1) {
                    todayImage6.setImageResource(R.drawable.clear_morning);
                } else if (wWeather.indexOf("短暫陣雨或雷雨") != -1) {
                    todayImage6.setImageResource(R.drawable.osot);
                } else if (wWeather.indexOf("多雲") != -1) {
                    todayImage6.setImageResource(R.drawable.partly_cloudy_morning);
                } else if (wWeather.indexOf("午後短暫雷陣雨") != -1) {
                    todayImage6.setImageResource(R.drawable.osot);
                }

            }


            if (Str.substring(wWI+20).indexOf("value")!=-1){

                wWI = Str.substring(wWI+20).indexOf("value")+wWI;
                wWeather = Str.substring(wWI+5,wWI+40);




                if (wWeather.indexOf("多雲短暫陣雨或雷雨") != -1) {
                    todayImage7.setImageResource(R.drawable.osot);
                } else if (wWeather.indexOf("陰") != -1) {
                    todayImage7.setImageResource(R.drawable.cloudy);
                } else if (wWeather.indexOf("晴") != -1) {
                    todayImage7.setImageResource(R.drawable.clear_night);
                } else if (wWeather.indexOf("短暫陣雨或雷雨") != -1) {
                    todayImage7.setImageResource(R.drawable.osot);
                } else if (wWeather.indexOf("多雲") != -1) {
                    todayImage7.setImageResource(R.drawable.partly_cloudy_night);
                }else if (wWeather.indexOf("午後短暫雷陣雨") != -1) {
                    todayImage7.setImageResource(R.drawable.osot);
                }
            }




            if (Str.indexOf("('")!=-1){
                int f=Str.indexOf("('");
                today1.setText(Str.substring(f+2,f+7));
                td1 = Str.substring(f+2,f+7);

                newStr1 = Str.substring(0,57);




                if (Str.indexOf("('",f+5)!=-1){
                    int g=Str.indexOf("('",f+5);
                    today2.setText(Str.substring(g+2,g+7));
                    td2 = Str.substring(g+2,g+7);

                    if (Str.indexOf("('",g+5)!=-1) {
                        int h = Str.indexOf("('", g + 5);
                        today3.setText(Str.substring(h+2,h+7));
                        td3 = Str.substring(h+2,h+7);

                        if (Str.indexOf("('",h+5)!=-1) {
                            int i = Str.indexOf("('",  h + 5);
                            today4.setText(Str.substring(i+2,i+7));
                            td4 = Str.substring(i+2,i+7);

                            if (Str.indexOf("('", i + 5) != -1) {
                                int j = Str.indexOf("('", i + 5);
                                today5.setText(Str.substring(j+2,j+7));
                                td5 = Str.substring(j+2,j+7);
                            }
                        }
                    }

                }
            }








            newStr1 = Str.substring(0,57);

            if (newStr1.indexOf("多雲")!=-1) {
                int a = newStr1.indexOf("多雲");
                today6.setText(newStr1.substring(a + 10, a + 13));
                tryText.setText(td1);
                if (td1.equals("06:00") | td1.equals("09:00") | td1.equals("12:00") | td1.equals("15:00")) {
                    todayImage1.setImageResource(R.drawable.partly_cloudy_morning);
                }
                else {
                    todayImage1.setImageResource(R.drawable.partly_cloudy_night);
                }


            }


            else if (newStr1.indexOf("陰")!=-1){
                int a = newStr1.indexOf("陰");
                today6.setText(newStr1.substring(a + 9, a + 12));
                todayImage1.setImageResource(R.drawable.cloudy);
            }
            else if (newStr1.indexOf("晴")!=-1){
                int a = newStr1.indexOf("晴");
                today6.setText(newStr1.substring(a + 9, a + 12));
                if (td1.equals("06:00") | td1.equals("09:00") | td1.equals("12:00") | td1.equals("15:00")) {
                    todayImage1.setImageResource(R.drawable.clear_morning);
                }
                else{
                    todayImage1.setImageResource(R.drawable.clear_night);
                }
            }
            else if (newStr1.indexOf("短暫陣雨")!=-1) {
                int a = newStr1.indexOf("短暫陣雨");
                today6.setText(newStr1.substring(a + 12, a + 15));
                todayImage1.setImageResource(R.drawable.oshowers);
            }
            else if (newStr1.indexOf("短暫陣雨或雷雨")!=-1){
                int a = newStr1.indexOf("短暫陣雨或雷雨");
                today6.setText(newStr1.substring(a + 15, a + 18));
                todayImage1.setImageResource(R.drawable.osot);
            }

            else if (newStr1.indexOf("午後短暫雷陣雨")!=-1){
                int a = newStr1.indexOf("午後短暫雷陣雨");
                today6.setText(newStr1.substring(a + 15, a + 18));
                todayImage1.setImageResource(R.drawable.osot);
            }



            newStr2 = Str.substring(60,120);

            if (newStr2.indexOf("多雲")!=-1){
                int b=newStr2.indexOf("多雲");
                today7.setText(newStr2.substring(b+10,b+13));
                if (td2.equals("06:00") | td2.equals("09:00") | td2.equals("12:00") | td2.equals("15:00")) {
                    todayImage2.setImageResource(R.drawable.partly_cloudy_morning);
                }
                else{
                    todayImage2.setImageResource(R.drawable.partly_cloudy_night);
                }
            }
            else if (newStr2.indexOf("陰")!=-1){
                int b=newStr2.indexOf("陰");
                today7.setText(newStr2.substring(b+9,b+12));
                todayImage2.setImageResource(R.drawable.cloudy);
            }
            else if (newStr2.indexOf("晴")!=-1) {
                int b = newStr2.indexOf("晴");
                today7.setText(newStr2.substring(b + 9, b + 12));
                if (td2.equals("06:00") | td2.equals("09:00") | td2.equals("12:00") | td2.equals("15:00")) {
                    todayImage2.setImageResource(R.drawable.clear_morning);
                }
                else{
                    todayImage2.setImageResource(R.drawable.clear_night);
                }
            }
            else if (newStr2.indexOf("短暫陣雨")!=-1) {
                int b = newStr2.indexOf("短暫陣雨");
                today7.setText(newStr2.substring(b + 12, b + 15));
                todayImage2.setImageResource(R.drawable.oshowers);
            }
            else if (newStr2.indexOf("短暫陣雨或雷雨")!=-1) {
                int b = newStr2.indexOf("短暫陣雨或雷雨");
                today7.setText(newStr2.substring(b + 15, b + 18));
                todayImage2.setImageResource(R.drawable.osot);
            }
            else if (newStr2.indexOf("午後短暫雷陣雨")!=-1) {
                int b = newStr2.indexOf("午後短暫雷陣雨");
                today7.setText(newStr2.substring(b + 15, b + 18));
                todayImage2.setImageResource(R.drawable.osot);
            }





            newStr3 = Str.substring(123,180);
            
            if (newStr3.indexOf("多雲")!=-1) {
                int c=newStr3.indexOf("多雲");
                today8.setText(newStr3.substring(c+10, c+13));
                if (td3.equals("06:00") | td3.equals("09:00") | td3.equals("12:00") | td3.equals("15:00")) {
                    todayImage3.setImageResource(R.drawable.partly_cloudy_morning);
                }
                else{
                    todayImage3.setImageResource(R.drawable.partly_cloudy_night);
                }

            }
            else if (newStr3.indexOf("陰")!=-1){
                int c=newStr3.indexOf("陰");
                today8.setText(newStr3.substring(c+9,c+12));
                todayImage3.setImageResource(R.drawable.cloudy);
            }
            else if (newStr3.indexOf("晴")!=-1){
                int c=newStr3.indexOf("晴");
                today8.setText(newStr3.substring(c+9,c+12));
                if (td3.equals("06:00") | td3.equals("09:00") | td3.equals("12:00") | td3.equals("15:00")) {
                    todayImage3.setImageResource(R.drawable.clear_morning);
                }
                else{
                    todayImage3.setImageResource(R.drawable.clear_night);
                }
            }
            else if (newStr3.indexOf("短暫陣雨")!=-1) {
                int c = newStr3.indexOf("短暫陣雨");
                today8.setText(newStr3.substring(c + 12, c + 15));
                todayImage3.setImageResource(R.drawable.oshowers);
            }

            else if (newStr3.indexOf("短暫陣雨或雷雨")!=-1){
                int c=newStr3.indexOf("短暫陣雨或雷雨");
                today8.setText(newStr3.substring(c+15,c+18));
                todayImage3.setImageResource(R.drawable.osot);
            }

            else if (newStr3.indexOf("午後短暫雷陣雨")!=-1){
                int c=newStr3.indexOf("午後短暫雷陣雨");
                today8.setText(newStr3.substring(c+15,c+18));
                todayImage3.setImageResource(R.drawable.osot);
            }



            newStr4 = Str.substring(185,240);

            if (newStr4.indexOf("多雲")!=-1) {
                int d=newStr4.indexOf("多雲");
                today9.setText(newStr4.substring(d+10, d+13));
                if (td4.equals("06:00") | td4.equals("09:00") | td4.equals("12:00") | td4.equals("15:00")) {
                    todayImage4.setImageResource(R.drawable.partly_cloudy_morning);
                }
                else{
                    todayImage4.setImageResource(R.drawable.partly_cloudy_night);
                }
            }
            else if (newStr4.indexOf("陰")!=-1){
                int d=newStr4.indexOf("陰");
                today9.setText(newStr4.substring(d+9,d+12));
                todayImage4.setImageResource(R.drawable.cloudy);
            }
            else if (newStr4.indexOf("晴")!=-1){
                int d=newStr4.indexOf("晴");
                today9.setText(newStr4.substring(d+9,d+12));
                if (td4.equals("06:00") | td4.equals("09:00") | td4.equals("12:00") | td4.equals("15:00")) {
                    todayImage4.setImageResource(R.drawable.clear_morning);
                }
                else{
                    todayImage4.setImageResource(R.drawable.clear_night);
                }
            }
            else if (newStr4.indexOf("短暫陣雨")!=-1) {
                int d = newStr4.indexOf("短暫陣雨");
                today9.setText(newStr4.substring(d + 12, d + 15));
                todayImage4.setImageResource(R.drawable.oshowers);
            }

            else if (newStr4.indexOf("短暫陣雨或雷雨")!=-1){
                int d=newStr4.indexOf("短暫陣雨或雷雨");
                today9.setText(newStr4.substring(d+15,d+18));
                todayImage4.setImageResource(R.drawable.osot);
            }
            else if (newStr4.indexOf("午後短暫雷陣雨")!=-1){
                int d=newStr4.indexOf("午後短暫雷陣雨");
                today9.setText(newStr4.substring(d+15,d+18));
                todayImage4.setImageResource(R.drawable.osot);
            }


            newStr5 = Str.substring(240,300);

            if (newStr5.indexOf("多雲")!=-1) {
                int e=newStr5.indexOf("多雲");
                today10.setText(newStr5.substring(e+10, e+13));
                if (td5.equals("06:00") | td5.equals("09:00") | td5.equals("12:00") | td5.equals("15:00")) {
                    todayImage5.setImageResource(R.drawable.partly_cloudy_morning);
                }
                else{
                    todayImage5.setImageResource(R.drawable.partly_cloudy_night);
                }
            }
            else if (newStr5.indexOf("陰")!=-1){
                int e=newStr5.indexOf("陰");
                today10.setText(newStr5.substring(e+9,e+12));
                todayImage5.setImageResource(R.drawable.cloudy);
            }
            else if (newStr5.indexOf("晴")!=-1){
                int e=newStr5.indexOf("晴");
                today10.setText(newStr5.substring(e+9,e+12));
                if (td5.equals("06:00") | td5.equals("09:00") | td5.equals("12:00") | td5.equals("15:00")) {
                    todayImage5.setImageResource(R.drawable.clear_morning);
                }
                else{
                    todayImage5.setImageResource(R.drawable.clear_night);
                }
            }
            else if (newStr5.indexOf("短暫陣雨")!=-1) {
                int e = newStr5.indexOf("短暫陣雨");
                today10.setText(newStr5.substring(e + 12, e + 15));
                todayImage5.setImageResource(R.drawable.oshowers);
            }

            else if (newStr5.indexOf("短暫陣雨或雷雨")!=-1){
                int e=newStr5.indexOf("短暫陣雨或雷雨");
                today10.setText(newStr5.substring(e+15,e+18));
                todayImage5.setImageResource(R.drawable.osot);
            }

            else if (newStr5.indexOf("午後短暫雷陣雨")!=-1){
                int e=newStr5.indexOf("午後短暫雷陣雨");
                today10.setText(newStr5.substring(e+15,e+18));
                todayImage5.setImageResource(R.drawable.osot);
            }





        }catch (IOException e) {
            e.printStackTrace();
        }

    }








    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onStop(){
        super.onStop();
    }




}
