package com.example.try_wearther1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class Navigation extends AppCompatActivity {
    private Button BTC, BTDC,Navigation,enter;
    private String address;
    private BluetoothAdapter bluetoothAdapter;
    private final UUID serialPortUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private TextView trytext;
    private EditText input_goal;
    private TextInputLayout textInputLayout;
    private ImageView MAP,goal_mark,start_mark;
    static String addString;


    private final BroadcastReceiver Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                BTC.setEnabled(false);
                BTDC.setEnabled(true);
                Navigation.setEnabled(true);

            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                BTC.setEnabled(true);
                BTDC.setEnabled(false);
                Navigation.setEnabled(false);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        BTC = findViewById(R.id.BTC);
        BTDC = findViewById(R.id.BTDC);

        Navigation = findViewById(R.id.Navigation);
        MAP = findViewById(R.id.MAP);

        input_goal = findViewById(R.id.input_goal);
        goal_mark = findViewById(R.id.goal_mark);
        start_mark =findViewById(R.id.start_mark);

        enter = findViewById(R.id.enter);

        trytext = findViewById(R.id.trytext);

        textInputLayout =findViewById(R.id.textInputLayout);




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
                MAP.setVisibility(View.INVISIBLE);
                input_goal.setVisibility(View.INVISIBLE);
                enter.setVisibility(View.INVISIBLE);
                textInputLayout.setVisibility(View.INVISIBLE);
                trytext.setText("");

            }
        });

        input_goal.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {

                send();



                return false;
            }

        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                send();

            }


        });





        Navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    outputStream.write("navigation".getBytes());
                    Thread.sleep(3000);
                    MAP.setVisibility(View.VISIBLE);
                    input_goal.setVisibility(View.VISIBLE);
                    enter.setVisibility(View.VISIBLE);
                    textInputLayout.setVisibility(View.VISIBLE);




                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }


           }

        });

    }
    private void read() {

        try {

            Thread.sleep(3000);

            byte[] bytes = new byte[4096];
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String Str = new String(bytes, "UTF-8");

            //String Sgoal = Str.substring(Str.length()-3);

            if (Str.equals("Unknow Enter")){
                trytext.setText(Str);
                start_mark.setVisibility(View.INVISIBLE);
                goal_mark.setVisibility(View.INVISIBLE);
                return;
            }
            if (Str.substring(0,5).equals("Start")){
                trytext.setText(Str);
                start_mark.setVisibility(View.INVISIBLE);
                goal_mark.setVisibility(View.INVISIBLE);
                return;
            }

            int separate = Str.indexOf("]");
            int separate_left = Str.indexOf("[")+1;
            String route = Str.substring(separate_left,separate);
            addString="";
            String[] y = route.split(", ");
            //int intRoute = 0;
            for(int i = 0;i<y.length;i++){

                if (y[i].equals("1")){
                    addString=addString+"A>";
                }else if (y[i].equals("2")){
                    addString=addString+"B>";
                }else if (y[i].equals("3")){
                    addString=addString+"C>";
                }else if (y[i].equals("4")){
                    addString=addString+"D>";
                }else if (y[i].equals("5")){
                    addString=addString+"E>";
                }else if (y[i].equals("6")){
                    addString=addString+"F>";
                }else if (y[i].equals("7")){
                    addString=addString+"G>";
                }else if (y[i].equals("8")){
                    addString=addString+"H>";
                }else if (y[i].equals("9")){
                    addString=addString+"I>";
                }else if (y[i].equals("10")){
                    addString=addString+"J>";
                }else if (y[i].equals("11")){
                    addString=addString+"K>";
                }

            }
            addString = addString.substring(0,addString.length()-1);



            String Coordinate = Str.substring(2,separate);

            if (Coordinate.substring(Coordinate.length()-2,Coordinate.length()-1).equals(" ")){
                //終點個位數
                String last_Coordinate = Coordinate.substring(Coordinate.length()-1);
                if (last_Coordinate.equals("1")){
                    //終點為A
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {

                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);

                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:A"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(112);
                    goal_mark.setTranslationY(275);
                }else if (last_Coordinate.equals("2")){
                    //終點為B
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:B"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(725);
                    goal_mark.setTranslationY(330);
                }else if (last_Coordinate.equals("3")){
                    //終點為C
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:C"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(112);
                    goal_mark.setTranslationY(910);
                }else if (last_Coordinate.equals("4")){
                    //終點為D
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:D"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(495);
                    goal_mark.setTranslationY(910);
                }else if (last_Coordinate.equals("5")){
                    //終點為E
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:E"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(112);
                    goal_mark.setTranslationY(1240);
                }else if (last_Coordinate.equals("6")){
                    //終點為F
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:F"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(495);
                    goal_mark.setTranslationY(1240);
                }else if (last_Coordinate.equals("7")){
                    //終點為G
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:G"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(697);
                    goal_mark.setTranslationY(1240);
                }else if (last_Coordinate.equals("8")){
                    //終點為H
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:H"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(890);
                    goal_mark.setTranslationY(1240);
                }else if (last_Coordinate.equals("9")){
                    //終點為I
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:I"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(697);
                    goal_mark.setTranslationY(1410);
                }


            }else if (!Coordinate.substring(Coordinate.length()-2,Coordinate.length()-1).equals(" ")){
                String last_Coordinate = Coordinate.substring(Coordinate.length() - 2);
                //終點兩位數
                if (last_Coordinate.equals("10")){
                    //終點為J
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:J"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(495);
                    goal_mark.setTranslationY(1520);
                }else if (last_Coordinate.equals("11")){
                    //終點為K
                    if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("1")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:A"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(275);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("2")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:B"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(725);
                        start_mark.setTranslationY(330);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("3")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:C"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("4")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:D"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("5")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:E"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(112);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("6")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:F"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(910);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("7")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:G"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("8")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:H"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1240);
                    }else if (Str.substring(Str.indexOf(")")-1,Str.indexOf(")")).equals("9")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:I"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(697);
                        start_mark.setTranslationY(1410);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("10")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:J"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(495);
                        start_mark.setTranslationY(1520);
                    }else if (Str.substring(Str.indexOf(")")-2,Str.indexOf(")")-1).equals("11")) {
                        String Path_length = Str.substring(separate+4,Str.length()-5);
                        String[] x = Path_length.split(" ");
                        int count = 0;
                        for(int i = 0;i<x.length;i++) {
                            count = count+Integer.parseInt(x[i]);
                        }
                        trytext.setText("起點為:K"+"\n"+"終點為:K"+"\n"+addString+","+"路徑長為:"+count);
                        start_mark.setVisibility(View.VISIBLE);
                        start_mark.setTranslationX(890);
                        start_mark.setTranslationY(1520);
                    }
                    goal_mark.setVisibility(View.VISIBLE);
                    goal_mark.setTranslationX(890);
                    goal_mark.setTranslationY(1520);
                }
            }




        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void send(){

        try {
            outputStream.write(input_goal.getText().toString().getBytes());
            outputStream.flush();
            input_goal.setText("");




            read();
        } catch (IOException e) {
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


