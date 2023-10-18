package com.bit.bharatplus.fragments;

import static com.google.firebase.database.util.JsonMapper.parseJson;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonArrayRequest;
import com.bit.bharatplus.adapters.ImageSliderAdapter;
import com.bit.bharatplus.adapters.ProductAdapter;
import com.bit.bharatplus.adapters.ProfessionAdapter;
import com.bit.bharatplus.models.ProductModel;
import com.bit.bharatplus.models.ProfessionModel;
import com.bit.bharatplus.databinding.FragmentShopBinding;
import com.bit.bharatplus.utils.AndroidUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShopFragment extends Fragment {
    FragmentShopBinding binding;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    ProfessionAdapter professionAdapter;
    ProductAdapter productAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // setup profession recycler
        binding.recyclerProfession.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false));
        professionAdapter = new ProfessionAdapter();
        binding.recyclerProfession.setAdapter(professionAdapter);
        fetchProfession();
        setupBannerSwitcher();

        binding.recyclerProducts.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        productAdapter = new ProductAdapter();
        binding.recyclerProducts.setAdapter(productAdapter);
        fetchProducts();

        binding.recyclerProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager = (GridLayoutManager)binding.recyclerProducts.getLayoutManager();
                boolean isScrolledToTop = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;

                if(isScrolledToTop){
                    fadeInView(binding.tvHeaderProfessionals);
                    fadeInView(binding.vpBanner);
                    fadeInView(binding.recyclerProfession);
                }else{
                    fadeOutView(binding.tvHeaderProfessionals);
                    fadeOutView(binding.vpBanner);
                    fadeOutView(binding.recyclerProfession);

                }
            }
        });

        // hide the profession and banner if the products are scrolled
//
//        binding.recyclerProducts.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                int startScroll = oldScrollY;
//                if (scrollY > 1) {
//                    // Scrolled down
//                    binding.tvHeaderProfessionals.setVisibility(View.GONE);
//                    binding.recyclerProfession.setVisibility(View.GONE);
//                    binding.vpBanner.setVisibility(View.GONE);
//                } else{
//                    // Scrolled up
//                    binding.tvHeaderProfessionals.setVisibility(View.VISIBLE);
//                    binding.recyclerProfession.setVisibility(View.VISIBLE);
//                    binding.vpBanner.setVisibility(View.VISIBLE);
//                }
//            }
//        });




        return binding.getRoot();
    }


    private void fadeInView(View view) {
        view.animate()
                .alpha(1f)
                .setDuration(500)
                .withStartAction(() -> view.setVisibility(View.VISIBLE));
    }

    private void fadeOutView(View view) {
        view.animate()
                .alpha(0f)
                .setDuration(800)
                .withStartAction(() -> view.setVisibility(View.GONE));
    }

    private void fetchProducts() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://dummyjson.com/products";
        Request request = new Request.Builder()
                .url(url) // Replace with your JSON file URL
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle request failure
                Log.e("Fetch Products", e.getMessage());
                AndroidUtils.showAlertDialog(requireContext(), "Error", e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String json = response.body().string();

                    // Parse the JSON data into a list of products
                    List<ProductModel> productList = parseJsonForProduct(json);

                    Log.e("profession", productList.toString());

                    // Update the UI on the main thread
                    requireActivity().runOnUiThread(() -> {
                        productAdapter.setProductList(productList);
                        productAdapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    private List<ProductModel> parseJsonForProduct(String json) {
        List<ProductModel> productList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray productsArray = jsonObject.getJSONArray("products");

            for (int i = 0; i < productsArray.length(); i++) {
                JSONObject productObject = productsArray.getJSONObject(i);

                int id = productObject.getInt("id");
                String title = productObject.getString("title");
                String description = productObject.getString("description");
                double price = productObject.getDouble("price");
                double discountPercentage = productObject.getDouble("discountPercentage");
                double rating = productObject.getDouble("rating");
                int stock = productObject.getInt("stock");
                String brand = productObject.getString("brand");
                String category = productObject.getString("category");
                String thumbnail = productObject.getString("thumbnail");
                JSONArray imagesArray = productObject.getJSONArray("images");
                List<String> images = new ArrayList<>();
                for (int j = 0; j < imagesArray.length(); j++) {
                    images.add(imagesArray.getString(j));
                }

                ProductModel product = new ProductModel();
                product.setProductId(id);
                product.setProductName(title);
                product.setProductDescription(description);
                product.setProductPrice(price);
                product.setProductDiscount(discountPercentage);
                product.setProductRating(rating);
                product.setProductStocks(stock);
                product.setProductBrand(brand);
                product.setProductCategory(category);
                product.setProductThumbnailURL(thumbnail);
                product.setProductImages(images);

                productList.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return productList;
    }


    private void setupBannerSwitcher() {
         ImageSliderAdapter imageSliderAdapter;
         List<String> imageUrls = new ArrayList<>();

         imageSliderAdapter = new ImageSliderAdapter(requireActivity().getApplicationContext(), imageUrls);
         binding.vpBanner.setAdapter(imageSliderAdapter);
         // fetching the urls from the firebase storage

        StorageReference storageRef = storage.getReference("banner_images");
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference item: listResult.getItems()){
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrls.add(uri.toString());
                            imageSliderAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            AndroidUtils.showAlertDialog(requireContext(), "Error", e.getMessage());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AndroidUtils.showAlertDialog(requireContext(), "Error", e.getMessage());
            }
        });



    }

    private void fetchProfession() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://raw.githubusercontent.com/tusharsaxena01/Bharat-Plus/master/app/data/professions.json";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    String jsonData = response.body().string();
                    Gson gson = new Gson();
                    Type professionListType = new TypeToken<List<ProfessionModel>>(){}.getType();
                    List<ProfessionModel> professionList = gson.fromJson(jsonData, professionListType);

                    Log.e("profession", professionList.toString());

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            professionAdapter.setProfessionList(professionList);
                            professionAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }

}
