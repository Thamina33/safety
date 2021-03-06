package com.bu.safeguard.viewHolders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bu.safeguard.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class viewholderForAllUser extends RecyclerView.ViewHolder {

    View mView;
    public  TextView nameTv, ssTv  ;
    public  ImageView pp ;
    public  LinearLayout containerLayout ;



    public viewholderForAllUser(@NonNull View itemView) {
        super(itemView);
        mView = itemView;



        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

    }

    public  void setPp(String link , Context context ) {
        pp = (ImageView) mView.findViewById(R.id.pponFriendreq);

        Glide.with(context).load(link).diskCacheStrategy(DiskCacheStrategy.ALL).into(pp) ;
    }



     public  void setDetails(Context context , String name , String pplInk) {
         // views
           // containerLayout = itemView.findViewById(R.id.container);
            nameTv  = (TextView) mView.findViewById(R.id.nameOnFriendReq);
            pp = (ImageView) mView.findViewById(R.id.pponFriendreq);
            ssTv = mView.findViewById(R.id.sstv);
            ssTv.setVisibility(View.INVISIBLE);

           nameTv.setText(name);
           Glide.with(context)
                   .load(pplInk)
                   .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                   .into(pp) ;




     }


    private static ClickListener mClickListener;


    public interface ClickListener {
        void onItemClick(View view, int position);

    }


    public  void setOnClickListener(ClickListener clickListener) {

        mClickListener = clickListener;

    }
}
