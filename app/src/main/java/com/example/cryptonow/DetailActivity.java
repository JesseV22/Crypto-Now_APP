package com.example.cryptonow;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    private TextView tvCryptoSymbolDetail, tvCryptoPriceDetail, tvCryptoChangeDetail;
    private ImageView ivCryptoDetailIcon, btnBack;
    private LineChart lineChart;
    private Button btnLive, btn4H, btn1D, btn1W, btn1M, btnMax;
    private OkHttpClient client = new OkHttpClient();
    private String symbol;
    private String iconUrl;
    private Handler handler = new Handler();
    private Runnable updateRunnable;
    private String selectedInterval = "1d"; // Intervalo padrão: 1 dia

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Referências para os elementos de UI
        tvCryptoSymbolDetail = findViewById(R.id.tvCryptoSymbolDetail);
        tvCryptoPriceDetail = findViewById(R.id.tvCryptoPriceDetail);
        tvCryptoChangeDetail = findViewById(R.id.tvCryptoChangeDetail);
        ivCryptoDetailIcon = findViewById(R.id.ivCryptoDetailIcon);
        btnBack = findViewById(R.id.btnBack);
        lineChart = findViewById(R.id.lineChart);

        // Botões de intervalo de tempo
        btnLive = findViewById(R.id.btnLive);
        btn4H = findViewById(R.id.btn4H);
        btn1D = findViewById(R.id.btn1D);
        btn1W = findViewById(R.id.btn1W);
        btn1M = findViewById(R.id.btn1M);
        btnMax = findViewById(R.id.btnMax);

        // Configurar o botão de voltar
        btnBack.setOnClickListener(v -> finish());

        // Configurar listeners para os botões de intervalo
        btnLive.setOnClickListener(v -> updateChartInterval("live"));
        btn4H.setOnClickListener(v -> updateChartInterval("4h"));
        btn1D.setOnClickListener(v -> updateChartInterval("1d"));
        btn1W.setOnClickListener(v -> updateChartInterval("1w"));
        btn1M.setOnClickListener(v -> updateChartInterval("1m"));
        btnMax.setOnClickListener(v -> updateChartInterval("max"));

        // Carregar preferência de tema
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean nightMode = prefs.getBoolean("night_mode", false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Pega o símbolo e a URL do ícone
        symbol = getIntent().getStringExtra("symbol");
        iconUrl = getIntent().getStringExtra("iconUrl");

        // Log para depuração
        Log.d("DetailActivity", "Carregando ícone para " + symbol + ": " + iconUrl);

        // Carregar o ícone
        Glide.with(this)
                .load(iconUrl)
                .apply(new RequestOptions()
                        .override(48, 48)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontTransform()
                        .encodeQuality(100))
                .placeholder(android.R.drawable.ic_menu_help)
                .error(android.R.drawable.ic_menu_close_clear_cancel)
                .into(ivCryptoDetailIcon);

        // Configurar o gráfico
        setupChart();

        // Começa a buscar as informações
        startUpdatingCryptoDetails();
    }

    private void setupChart() {
        // Configurações básicas do gráfico
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setBackgroundColor(Color.BLACK);

        // Configurar eixo X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormatDay = new SimpleDateFormat("dd/MM HH:mm", Locale.US);
            private final SimpleDateFormat mFormatHour = new SimpleDateFormat("HH:mm", Locale.US);

            @Override
            public String getFormattedValue(float value) {
                if (selectedInterval.equals("1m") || selectedInterval.equals("max")) {
                    return mFormatDay.format(new Date((long) value));
                } else {
                    return mFormatHour.format(new Date((long) value));
                }
            }
        });

        // Configurar eixo Y
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        // Legenda
        lineChart.getLegend().setTextColor(Color.WHITE);
    }

    private void updateChartInterval(String interval) {
        selectedInterval = interval;
        fetchChartData();
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        // Resetar estilos
        btnLive.setTextColor(Color.WHITE);
        btnLive.setTypeface(null, android.graphics.Typeface.NORMAL);
        btnLive.setBackgroundResource(R.drawable.button_background);
        btn4H.setTextColor(Color.WHITE);
        btn4H.setTypeface(null, android.graphics.Typeface.NORMAL);
        btn4H.setBackgroundResource(R.drawable.button_background);
        btn1D.setTextColor(Color.WHITE);
        btn1D.setTypeface(null, android.graphics.Typeface.NORMAL);
        btn1D.setBackgroundResource(R.drawable.button_background);
        btn1W.setTextColor(Color.WHITE);
        btn1W.setTypeface(null, android.graphics.Typeface.NORMAL);
        btn1W.setBackgroundResource(R.drawable.button_background);
        btn1M.setTextColor(Color.WHITE);
        btn1M.setTypeface(null, android.graphics.Typeface.NORMAL);
        btn1M.setBackgroundResource(R.drawable.button_background);
        btnMax.setTextColor(Color.WHITE);
        btnMax.setTypeface(null, android.graphics.Typeface.NORMAL);
        btnMax.setBackgroundResource(R.drawable.button_background);

        // Destacar o botão selecionado
        Button selectedButton = btn1D; // Padrão
        switch (selectedInterval) {
            case "live":
                selectedButton = btnLive;
                break;
            case "4h":
                selectedButton = btn4H;
                break;
            case "1d":
                selectedButton = btn1D;
                break;
            case "1w":
                selectedButton = btn1W;
                break;
            case "1m":
                selectedButton = btn1M;
                break;
            case "max":
                selectedButton = btnMax;
                break;
        }
        selectedButton.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        selectedButton.setTypeface(null, android.graphics.Typeface.BOLD);
        selectedButton.setBackgroundResource(R.drawable.button_background_selected);
    }

    private void fetchChartData() {
        String interval;
        long startTime;
        int limit = 500; // Número máximo de pontos de dados

        switch (selectedInterval) {
            case "live":
                interval = "1m"; // 1 minuto
                startTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000; // Últimas 24 horas
                limit = 1440; // 24 horas * 60 minutos
                break;
            case "4h":
                interval = "1m"; // 1 minuto
                startTime = System.currentTimeMillis() - 4 * 60 * 60 * 1000; // Últimas 4 horas
                limit = 240; // 4 horas * 60 minutos
                break;
            case "1d":
                interval = "5m"; // 5 minutos
                startTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000; // Últimas 24 horas
                limit = 288; // 24 horas / 5 minutos
                break;
            case "1w":
                interval = "1h"; // 1 hora
                startTime = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000; // Última semana
                limit = 168; // 7 dias * 24 horas
                break;
            case "1m":
                interval = "4h"; // 4 horas
                startTime = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L; // Último mês
                limit = 180; // 30 dias * 6 (4h por dia)
                break;
            case "max":
                interval = "1d"; // 1 dia
                startTime = System.currentTimeMillis() - 365 * 24 * 60 * 60 * 1000L; // Último ano
                limit = 365; // 1 ano
                break;
            default:
                interval = "5m";
                startTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
                limit = 288;
                break;
        }

        String url = "https://api.binance.com/api/v3/klines?symbol=" + symbol + "&interval=" + interval + "&startTime=" + startTime + "&limit=" + limit;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Erro ao carregar dados do gráfico", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();

                List<Entry> entries = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    JsonArray kline = array.get(i).getAsJsonArray();
                    long time = kline.get(0).getAsLong();
                    float price = kline.get(4).getAsFloat(); // Preço de fechamento
                    entries.add(new Entry(time, price));
                }

                runOnUiThread(() -> {
                    if (entries.isEmpty()) {
                        lineChart.clear();
                        return;
                    }

                    LineDataSet dataSet = new LineDataSet(entries, symbol);
                    dataSet.setColor(Color.GREEN);
                    dataSet.setDrawCircles(false);
                    dataSet.setDrawValues(false);
                    dataSet.setLineWidth(2f);

                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);
                    lineChart.invalidate(); // Atualiza o gráfico
                });
            }
        });
    }

    private void startUpdatingCryptoDetails() {
        updateRunnable = () -> {
            fetchCryptoDetails();
            fetchChartData(); // Atualiza o gráfico
            handler.postDelayed(updateRunnable, 1000); // Atualiza a cada 1 segundo
        };
        handler.post(updateRunnable);
    }

    private void stopUpdatingCryptoDetails() {
        handler.removeCallbacksAndMessages(null);
    }

    private void fetchCryptoDetails() {
        String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

                String lastPrice = obj.get("price").getAsString();

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                    double lastPriceValue = Double.parseDouble(lastPrice);
                    tvCryptoPriceDetail.setText(format.format(lastPriceValue));
                });
            }
        });

        // Atualizar a variação percentual a cada 15 segundos
        String changeUrl = "https://api.binance.com/api/v3/ticker/24hr?symbol=" + symbol;
        Request changeRequest = new Request.Builder()
                .url(changeUrl)
                .build();

        client.newCall(changeRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Não exibir Toast para evitar spam
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

                String priceChangePercent = obj.get("priceChangePercent").getAsString();

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    double changePercentValue = Double.parseDouble(priceChangePercent);
                    String arrow = changePercentValue >= 0 ? "🔼" : "🔽";
                    String changeText = arrow + " " + String.format(Locale.US, "%.2f", changePercentValue) + "%";
                    tvCryptoChangeDetail.setText(changeText);

                    if (changePercentValue >= 0) {
                        tvCryptoChangeDetail.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    } else {
                        tvCryptoChangeDetail.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    }

                    tvCryptoSymbolDetail.setText(symbol);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdatingCryptoDetails();
    }
}