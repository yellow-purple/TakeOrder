package com.example.takeorder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeorder.fragment.eCountFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static android.content.Context.ACTIVITY_SERVICE;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    JSONArray mData;
    Context mContext;
    Socket msocket;
    Handler msghandler;



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView orderNumberView;
        public TextView orderTemperatureView;
        public TextView orderMenuView;
        public TextView orderMessageView;
        public Button confirmButton;
        public Button cancelButton;

        public ViewHolder(View itemView) {
            super(itemView);

            confirmButton = itemView.findViewById(R.id.confirm_order);
            cancelButton = itemView.findViewById(R.id.cancle_order);
            orderNumberView = itemView.findViewById(R.id.order_number);
            orderTemperatureView = itemView.findViewById(R.id.order_temperature);
            orderMenuView = itemView.findViewById(R.id.order_menu);
            orderMessageView = itemView.findViewById(R.id.order_message);
        }
    }

    public RecyclerViewAdapter(JSONArray jsonArray, Context context, Socket socket) {
        mData = jsonArray;
        mContext = context;
        msocket=socket;


    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_take_order_item, parent, false);
        RecyclerViewAdapter.ViewHolder  viewHolder = new RecyclerViewAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override

    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {
        try {
            final JSONObject jsonObject = mData.getJSONObject(holder.getAdapterPosition());

            holder.orderNumberView.setText(jsonObject.getString("number"));
            holder.orderTemperatureView.setText(jsonObject.getString("temperature"));
            holder.orderMenuView.setText(jsonObject.getString("menu"));
            holder.orderMessageView.setText(jsonObject.getString("message"));

            holder.confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(R.string.complete_order_message)
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {

                                @SuppressLint("NewApi")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendMenuForCount(jsonObject);

                                    final int itemPosition=holder.getAdapterPosition();

                                    SendThread sendThread= null;
                                    try {
                                        sendThread = new SendThread(msocket,jsonObject.getString("number"),itemPosition);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    sendThread.start();

                                    msghandler=new Handler(){
                                        public void handleMessage(Message msg){
                                            if(msg.what==11){
                                                notifyItemRemoved(itemPosition);
                                            }
                                        }
                                    };



                                }
                            })
                            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            });
            holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int itemPosition=holder.getAdapterPosition();


                    SendThread sendThread= null;
                    try {
                        sendThread = new SendThread(msocket,jsonObject.getString("number"),itemPosition);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendThread.start();

                    msghandler=new Handler(){
                        public void handleMessage(Message msg){
                            if(msg.what==11){
                                notifyItemRemoved(itemPosition);
                            }
                        }
                    };

                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }

    private void saveData(String stringKey,JSONArray jsonArray) {
        SharedPreferences sharedPreferences=mContext.getSharedPreferences("take order", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(stringKey, jsonArray.toString());
        editor.commit();
    }

    private void sendMenuForCount(JSONObject jsonObject){
        Message msg = new Message();
        msg.what = 0;
        Bundle bundle = new Bundle();
        try {
            bundle.putString("count",jsonObject.getString("menu"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        msg.setData(bundle);
        eCountFragment.handler.sendMessage(msg);
    }

    //data를 보냄
    class SendThread extends  Thread{
        private Socket msocket;
        String sendmsg;
        DataOutputStream output;
        int position;

        public SendThread(Socket socket,String msg,int itemPosition){
            this.msocket=socket;
            sendmsg=msg;
            position=itemPosition;
            try{

                output=new DataOutputStream(msocket.getOutputStream());

            }catch (IOException e){

            }
            catch (NullPointerException npe){
                Toast.makeText(mContext, "연결이 되지 않았습니다 새로고침 해주세요", Toast.LENGTH_SHORT).show();

            }
        }
        public void run(){
            try{
                Log.d(ACTIVITY_SERVICE,"sendThread "+sendmsg);

                if(output !=null){
                    output.writeUTF("TakeOrder" +" : "+sendmsg);
                    mData.remove(position);

                    msghandler.sendEmptyMessage(11);
                    saveData("one",mData);
                }

            }catch(IOException e){

            }
        }
    }
}
