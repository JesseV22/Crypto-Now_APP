package com.example.cryptonow;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TopGainerAdapter extends RecyclerView.Adapter<TopGainerAdapter.TopGainerViewHolder> {

    private List<Crypto> topGainers;

    public TopGainerAdapter(List<Crypto> topGainers) {
        this.topGainers = new ArrayList<>(topGainers);
    }

    public void updateList(List<Crypto> newList) {
        this.topGainers.clear();
        this.topGainers.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopGainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_gainer, parent, false);
        return new TopGainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopGainerViewHolder holder, int position) {
        Crypto crypto = topGainers.get(position);
        holder.bind(crypto);
    }

    @Override
    public int getItemCount() {
        return topGainers.size();
    }

    static class TopGainerViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTopGainerName, tvTopGainerChange;
        private final ImageView ivTopGainerIcon;

        public TopGainerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTopGainerName = itemView.findViewById(R.id.tvTopGainerName);
            tvTopGainerChange = itemView.findViewById(R.id.tvTopGainerChange);
            ivTopGainerIcon = itemView.findViewById(R.id.ivTopGainerIcon);
        }

        public void bind(Crypto crypto) {
            tvTopGainerName.setText(crypto.getSymbol());

            double changePercent;
            try {
                changePercent = Double.parseDouble(crypto.getPriceChangePercent());
            } catch (NumberFormatException e) {
                changePercent = 0.0;
            }
            String changeText = String.format(Locale.US, "%.2f%%", changePercent);
            tvTopGainerChange.setText("+" + changeText);

            if (changePercent >= 0) {
                tvTopGainerChange.setTextColor(itemView.getResources().getColor(R.color.green));
            } else {
                tvTopGainerChange.setTextColor(itemView.getResources().getColor(R.color.red));
            }

            String imageUrl = crypto.getIconUrl();
            Log.d("TopGainerAdapter", "Carregando ícone para " + crypto.getSymbol() + ": " + imageUrl);

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .override(48, 48)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontTransform() // Evita transformações que podem degradar a qualidade
                            .encodeQuality(100)) // Garante alta qualidade na codificação
                    .placeholder(android.R.drawable.ic_menu_help)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(ivTopGainerIcon);
        }
    }
}