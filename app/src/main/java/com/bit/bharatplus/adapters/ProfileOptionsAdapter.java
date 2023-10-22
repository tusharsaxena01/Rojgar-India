package com.bit.bharatplus.adapters;

import static com.bit.bharatplus.databinding.OptionsListLayoutBinding.inflate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bit.bharatplus.databinding.OptionsListLayoutBinding;

import java.util.ArrayList;

public class ProfileOptionsAdapter extends ArrayAdapter {
    Context context;
    OptionsListLayoutBinding binding;
    ArrayList<Integer> drawables;
    ArrayList<String> options;
    public ProfileOptionsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Integer> drawables, ArrayList<String> options) {
        super(context, resource, options);
        this.context = context;
        this.drawables = drawables;
        this.options = options;
    }

    @Override
    public int getCount() {
        return options.size();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        binding = inflate(LayoutInflater.from(context), parent, false);

        binding.optionIcon.setImageResource(drawables.get(position));
        binding.optionText.setText(options.get(position));

        return binding.getRoot();
    }
}
