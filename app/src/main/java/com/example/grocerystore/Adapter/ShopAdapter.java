package com.example.grocerystore.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.Activity.DetailShopActivity;
import com.example.grocerystore.Model.ShopModel;
import com.example.grocerystore.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private Context context ;
    ArrayList<ShopModel> shopModels ;

    public ShopAdapter(Context context, ArrayList<ShopModel> shopModels) {
        this.context = context;
        this.shopModels = shopModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ShopModel shopModel = shopModels.get(position);

            String accountType = shopModel.getAccountType();
            String name = shopModel.getName();
            String shop_name = shopModel.getShop_name();
            String phone = shopModel.getPhone();
            String address_shop = shopModel.getAddress_shop();
            double latitue = shopModel.getLatitue();
            double longtitue = shopModel.getLongtitue();
            String timeStamp = shopModel.getTimeStamp();
            String img_Profile = shopModel.getImg_Profile();
            String uId = shopModel.getuId();
            String online = shopModel.getOnline();
            String open = shopModel.getOpen();

            holder.name_ShopUser.setText(shop_name);
            holder.phone_ShopUser.setText(phone);
            holder.address_ShopUser.setText(address_shop);

            if(online.equals("true")){
                holder.img_ShopOnline.setVisibility(View.VISIBLE);
            }else {
                holder.img_ShopOnline.setVisibility(View.GONE);
            }

            if(open.equals("true")){
                holder.shop_Close.setVisibility(View.GONE);
            }else {
                holder.shop_Close.setVisibility(View.GONE);
            }

            Picasso.get().load(img_Profile).into(holder.img_ShopUser);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context , DetailShopActivity.class);
                    intent.putExtra("shopId" , uId);
                    context.startActivity(intent);
                }
            });


    }

    @Override
    public int getItemCount() {
        return shopModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img_ShopUser ;
        ImageView img_ShopOnline ;
        RatingBar rating_ShopUser ;
        TextView name_ShopUser , phone_ShopUser ,address_ShopUser ,shop_Close ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_ShopUser = itemView.findViewById(R.id.img_ShopUser);
            img_ShopOnline = itemView.findViewById(R.id.img_ShopOnline);
            rating_ShopUser = itemView.findViewById(R.id.rating_ShopUser);
            name_ShopUser = itemView.findViewById(R.id.name_ShopUser);
            phone_ShopUser = itemView.findViewById(R.id.phone_ShopUser);
            address_ShopUser = itemView.findViewById(R.id.address_ShopUser);
            shop_Close = itemView.findViewById(R.id.shop_Close);
        }
    }
}
