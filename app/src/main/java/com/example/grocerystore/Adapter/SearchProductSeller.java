package com.example.grocerystore.Adapter;

import android.widget.Filter;

import com.example.grocerystore.Adapter.ProductSellerAdapter;
import com.example.grocerystore.Model.ProductSeller;

import java.util.ArrayList;
import java.util.List;

public class SearchProductSeller extends Filter {
    private ProductSellerAdapter adapter ;
    private List<ProductSeller> list ;

    public SearchProductSeller(ProductSellerAdapter adapter, List<ProductSeller> list) {
        this.adapter = adapter;
        this.list = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();

        if(charSequence!=null && charSequence.length()>0){

            charSequence = charSequence.toString().toUpperCase();

            ArrayList<ProductSeller> productSellers = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {

                //search by name and category
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
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.productSellers = (ArrayList<ProductSeller>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
