package com.bit.bharatplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bit.bharatplus.activities.ButtonClickHomeActivity;
import com.bit.bharatplus.activities.JobPostActivity;
import com.bit.bharatplus.fragments.ShopFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        // Customize the bottom sheet content if needed

        TextView item1 = view.findViewById(R.id.nav_item1);
        TextView item2 = view.findViewById(R.id.nav_item2);
        TextView item3 = view.findViewById(R.id.nav_item3);
        TextView item4 = view.findViewById(R.id.nav_item4);
        TextView item5 = view.findViewById(R.id.nav_item5);

        item1.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ButtonClickHomeActivity.class);
            intent.putExtra("name", "jobs");
            startActivity(intent);
        });

        item2.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ButtonClickHomeActivity.class);
            intent.putExtra("name", "workers");
            startActivity(intent);
        });

        item3.setOnClickListener(v -> requireActivity().startService(new Intent(requireContext(), LocationService.class)));

        item4.setOnClickListener(v -> startActivity(new Intent(requireContext(), JobPostActivity.class)));

        item5.setOnClickListener(v -> {

            ShopFragment currentFragment = new ShopFragment();

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragContainer, currentFragment)
                    .addToBackStack(null)
                    .commit();

        });

        return view;
    }
}
