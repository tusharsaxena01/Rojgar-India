package com.bit.bharatplus.adapters;

import static com.bit.bharatplus.databinding.OptionsListLayoutBinding.*;

import android.content.Context;
import java.lang.Integer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bit.bharatplus.R;
import com.bit.bharatplus.databinding.OptionsListLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class ProfileOptionsAdapter extends ArrayAdapter {
    Context context;
//    LayoutInflater layoutInflator;
    OptionsListLayoutBinding binding;
    ArrayList<Integer> drawables;
    ArrayList<String> options;
    public ProfileOptionsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Integer> drawables, ArrayList<String> options) {
        super(context, resource, options);
        this.context = context;
        this.drawables = drawables;
        this.options = options;
//        layoutInflator = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return options.size();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View view = layoutInflator.inflate(R.layout.options_list_layout, null);
//        convertView = LayoutInflater.from(context).inflate(R.layout.options_list_layout, parent, false);
        binding = inflate(LayoutInflater.from(context), parent, false);

        binding.optionIcon.setImageResource(drawables.get(position));
        binding.optionText.setText(options.get(position));

        return binding.getRoot();
    }
}
