package com.bit.bharatplus.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bit.bharatplus.R;
import com.bit.bharatplus.models.ProductModel;
import com.bit.bharatplus.utils.AndroidUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    Context context;
    private List<ProductModel> productList = new ArrayList<>();

    public void setProductList(List<ProductModel> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_product_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = productList.get(position);
//        holder.profession.setText(profession.getProduct());
//        holder.icon.setText(profession.getIconURL());

        holder.productName.setText(product.getProductName());
        String productPrice = "₹ "+product.getProductPrice();
        holder.productPrice.setText(productPrice);
        String productDiscount = "Discount: "+product.getProductDiscount()+ "%";
        holder.productDiscount.setText(productDiscount);
        if(product.getProductDiscount() < 1){
            holder.productDiscount.setVisibility(View.INVISIBLE);
        }
        String productRating = String.valueOf(product.getProductRating());
        holder.productRatings.setText(productRating);

        String imageUrl = product.getProductThumbnailURL();
        if(isValidContextForGlide(context)){
            if(imageUrl != null && !imageUrl.isEmpty())
                Glide.with(context)
                        .load(imageUrl)
                        .error(R.drawable.baseline_error_outline_24)
                        .fitCenter()
                        .into(holder.productImage);
        }else{
            AndroidUtils.showToast(context, "Unknown Error Occurred");
            Log.e("Product", "Product Image Setup Failed for position"+position);
        }

    }


    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            return !activity.isDestroyed() && !activity.isFinishing();
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productPrice;
        private TextView productDiscount;
        private TextView productRatings;

        private ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.ivProduct);
            productName = itemView.findViewById(R.id.tvProductName);
            productPrice = itemView.findViewById(R.id.tvProductPrice);
            productDiscount = itemView.findViewById(R.id.tvProductDiscount);
            productRatings = itemView.findViewById(R.id.tvProductRatings);

        }
    }
}
