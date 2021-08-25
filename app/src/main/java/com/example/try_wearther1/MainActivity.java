package com.example.try_wearther1;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private final Set<BluetoothDevice> discoveredDevices = new HashSet<>();
    private RecyclerViewAdapter recyclerViewAdapter;




    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) return;

                Log.d("onReceive", device.getName() + ":" + device.getAddress());

                discoveredDevices.add(device);
                updateList();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());





        recyclerViewAdapter = new RecyclerViewAdapter();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, filter);

        updateList();


    };


    private void updateList() {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView textName, textAddress;
        private BluetoothDevice device;
        private boolean isPaired;

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            textName = itemView.findViewById(R.id.textName);
            textAddress = itemView.findViewById(R.id.textAddress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent intent = new Intent(MainActivity.this, Navigation.class);
                    intent.putExtra("DeviceName", device.getName());
                    intent.putExtra("DeviceAddress", device.getAddress());
                    startActivity(intent);
                }
            });
        }

        void loadDevice(@NonNull BluetoothDevice device, boolean isPaired) {
            this.device = device;
            this.isPaired = isPaired;

            String name = this.device.getName();
            if (name == null) name = "裝置名稱未顯示";

            icon.setImageResource(this.isPaired ? R.drawable.ic_bluetooth_black_24dp : R.drawable.ic_bluetooth_searching_black_24dp);
            textName.setText(name);
            textAddress.setText(this.device.getAddress());
        }
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_device_item, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (position < pairedDevices.size()) {
                holder.loadDevice(pairedDevices.toArray(new BluetoothDevice[0])[position], true);
            } else {
                holder.loadDevice(discoveredDevices.toArray(new BluetoothDevice[0])[position - pairedDevices.size()], false);
            }
        }

        @Override
        public int getItemCount() {
            return bluetoothAdapter.getBondedDevices().size() + discoveredDevices.size();
        }
    }


}
