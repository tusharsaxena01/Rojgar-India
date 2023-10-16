package com.bit.bharatplus.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bit.bharatplus.adapters.ImageSliderAdapter;
import com.bit.bharatplus.adapters.ProfessionAdapter;
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

import com.google.gson.reflect.TypeToken;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShopFragment extends Fragment {
    FragmentShopBinding binding;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    ProfessionAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // setup profession recycler
        binding.recyclerProfession.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ProfessionAdapter();
        binding.recyclerProfession.setAdapter(adapter);
        fetchProfession();


        setupBannerSwitcher();

        return binding.getRoot();
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
                            adapter.setProfessionList(professionList);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }

}
