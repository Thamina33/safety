package com.bu.safeguard.chatOperation;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bu.safeguard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class chatPage extends AppCompatActivity {
    DatabaseReference mref, histroyref;
    RecyclerView recyclerView;
    LinearLayoutManager llm;
    List<chatMsgModel> loadedChat;
    chatAdapter chatAdapter;
    EditText chatINput;
    ImageView sendBtn;
    String frindShipId, friendUserID, friendUserName, friendPP;
    String uid;
    String name = "", userImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        recyclerView = findViewById(R.id.list);
        chatINput = findViewById(R.id.message_input);
        sendBtn = findViewById(R.id.message_send_btn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        uid = FirebaseAuth.getInstance().getUid();


//        o.putExtra("frindShipId" , frindShipId) ;
//        o.putExtra("friendUserID" , friendUserID) ;
//        o.putExtra("Firend_Username" , friendUserName) ;
//        o.putExtra("Firend_pp" , friendPP) ;

        Intent intent = getIntent();


        getSupportActionBar().setTitle("Chat");

        llm = new LinearLayoutManager(getApplicationContext());
        llm.setStackFromEnd(true);


        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(llm);

        if (getIntent().getStringExtra("group").contains("yes")) {
            friendUserID = "";
            frindShipId = "";
            mref = FirebaseDatabase.getInstance().getReference(intent.getStringExtra("groupID")).child("msg");
            histroyref = FirebaseDatabase.getInstance().getReference(intent.getStringExtra("groupID")).child("lastMsg");

        } else {
            friendUserID = intent.getStringExtra("friendUserID");
            frindShipId = intent.getStringExtra("frindShipId");
          //  mref = FirebaseDatabase.getInstance().getReference("chat_repo");
           histroyref = FirebaseDatabase.getInstance().getReference("chatHistory");
        }


        if (frindShipId.isEmpty() && !getIntent().getStringExtra("group").contains("yes")) {


            AlertDialog dialog = new AlertDialog.Builder(chatPage.this).create();
            dialog.setTitle("Something went Wrong !!");
            dialog.show();


        } else {

            downloadMsg();
        }


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = chatINput.getText().toString();


                if (msg.isEmpty()) {

                } else {

                    sendTheMsg(msg);
                    chatINput.setText("");

                }

            }
        });


    }

    private void checKIfOldConversionExists() {


        //downloadMsg() ;

        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    downloadMsg();
                } else {

                    // sendng 1st msg

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendTheMsg(@NonNull String msg) {

        String msg_ID = mref.push().getKey();
        // String msg , msg_id , uid , time   ;

        final chatMsgModel msgModel = new chatMsgModel(msg.trim(), msg_ID, uid, "null", "null", System.currentTimeMillis() / 1000, name, userImage);


        mref.child(msg_ID).setValue(msgModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                chatINput.setText("");

                if (!getIntent().getStringExtra("group").contains("yes")) {
                    writeChatHistory(uid, friendUserID, frindShipId, msgModel.msg);
                }


                // chatINput.getText().clear();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //  downloadTheFriendData();

    }

//    private void downloadTheFriendData() {
//
//        DatabaseReference fref = FirebaseDatabase.getInstance().getReference("profile");
//        fref.keepSynced(true);
//
//        fref.child(friendUserID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                modelForProfile model = dataSnapshot.getValue(modelForProfile.class);
//
//                friendUserName = model.getNickName();
//
//                getSupportActionBar().setTitle("Chatting With " + friendUserName);
//
//                friendPP = model.getPpLink();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    private void downloadMsg() {

        if (!getIntent().getStringExtra("group").contains("yes")) {
            mref = FirebaseDatabase.getInstance().getReference("chat_repo").child(frindShipId);
        } else {
            mref = FirebaseDatabase.getInstance().getReference("groupList").child(getIntent().getStringExtra("groupID")).child("msg");
        }


        loadedChat = new ArrayList<>();

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    loadedChat.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        chatMsgModel chat = ds.getValue(chatMsgModel.class);

                        // Log.d("MSGESDSS" , chat.getMsg()  );
                        loadedChat.add(chat);
                    }
                    //
                    chatAdapter = new chatAdapter(getApplicationContext(), loadedChat);
                    chatAdapter.notifyDataSetChanged();

                    // set adapter
                    recyclerView.setAdapter(chatAdapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


    }

    private void writeChatHistory(String uid, String friendUserID, String FrienshipID, String msg) {
        // user1  , user2 = uid   , friendUserID
        // FrienshipID  = FrienshipID
        // long  timestamp = lastMsg of  the friends

        //    chatHistoryModel historyModel = new chatHistoryModel(uid, friendUserID, FrienshipID, System.currentTimeMillis() / 1000);

        HashMap<String, Object> map = new HashMap<>();
        map.put("user1", uid);
        map.put("user2", friendUserID);
        map.put("frindShipId", FrienshipID);
        map.put("timeStamp", System.currentTimeMillis() / 1000);
        map.put("lastMsg", msg);
        map.put("lsatMsgSender", uid);
        histroyref.child(FrienshipID).updateChildren(map);


    }

    @Override
    protected void onResume() {
        super.onResume();

        getdata();
    }

    private void getdata() {

        SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
//            PH1 = prefs.getString("ph1", "No name defined");//"No name defined" is the default value.
//            PH2 = prefs.getString("ph2", "No name defined");
//            PH3 = prefs.getString("ph3", "No name defined");
        name = prefs.getString("name", "User");
//            personalPh = prefs.getString("pph", "none");
        userImage = prefs.getString("uimage", "User");


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
