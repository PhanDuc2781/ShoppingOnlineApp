package com.example.grocerystore.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.Activity.EditProductActivity;
import com.example.grocerystore.Activity.MainSellerActivity;
import com.example.grocerystore.Model.ProductSeller;
import com.example.grocerystore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductSellerAdapter extends RecyclerView.Adapter<ProductSellerAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<ProductSeller> productSellers  , filterList;
    private SearchProductSeller filterProduct;

    FirebaseAuth auth ;

    public ProductSellerAdapter(Context context, ArrayList<ProductSeller> productSellers) {
        this.context = context;
        this.productSellers = productSellers;
        this.filterList = productSellers;
    }

    @NonNull
    @Override
    public ProductSellerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        auth = FirebaseAuth.getInstance();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_products_seller , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSellerAdapter.ViewHolder holder, int position) {
        ProductSeller productSeller = productSellers.get(position);
        String id = productSeller.getProductId();
        String uId = productSeller.getuId();
        String name = productSeller.getName();
        String discount = productSeller.getDiscount();
        String discountNote = productSeller.getDiscountNote();
        String category = productSeller.getCategory();
        String quantity = productSeller.getQuantity();
        String price = productSeller.getRealPrice();
        String img_Url = productSeller.getImg_Url();
        String timeStamp = productSeller.getTimeStamp();
        String discountAvailable = productSeller.getDiscountAvailable();


        //set Data
        holder.txt_NameProduct.setText(name);
        holder.txt_PriceProduct.setText(price+"$");
        holder.txt_discountNote.setText(discountNote);
        holder.txt_DiscountProduct.setText(discount+"$");
        holder.txt_quantityProduct.setText(quantity);

        Picasso.get().load(img_Url).into(holder.img_Product);

        if(discountAvailable.equals("true")){
            holder.txt_DiscountProduct.setVisibility(View.VISIBLE);
            holder.txt_discountNote.setVisibility(View.VISIBLE);
            holder.txt_PriceProduct.setPaintFlags(holder.txt_PriceProduct.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.txt_DiscountProduct.setVisibility(View.GONE);
            holder.txt_discountNote.setVisibility(View.GONE);
            holder.txt_PriceProduct.setPaintFlags(0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailBottomSheet(productSeller);
            }
        });
    }

    private void detailBottomSheet(ProductSeller productSeller) {

        BottomSheetDialog sheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.detail_product , null);

        sheetDialog.setContentView(view);

        ImageView back = view.findViewById(R.id.back_DetailToMainSeller);
        ImageView edit = view.findViewById(R.id.edit_ProductSeller);
        ImageView remove = view.findViewById(R.id.remove_ProductSeller);
        CircleImageView img_Product = view.findViewById(R.id.img_DetailProductSeller);
        TextView nameIT = view.findViewById(R.id.detail_NameProduct);
        TextView discountNoteIT = view.findViewById(R.id.detail_DiscountNote);
        TextView descriptionIT = view.findViewById(R.id.detail_Description);
        TextView categoryIT = view.findViewById(R.id.detail_CategoryProduct);
        TextView quantityIT = view.findViewById(R.id.detail_QuantityProduct);
        TextView discountIT = view.findViewById(R.id.detail_Discount);
        TextView priceIT = view.findViewById(R.id.detail_Price);

        String id = productSeller.getProductId();
        String uId = productSeller.getuId();
        String name = productSeller.getName();
        String discount = productSeller.getDiscount();
        String discountNote = productSeller.getDiscountNote();
        String category = productSeller.getCategory();
        String quantity = productSeller.getQuantity();
        String price = productSeller.getRealPrice();
        String img_Url = productSeller.getImg_Url();
        String description = productSeller.getDescription();
        String timeStamp = productSeller.getTimeStamp();
        String discountAvailable = productSeller.getDiscountAvailable();

        nameIT.setText("Tên : " + name);
        discountNoteIT.setText( discountNote);
        descriptionIT.setText("Mô tả : " +description);
        categoryIT.setText("Loại : " +category);
        quantityIT.setText("Số lượng : " +quantity);
        priceIT.setText("Giá : " + price+" $");

        if(discountAvailable.equals("true")){
            discountIT.setVisibility(View.VISIBLE);
            discountNoteIT.setVisibility(View.VISIBLE);
            priceIT.setPaintFlags(priceIT.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            discountIT.setVisibility(View.GONE);
            discountNoteIT.setVisibility(View.GONE);
        }

        Picasso.get().load(img_Url).into(img_Product);

        sheetDialog.show();
        //
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetDialog.dismiss();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , EditProductActivity.class);
                intent.putExtra("currentProduct" , timeStamp);
                context.startActivity(intent);
            }
        });
//
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Bạn chắc chắn muốn xóa không ?");
                        alertDialogBuilder.setPositiveButton("Có",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                        reference.child(auth.getUid()).child("Products").child(timeStamp).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(context , "Đã Xóa" , Toast.LENGTH_SHORT).show();
                                                    context.startActivity(new Intent(context , MainSellerActivity.class));

                                                }else {
                                                    Toast.makeText(context , ""+task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productSellers.size();
    }

    @Override
    public Filter getFilter() {
        if(filterProduct==null){
            filterProduct = new SearchProductSeller(this , filterList);
        }
        return filterProduct;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView img_Product ;
        TextView txt_discountNote , txt_NameProduct ,txt_quantityProduct , txt_DiscountProduct , txt_PriceProduct ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_Product = itemView.findViewById(R.id.img_Product);
            txt_NameProduct = itemView.findViewById(R.id.txt_NameProduct);
            txt_discountNote = itemView.findViewById(R.id.txt_discountNote);
            txt_quantityProduct = itemView.findViewById(R.id.txt_quantityProduct);
            txt_PriceProduct = itemView.findViewById(R.id.txt_PriceProduct);
            txt_DiscountProduct = itemView.findViewById(R.id.txt_DiscountProduct);
        }
    }
}
