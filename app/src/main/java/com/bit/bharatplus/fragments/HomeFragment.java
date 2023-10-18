package com.bit.bharatplus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bit.bharatplus.LocationService;
import com.bit.bharatplus.MyBottomSheetDialogFragment;
import com.bit.bharatplus.R;
import com.bit.bharatplus.activities.ButtonClickHomeActivity;
import com.bit.bharatplus.adapters.ProfessionAdapter;
import com.bit.bharatplus.databinding.FragmentHomeBinding;
import com.bit.bharatplus.models.ProfessionModel;
import com.bit.bharatplus.utils.AndroidUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private View topNavigationDrawer;
    private Animation slideDownAnimation, slideUpAnimation;
    private boolean isDrawerOpen = false;
    FragmentHomeBinding binding;
    FirebaseAuth mAuth;
    ProfessionAdapter professionAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();


        // setup profession recycler
        binding.recyclerProfession.setLayoutManager(new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false));
        professionAdapter = new ProfessionAdapter();
        binding.recyclerProfession.setAdapter(professionAdapter);
        fetchProfession();

        binding.findJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ButtonClickHomeActivity.class);
                intent.putExtra("name", "jobs");
                startActivity(intent);
            }
        });
        binding.findWorkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ButtonClickHomeActivity.class);
                intent.putExtra("name", "workers");
                startActivity(intent);
            }
        });
        binding.updateLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().startService(new Intent(requireContext(), LocationService.class));
            }
        });

        binding.btnOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        return binding.getRoot();
    }

    private void showBottomSheetDialog() {
        MyBottomSheetDialogFragment bottomSheetDialogFragment = new MyBottomSheetDialogFragment();
        bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());

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
