package com.bu.safeguard.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bu.safeguard.R;
import com.bu.safeguard.chatOperation.chatHistoryModel;
import com.bu.safeguard.chatOperation.chatPage;
import com.bu.safeguard.models.modelForProfile;
import com.bu.safeguard.viewHolders.chatListingViewholdeers;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



public class FriendsChatFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    LinearLayoutManager llm;
    DatabaseReference userRef, fref;
    FirebaseRecyclerAdapter<chatHistoryModel, chatListingViewholdeers> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<chatHistoryModel> options;
    Context context;
    String uid;
    public FriendsChatFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends_chat_list, container, false);
        uid = FirebaseAuth.getInstance().getUid();
        context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        llm = new LinearLayoutManager(context);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);


        loadData();


        return view;
    }

    private void loadData() {

        DatabaseReference histroyref = FirebaseDatabase.getInstance().getReference("chatHistory");

        Query query = histroyref.orderByChild("timestamp");

        fref = FirebaseDatabase.getInstance().getReference("profile");
        fref.keepSynced(true);


        histroyref.keepSynced(true);


        options = new FirebaseRecyclerOptions.Builder<chatHistoryModel>()
                .setQuery(query, chatHistoryModel.class)
                .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<chatHistoryModel, chatListingViewholdeers>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final chatListingViewholdeers viewholder, final int i, @NonNull final chatHistoryModel chatHistoryModel) {


                if (chatHistoryModel.getUser1().equals(uid)) {
                    //  user1 is me // user2 is my friend
                    // download the friend data
                    fref.child(chatHistoryModel.getUser2()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            modelForProfile model = dataSnapshot.getValue(modelForProfile.class);
                            viewholder.setNameTv(model.getNickName());
                            viewholder.setPp(model.getPpLink(), context);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    viewholder.setLastMsgTv(chatHistoryModel.getLastMsg()+"");



                } else if (chatHistoryModel.getUser2().equals(uid)) {

                    //  user2  is me // user1 is my friend
                    // download the friend data
                    fref.child(chatHistoryModel.getUser1()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            modelForProfile model = dataSnapshot.getValue(modelForProfile.class);
                            viewholder.setNameTv(model.getNickName());
                            viewholder.setPp(model.getPpLink(), context);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    viewholder.setLastMsgTv(chatHistoryModel.getLastMsg()+"");

                } else {
                    //this row not mine
                    viewholder.container.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = viewholder.container.getLayoutParams();
                    params.height = 0;
                    viewholder.container.setLayoutParams(params);

                }


                viewholder.setOnClickListener(new chatListingViewholdeers.ClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {


                        String frindShipId = getItem(position).getFrindShipId();

                        if (getItem(position).getUser1().equals(uid)) {
                            String friendUserID = getItem(position).getUser2();
                            Intent o = new Intent(context, chatPage.class);

                            o.putExtra("frindShipId", frindShipId);
                            o.putExtra("friendUserID", friendUserID);
                            o.putExtra("group", "no");
                            o.putExtra("groupID", "model");
                            startActivity(o);

                            // Toast.makeText(context , friendUserID , Toast.LENGTH_SHORT).show();
                        } else {
                            String friendUserID = getItem(position).getUser1();
                            Intent o = new Intent(context, chatPage.class);

                            o.putExtra("frindShipId", frindShipId);
                            o.putExtra("friendUserID", friendUserID);
                            o.putExtra("group", "no");
                            o.putExtra("groupID", "model");
                            //Toast.makeText(context , friendUserID , Toast.LENGTH_SHORT).show();
                            startActivity(o);
                        }


                    }
                });


            }

            @NonNull
            @Override
            public chatListingViewholdeers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_for_chat_display, parent, false);

                final chatListingViewholdeers viewholder = new chatListingViewholdeers(view);


                return viewholder;
            }


        };

        recyclerView.setLayoutManager(llm);
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }


}
