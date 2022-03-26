package com.example.grocerystore.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grocerystore.Activity.DetailShopActivity;
import com.example.grocerystore.Model.ProductSeller;
import com.example.grocerystore.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailProductAdapter extends RecyclerView.Adapter<ShopDetailProductAdapter.ViewHolder> implements Filterable {

    private final Context context ;
    ArrayList<ProductSeller> list , filterList ;
    private SearchProductShopUser productShopUser ;
    public ShopDetailProductAdapter(Context context, ArrayList<ProductSeller> list) {
        this.context = context;
        this.list = list;
        this.filterList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_detail , parent , false);

        return new ViewHolder(view);
    }
    public String uId ;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductSeller seller = list.get(position);

        String productId = seller.getProductId();
        String name = seller.getName();
        String description = seller.getDescription();
        String category = seller.getCategory();
        String quantity = seller.getQuantity();
        String realPrice = seller.getRealPrice();
        String discount = seller.getDiscount();
        String discountNote = seller.getDiscountNote();
        String img_Url = seller.getImg_Url();
        String discountAvailable = seller.getDiscountAvailable();
        String timeStamp = seller.getTimeStamp();
        uId = seller.getuId();

        holder.txt_NameProductDetail.setText(name);
        holder.txt_DescriptionDetail.setText(description);
        holder.txt_DiscountProductDetail.setText(discount + "$");
        holder.txt_PriceProductDetail.setText(realPrice+"$");
        holder.txt_discountNoteDetail.setText(discountNote);

        if(discountAvailable.equals("true")){
            holder.txt_DiscountProductDetail.setVisibility(View.VISIBLE);
            holder.txt_discountNoteDetail.setVisibility(View.VISIBLE);
            holder.txt_PriceProductDetail.setPaintFlags(holder.txt_PriceProductDetail.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.txt_DiscountProductDetail.setVisibility(View.GONE);
            holder.txt_discountNoteDetail.setVisibility(View.GONE);
            holder.txt_PriceProductDetail.setPaintFlags(0);
        }

        Picasso.get().load(img_Url).into(holder.img_ProductDetailShop);

        //
        holder.addToCartDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogProduct(seller);
            }
        });
        //
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showProduct(seller);
            }
        });


    }
//Add to cart
    private double cost = 0 , finalCost = 0 ;
    private int numberPro = 0 ;
    public String productId ;
    private void showDialogProduct(ProductSeller seller) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add , null);

        CircleImageView img_ProductAdd = view.findViewById(R.id.img_ProductAdd);
        TextView discountNote_ProductAdd = view.findViewById(R.id.discountNote_ProductAdd);
        TextView name_ProductAdd = view.findViewById(R.id.name_ProductAdd);
        TextView quantyti_ProductAdd = view.findViewById(R.id.quantyti_ProductAdd);
        TextView description_ProductAdd = view.findViewById(R.id.description_ProductAdd);
        TextView discount_ProductAdd = view.findViewById(R.id.discount_ProductAdd);
        TextView price_ProductAdd = view.findViewById(R.id.price_ProductAdd);
        TextView total_Money = view.findViewById(R.id.total_Money);
        TextView txt_numberProduct = view.findViewById(R.id.txt_numberProduct);
        ImageView btn_add = view.findViewById(R.id.btn_add);
        ImageView btn_remove = view.findViewById(R.id.btn_remove);
        Button btn_AddToCart = view.findViewById(R.id.btn_AddToCart);

        productId = seller.getProductId();
        String name = seller.getName();
        String description = seller.getDescription();
        String quantity = seller.getQuantity();
        String discount = seller.getDiscount();
        String discountNote = seller.getDiscountNote();
        String img_Url = seller.getImg_Url();
        String discountAvailable = seller.getDiscountAvailable();
        String realPrice = seller.getRealPrice();

        Picasso.get().load(img_Url).into(img_ProductAdd);
        name_ProductAdd.setText(name);
        quantyti_ProductAdd.setText(quantity);
        description_ProductAdd.setText(description);
        discount_ProductAdd.setText(discount +"$");
        discountNote_ProductAdd.setText(discountNote);
        price_ProductAdd.setText(realPrice +"$");

        final String price ;
        if(discountAvailable.equals("true")){
            price = seller.getDiscount();
            total_Money.setText("Tổng tiền: "+ price+"$");
            discountNote_ProductAdd.setVisibility(View.VISIBLE);
            price_ProductAdd.setPaintFlags(price_ProductAdd.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            discountNote_ProductAdd.setVisibility(View.GONE);
            discount_ProductAdd.setVisibility(View.GONE);
            price = seller.getRealPrice();
            total_Money.setText("Tổng tiền: "+ price+"$");
        }

        cost = Double.parseDouble(price.replaceAll("$" , ""));
        finalCost = Double.parseDouble(price.replaceAll("$" , ""));
        numberPro = 1 ;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog alertDialog = builder.show() ;
        alertDialog.show();
    // Add
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalCost = finalCost + cost ;
                numberPro++ ;
                total_Money.setText(""+ finalCost);
                txt_numberProduct.setText(""+numberPro);
            }
        });
        //remove
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numberPro>1){
                    finalCost = finalCost - cost ;
                    numberPro--;
                    total_Money.setText(""+finalCost);
                    txt_numberProduct.setText(""+ numberPro);
                }
            }
        });

        btn_AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_ProductAdd.getText().toString().trim();
                String priceEach = price ;
                String totalPrice = total_Money.getText().toString();
                String numberPro = txt_numberProduct.getText().toString().trim();

                //Add to sqlite database
                addToCart(uId , productId , name , priceEach , totalPrice , numberPro);

                alertDialog.dismiss();

            }
        });



    }

    private int itemId = 1 ;
    private void addToCart(String uId, String productId, String name, String priceEach, String totalPrice, String numberPro) {
        itemId++ ;
        EasyDB easyDB = EasyDB.init(context , "ECommerce_DB")
                .setTableName("CART_ITEM")
                .addColumn(new Column("Id_Item" , new String[]{"text" , "unique"}))
                .addColumn(new Column("ProductId" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Name" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Price_Each" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Number" , new String[]{"text" , "not null"}))
                .addColumn(new Column("Total_Price" , new String[]{"text" , "not null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Id_Item" , itemId)
                .addData("ProductId" , productId)
                .addData("Name" , name)
                .addData("Price_Each" , priceEach)
                .addData("Number" , numberPro)
                .addData("Total_Price" , totalPrice)
                .doneDataAdding();

        ((DetailShopActivity) context).cartCount();

        Toast.makeText(context , "Thêm thành công" , Toast.LENGTH_SHORT).show();
    }

    private void showProduct(ProductSeller seller) {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.detail_pro_user , null);

        sheetDialog.setContentView(view);

        ImageView back = view.findViewById(R.id.back_DetailToMainUser);
        CircleImageView img_Product = view.findViewById(R.id.img_DetailProductUser);
        TextView nameIT = view.findViewById(R.id.detail_NameProductUser);
        TextView discountNoteIT = view.findViewById(R.id.detail_DiscountNoteUser);
        TextView descriptionIT = view.findViewById(R.id.detail_DescriptionUser);
        TextView categoryIT = view.findViewById(R.id.detail_CategoryProductUser);
        TextView quantityIT = view.findViewById(R.id.detail_QuantityProductUser);
        TextView discountIT = view.findViewById(R.id.detail_DiscountUser);
        TextView priceIT = view.findViewById(R.id.detail_PriceUser);

        String id = seller.getProductId();
        String uId = seller.getuId();
        String name = seller.getName();
        String discount = seller.getDiscount();
        String discountNote = seller.getDiscountNote();
        String category = seller.getCategory();
        String quantity = seller.getQuantity();
        String price = seller.getRealPrice();
        String img_Url = seller.getImg_Url();
        String description = seller.getDescription();
        String timeStamp = seller.getTimeStamp();
        String discountAvailable = seller.getDiscountAvailable();

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
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    @Override
    public Filter getFilter() {
        if(productShopUser==null){
            productShopUser = new SearchProductShopUser(this , filterList);
        }
        return productShopUser;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_ProductDetailShop ;
        TextView txt_NameProductDetail , txt_DescriptionDetail , txt_DiscountProductDetail ,
                txt_PriceProductDetail ,txt_discountNoteDetail , addToCartDetail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_ProductDetailShop = itemView.findViewById(R.id.img_ProductDetailShop);
            txt_NameProductDetail = itemView.findViewById(R.id.txt_NameProductDetail);
            txt_DescriptionDetail = itemView.findViewById(R.id.txt_DescriptionDetail);
            txt_DiscountProductDetail = itemView.findViewById(R.id.txt_DiscountProductDetail);
            txt_PriceProductDetail = itemView.findViewById(R.id.txt_PriceProductDetail);
            txt_discountNoteDetail = itemView.findViewById(R.id.txt_discountNoteDetail);
            addToCartDetail = itemView.findViewById(R.id.addToCartDetail);

        }
    }
}
