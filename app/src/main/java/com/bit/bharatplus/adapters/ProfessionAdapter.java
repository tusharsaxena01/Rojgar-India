package com.bit.bharatplus.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bit.bharatplus.R;
import com.bit.bharatplus.activities.ButtonClickHomeActivity;
import com.bit.bharatplus.models.ProfessionModel;
import com.bit.bharatplus.utils.AndroidUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProfessionAdapter extends RecyclerView.Adapter<ProfessionAdapter.ProfessionViewHolder> {
    Context context;
    private List<ProfessionModel> professionList = new ArrayList<>();

    public void setProfessionList(List<ProfessionModel> professionList) {
        this.professionList = professionList;
    }

    @NonNull
    @Override
    public ProfessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_profession_layout, parent, false);
        context = parent.getContext();
        return new ProfessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessionViewHolder holder, int position) {
        ProfessionModel profession = professionList.get(position);
        holder.profession.setText(profession.getProfession());

        String imageUrl = profession.getIconURL();
        if(isValidContextForGlide(context)){
            if(imageUrl != null && !imageUrl.isEmpty())
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.baseline_person_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .centerInside()
                        .into(holder.icon);
        }else{
            AndroidUtils.showToast(context, "Unknown Error Occurred");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ButtonClickHomeActivity.class);
            intent.putExtra("name", "profession");
            intent.putExtra("Profession name", profession.getProfession());
            context.startActivity(intent);
        });

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
        return professionList.size();
    }

    public static class ProfessionViewHolder extends RecyclerView.ViewHolder {
        private final TextView profession;
        private final ImageView icon;

        public ProfessionViewHolder(@NonNull View itemView) {
            super(itemView);
            profession = itemView.findViewById(R.id.tvProfession);
            icon = itemView.findViewById(R.id.ivProfession);
        }
    }
}
