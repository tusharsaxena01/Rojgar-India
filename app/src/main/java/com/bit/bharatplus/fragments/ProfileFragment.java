package com.bit.bharatplus.fragments;

import static android.R.color.transparent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bit.bharatplus.R;
import com.bit.bharatplus.activities.CompleteProfileActivity;
import com.bit.bharatplus.activities.LoginActivity;
import com.bit.bharatplus.activities.NavigationActivity;
import com.bit.bharatplus.activities.SettingsActivity;
import com.bit.bharatplus.adapters.ProfileOptionsAdapter;
import com.bit.bharatplus.databinding.DialogConfirmBinding;
import com.bit.bharatplus.databinding.FragmentProfileBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    FirebaseAuth mAuth;
    ArrayList<Integer> drawables;
    ArrayList<String> options;
    ProfileOptionsAdapter adapter;
    SharedPreferences sp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mAuth = FirebaseAuth.getInstance();

        sp = requireActivity().getSharedPreferences("data", 0);

        addDrawablesToList();
        addOptionsToList();

        setCurrentUserDetails();

        adapter = new ProfileOptionsAdapter(requireContext(), R.layout.options_list_layout, drawables, options);
        binding.profileListView.setAdapter(adapter);

        binding.profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(options.get(position).equals("Settings")){
                    Log.d("check", "Settings");
                    requireActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
                } else if (options.get(position).equals("Help")) {
                    Log.d("check", "Help");
                } else if (options.get(position).equals("Logout")) {
                    Log.d("check", "Logout");

                    requireActivity().runOnUiThread(() -> {

                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        DialogConfirmBinding confirmDialogBinding = DialogConfirmBinding.inflate(LayoutInflater.from(requireContext()));
                        builder.setView(confirmDialogBinding.getRoot());

                        String message = "Do you want to Logout?";
                        confirmDialogBinding.tvMessage.setText(message);
                        AlertDialog dialog = builder.create();
                        dialog.getWindow().setBackgroundDrawableResource(transparent);

                        confirmDialogBinding.btnYes.setOnClickListener(v -> {
                            startActivity(new Intent(requireContext(), LoginActivity.class));
                            mAuth.signOut();
                            requireActivity().finish();
//                            getActivity().finish();
                        });

                        confirmDialogBinding.btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    });
                }
            }
        });

        return binding.getRoot();
    }

    private void setCurrentUserDetails() {
        binding.include.tvUsername.setText(sp.getString("CurrentUserName", "Error"));
        binding.include.tvPhoneNumber.setText(sp.getString("CurrentUserPhone", "Error"));
        Glide.with(requireContext())
                .load(sp.getString("CurrentProfilePictureURL", "https://cdn-icons-png.flaticon.com/512/149/149071.png"))
                .into(binding.include.ivProfile);
    }

    private void addOptionsToList() {
        options = new ArrayList<>();
        options.add("Settings");
        options.add("Help");
        options.add("Logout");
    }

    private void addDrawablesToList() {
        drawables = new ArrayList<>();
        drawables.add(R.drawable.baseline_settings_24);
        drawables.add(R.drawable.baseline_help_24);
        drawables.add(R.drawable.baseline_logout_24);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCurrentUserDetails();
    }
}
