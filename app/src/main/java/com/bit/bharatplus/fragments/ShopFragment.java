package com.bit.bharatplus.fragments;

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

import com.bit.bharatplus.adapters.ProfessionAdapter;
import com.bit.bharatplus.classes.ProfessionModel;
import com.bit.bharatplus.databinding.FragmentShopBinding;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
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
    ProfessionAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        // setup profession recycler
//        binding.recyclerProfession.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerProfession.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ProfessionAdapter();
        binding.recyclerProfession.setAdapter(adapter);
        fetchProfession();

        return binding.getRoot();
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
