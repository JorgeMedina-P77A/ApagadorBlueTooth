package com.example.jorge.controllights;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    private ImageView imgSW;
    private TextView txtSW;

    public int control = 0;

    BluetoothAdapter mBluetoothAdapter ;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;
    OutputStream mOutputStream;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        connect();

        inicializarUI();

    }

    private void inicializarUI(){

        imgSW = (ImageView) findViewById(R.id.imageSW);
        imgSW.setOnLongClickListener(this);

        txtSW = (TextView) findViewById(R.id.txt_sw);

    }

    @Override
    public boolean onLongClick(View v) {

        if(v.equals(imgSW)){

            if(control==0){



                imgSW.setImageResource(R.drawable.green);
                txtSW.setText("ON");
                sendBT("1");
                control=1;

            }else if(control==1){



                imgSW.setImageResource(R.drawable.red);
                txtSW.setText("OFF");
                sendBT("0");
                control=0;

            }

        }

        return false;

    }



    public void connect(){

        if (mBluetoothSocket != null && mBluetoothSocket.isConnected()) {
            try {
                endBT();
            } catch (IOException e) {
                showMessage(e.getMessage(), Toast.LENGTH_LONG);
            }
        } else {
            try {
                startBT();
            } catch (IOException e) {
                showMessage(e.getMessage(), Toast.LENGTH_LONG);
            }
        }

    }

    private void startBT() throws IOException{

        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            showMessage("Dispositivo No tiene Blueetooth", Toast.LENGTH_LONG);
            return;
        }

        if(!mBluetoothAdapter.isEnabled()){
            Intent intentBTEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(intentBTEnable,1001);
        }

        Set<BluetoothDevice> devicesVinculados =  mBluetoothAdapter.getBondedDevices();
        boolean blnVinculado = false;
        for(BluetoothDevice item : devicesVinculados)
        {
            if(item.getName().equals("ProtoLights")){
                mBluetoothDevice = item;
                blnVinculado = true;
                showMessage("Dispositivo Vinculado", Toast.LENGTH_SHORT);
            }
        }

        if(blnVinculado){
            openBT();
        }else{
            showMessage("No se ha encontrado el dispositivo Arduino", Toast.LENGTH_SHORT);
        }

    }

    private void openBT() throws IOException{
//        UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//        UUID  uuid = UUID.fromString("00001101-0000-8000-00805f9b34fb");
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        mBluetoothSocket =  mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        mBluetoothSocket.connect();
        mOutputStream=mBluetoothSocket.getOutputStream();
        //enableSwitxh(mBluetoothSocket.isConnected());
        showMessage("Conectado a Arduino", Toast.LENGTH_LONG);

    }

    private void showMessage(String message, int lengthLong) {

        Toast.makeText(this,message,lengthLong).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==1001 && requestCode==1){
            showMessage("Se Activo Bluetoot", Toast.LENGTH_SHORT);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void endBT() throws IOException{

        mOutputStream.close();
        mBluetoothSocket.close();
        //enableSwitxh(mBluetoothSocket.isConnected());

    }

    private void sendBT(String signal) {

        String msj = signal;
        msj += "\n";

        try {

            mOutputStream.write(msj.getBytes());
            showMessage("Datos enviados", Toast.LENGTH_SHORT);

        }catch (IOException e){
            showMessage(e.getMessage(), Toast.LENGTH_LONG);
        }

    }









}
