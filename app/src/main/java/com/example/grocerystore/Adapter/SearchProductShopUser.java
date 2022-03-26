package com.example.grocerystore.Adapter;

import android.widget.Filter;

import com.example.grocerystore.Model.ProductSeller;

import java.util.ArrayList;
import java.util.List;

public class SearchProductShopUser extends Filter{
    private ShopDetailProductAdapter adapter ;
    private List<ProductSeller> list ;

    public SearchProductShopUser(ShopDetailProductAdapter adapter, List<ProductSeller> list) {
        this.adapter = adapter;
        this.list = list;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence charSequence) {
        Filter.FilterResults results = new Filter.FilterResults();

        if(charSequence!=null && charSequence.length()>0){
        //Check editSearch is not null
            charSequence = charSequence.toString().toUpperCase();

            ArrayList<ProductSeller> productSellers = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {

                //search by name and category products
                if(list.get(i).getName().toUpperCase().contains(charSequence) ||
                        list.get(i).getCategory().toUpperCase().contains(charSequence) ){
                    productSellers.add(list.get(i));
                }
            }
            results.count = productSellers.size();
            results.values = productSellers ;
        }else{
            results.count = list.size();
            results.values = list;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
        adapter.list = (ArrayList<ProductSeller>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
