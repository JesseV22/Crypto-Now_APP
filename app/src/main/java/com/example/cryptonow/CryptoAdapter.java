package com.example.cryptonow;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder> {

    private List<Crypto> cryptoList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Crypto crypto);
    }

    public CryptoAdapter(List<Crypto> cryptoList, OnItemClickListener listener) {
        this.cryptoList = new ArrayList<>(cryptoList);
        this.listener = listener;
    }

    public void updateList(List<Crypto> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CryptoDiffCallback(cryptoList, newList));
        cryptoList.clear();
        cryptoList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public CryptoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crypto, parent, false);
        return new CryptoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoViewHolder holder, int position) {
        Crypto crypto = cryptoList.get(position);
        holder.bind(crypto, listener);
    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    static class CryptoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvSymbol, tvPrice, tvChange, tvMarketCap;
        private final ImageView ivCryptoIcon;

        public CryptoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSymbol = itemView.findViewById(R.id.tvCryptoSymbol);
            tvPrice = itemView.findViewById(R.id.tvCryptoPrice);
            tvChange = itemView.findViewById(R.id.tvCryptoChange);
            tvMarketCap = itemView.findViewById(R.id.tvMarketCap);
            ivCryptoIcon = itemView.findViewById(R.id.ivCryptoIcon);
        }

        public void bind(Crypto crypto, CryptoAdapter.OnItemClickListener listener) {
            tvSymbol.setText(crypto.getSymbol());

            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            double lastPriceValue = Double.parseDouble(crypto.getLastPrice());
            tvPrice.setText(format.format(lastPriceValue));

            double changePercent;
            try {
                changePercent = Double.parseDouble(crypto.getPriceChangePercent());
            } catch (NumberFormatException e) {
                changePercent = 0.0;
            }
            String changeText = String.format(Locale.US, "%.2f%%", changePercent);
            if (changePercent >= 0) {
                tvChange.setText("+" + changeText);
                tvChange.setTextColor(itemView.getResources().getColor(R.color.green));
            } else {
                tvChange.setText(changeText);
                tvChange.setTextColor(itemView.getResources().getColor(R.color.red));
            }

            tvMarketCap.setText(crypto.getMarketCap() != null ? crypto.getMarketCap() : "N/A");

            String imageUrl = crypto.getIconUrl();
            Log.d("CryptoAdapter", "Carregando ícone para " + crypto.getSymbol() + ": " + imageUrl);

            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .override(48, 48)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontTransform() // Evita transformações que podem degradar a qualidade
                            .encodeQuality(100)) // Garante alta qualidade na codificação
                    .placeholder(android.R.drawable.ic_menu_help)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(ivCryptoIcon);

            itemView.setOnClickListener(v -> listener.onItemClick(crypto));
        }
    }

    static class CryptoDiffCallback extends DiffUtil.Callback {
        private final List<Crypto> oldList;
        private final List<Crypto> newList;

        public CryptoDiffCallback(List<Crypto> oldList, List<Crypto> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getSymbol().equals(newList.get(newItemPosition).getSymbol());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Crypto oldCrypto = oldList.get(oldItemPosition);
            Crypto newCrypto = newList.get(newItemPosition);
            return oldCrypto.getSymbol().equals(newCrypto.getSymbol())
                    && oldCrypto.getLastPrice().equals(newCrypto.getLastPrice())
                    && oldCrypto.getPriceChangePercent().equals(newCrypto.getPriceChangePercent())
                    && (oldCrypto.getIconUrl() == null && newCrypto.getIconUrl() == null ||
                    (oldCrypto.getIconUrl() != null && oldCrypto.getIconUrl().equals(newCrypto.getIconUrl())))
                    && (oldCrypto.getMarketCap() == null && newCrypto.getMarketCap() == null ||
                    (oldCrypto.getMarketCap() != null && oldCrypto.getMarketCap().equals(newCrypto.getMarketCap())));
        }
    }
}