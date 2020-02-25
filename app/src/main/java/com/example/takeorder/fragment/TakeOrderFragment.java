package com.example.takeorder.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeorder.R;
import com.example.takeorder.RecyclerViewAdapter;
import com.example.takeorder.activity.MainActivity;
import com.example.takeorder.item.OrderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class TakeOrderFragment extends Fragment {

    private String TAG="TakeOrderFragmentConnect";
    public Socket socket;
    Handler msghandler;
    ReceiveThread receive;
    SocketClient socketClient;
    RecyclerView recyclerView;


    JSONArray jArray;

    SharedPreferences sharedPreferences;
    RecyclerViewAdapter adapter;

    String jsonStatus;
    ArrayList<OrderItem> orderItemsArray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getContext().getSharedPreferences("take order", Context.MODE_PRIVATE);

        jArray=new JSONArray();
        orderItemsArray = new ArrayList<OrderItem>();
        loadData();

        msghandler=new Handler(){
            public void handleMessage(Message hdmsg){
                if(hdmsg.what==1111){
                    //ui처리할 부분 처리하기
                    String msg=hdmsg.obj.toString();
                    Log.d(TAG,msg);
                }else if(hdmsg.what==111){
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext())) ;
                    adapter = new RecyclerViewAdapter(jArray, getContext(),socket) ;
                    recyclerView.setAdapter(adapter) ;


                }
            }
        };


        SocketClient socketClient=new SocketClient();
        socketClient.start();



//        addOrder(0, "ICE","헤이즐넛 아메리카노", "연하게");
//        addOrder(1, "HOT","아메리카노", "연하게");
//        addOrder(2, "ICE","레몬에이드", "진하게");
//        addOrder(3, "HOT","카라멜마끼아또", "");
//        addOrder(4, "HOT","카페모카", "진하게");
//        addOrder(5, "HOT","초코 포인트치노", "연하게");
//        addOrder(6, "ICE","레몬에이드", "진하게");
//        addOrder(7, "HOT","유자에이드", "");
//        addOrder(8, "ICE","얼그레이", "진하게");
//        addOrder(9, "ICE","녹차", "연하게");
//        saveData("one");

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_take_order, container, false);
        recyclerView = root.findViewById(R.id.recycler_view) ;

        return root;
    }


    private void loadData() {
        jsonStatus = sharedPreferences.getString("one", "0");
        try {
            jArray= new JSONArray(jsonStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveData(String stringKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(stringKey, jArray.toString());//jArray는 JsonArray
        editor.commit();
    }

    private void addOrder(int number, String temperature, String menu, String message) {
        try {
            JSONObject jsonObject= new JSONObject();

            jsonObject.put("number", number);
            jsonObject.put("temperature", temperature);
            jsonObject.put("menu", menu);
            jsonObject.put("message", message);

            jArray.put(jsonObject);


        } catch (JSONException e) {
            Log.d(TAG, "Failed");

        }
    }



    class SocketClient extends Thread{
        boolean threadAlive;

        private DataOutputStream output=null;

        public SocketClient(){
            threadAlive=true;

        }
        public void run(){
            try{
                socket = new Socket("192.168.123.29", 5000);
                Log.d(TAG,"socket 접속 ");
                msghandler.sendEmptyMessage(111);

                output = new DataOutputStream(socket.getOutputStream());
                receive = new ReceiveThread(socket);
                receive.start();

                output.writeUTF("TakeOrder");

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //data를 받음
    class ReceiveThread extends Thread{
        private Socket socket;
        DataInputStream input;

        public ReceiveThread(Socket msocket){
            this.socket = msocket;
            try{
                input = new DataInputStream(socket.getInputStream());
            }catch (IOException e){
            }
        }

        public void run(){
            try{
                while(input !=null){
                    String msg =input.readUTF();
                    if(msg!=null){
                        Log.d(TAG,"receiveThread 실행");

                        Message hdmsg=msghandler.obtainMessage();
                        hdmsg.what=1111;
                        hdmsg.obj=msg;
                        msghandler.sendMessage(hdmsg);
                        Log.d(TAG,"Receive :"+hdmsg.obj.toString());
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    class SendThread extends  Thread{

        private Socket socket;
        String sendmsg;
        DataOutputStream output;
        public SendThread(Socket msocket,String msg){
            this.socket=msocket;
            sendmsg=msg;
            try{ output=new DataOutputStream(socket.getOutputStream());

            }catch (IOException e){

            }
        }

        public void run(){
            try{
                Log.d(TAG,"sendThread "+sendmsg);

                if(output !=null){
                    output.writeUTF(sendmsg);
                }
            }catch(IOException e){
                e.printStackTrace();
            }catch(NullPointerException npe){
                npe.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SendThread sendThread=new SendThread(socket,"close");
        sendThread.start();
    }

}
