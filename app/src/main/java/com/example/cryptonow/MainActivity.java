package com.example.cryptonow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements CryptoAdapter.OnItemClickListener {

    private RecyclerView rvTopGainers, rvCryptoList;
    private TopGainerAdapter topGainerAdapter;
    private CryptoAdapter cryptoAdapter;
    private List<Crypto> cryptoList = new ArrayList<>();
    private List<Crypto> topGainersList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private Handler handler = new Handler();
    private Runnable updateRunnable;
    private Map<String, String> symbolToIconUrl = new HashMap<>();
    private Map<String, String> symbolToMarketCap = new HashMap<>();

    // Mapeamento manual de símbolos da CoinGecko para Binance
    private static final Map<String, String> COINGECKO_TO_BINANCE = new HashMap<>();

    static {
        COINGECKO_TO_BINANCE.put("btc", "BTCUSDT");
        COINGECKO_TO_BINANCE.put("eth", "ETHUSDT");
        COINGECKO_TO_BINANCE.put("bnb", "BNBUSDT");
        COINGECKO_TO_BINANCE.put("ada", "ADAUSDT");
        COINGECKO_TO_BINANCE.put("sol", "SOLUSDT");
        COINGECKO_TO_BINANCE.put("chz", "CHZUSDT");
        COINGECKO_TO_BINANCE.put("ltc", "LTCUSDT");
        COINGECKO_TO_BINANCE.put("doge", "DOGEUSDT");
        COINGECKO_TO_BINANCE.put("neo", "NEOUSDT");
        COINGECKO_TO_BINANCE.put("bcc", "BCCUSDT");
        COINGECKO_TO_BINANCE.put("qtum", "QTUMUSDT");
        COINGECKO_TO_BINANCE.put("sign", "SIGNUSDT");
        COINGECKO_TO_BINANCE.put("cream", "CREAMUSDT");
        COINGECKO_TO_BINANCE.put("snt", "SNTUSDT");
        COINGECKO_TO_BINANCE.put("xrp", "XRPUSDT");
        COINGECKO_TO_BINANCE.put("dot", "DOTUSDT");
        COINGECKO_TO_BINANCE.put("link", "LINKUSDT");
        COINGECKO_TO_BINANCE.put("matic", "MATICUSDT");
        COINGECKO_TO_BINANCE.put("avax", "AVAXUSDT");
        COINGECKO_TO_BINANCE.put("shib", "SHIBUSDT");
        COINGECKO_TO_BINANCE.put("trx", "TRXUSDT");
        COINGECKO_TO_BINANCE.put("uni", "UNIUSDT");
        COINGECKO_TO_BINANCE.put("atom", "ATOMUSDT");
        COINGECKO_TO_BINANCE.put("xlm", "XLMUSDT");
        COINGECKO_TO_BINANCE.put("vet", "VETUSDT");
        COINGECKO_TO_BINANCE.put("fil", "FILUSDT");
        COINGECKO_TO_BINANCE.put("aave", "AAVEUSDT");
        COINGECKO_TO_BINANCE.put("eos", "EOSUSDT");
        COINGECKO_TO_BINANCE.put("algo", "ALGOUSDT");
        COINGECKO_TO_BINANCE.put("xtz", "XTZUSDT");
        COINGECKO_TO_BINANCE.put("icp", "ICPUSDT");
        COINGECKO_TO_BINANCE.put("theta", "THETAUSDT");
        COINGECKO_TO_BINANCE.put("etc", "ETCUSDT");
        COINGECKO_TO_BINANCE.put("cake", "CAKEUSDT");
        COINGECKO_TO_BINANCE.put("ftm", "FTMUSDT");
        COINGECKO_TO_BINANCE.put("kcs", "KCSUSDT");
        COINGECKO_TO_BINANCE.put("rune", "RUNEUSDT");
        COINGECKO_TO_BINANCE.put("near", "NEARUSDT");
        COINGECKO_TO_BINANCE.put("sand", "SANDUSDT");
        COINGECKO_TO_BINANCE.put("mana", "MANAUSDT");
        COINGECKO_TO_BINANCE.put("gala", "GALAUSDT");
        COINGECKO_TO_BINANCE.put("axs", "AXSUSDT");
        COINGECKO_TO_BINANCE.put("crv", "CRVUSDT");
        COINGECKO_TO_BINANCE.put("one", "ONEUSDT");
        COINGECKO_TO_BINANCE.put("enj", "ENJUSDT");
        COINGECKO_TO_BINANCE.put("hbar", "HBARUSDT");
        COINGECKO_TO_BINANCE.put("hot", "HOTTUSDT");
        COINGECKO_TO_BINANCE.put("iota", "IOTAUSDT");
        COINGECKO_TO_BINANCE.put("lrc", "LRCUSDT");
        COINGECKO_TO_BINANCE.put("zec", "ZECUSDT");
        COINGECKO_TO_BINANCE.put("dash", "DASHUSDT");
        COINGECKO_TO_BINANCE.put("omg", "OMGUSDT");
        COINGECKO_TO_BINANCE.put("comp", "COMPUSDT");
        COINGECKO_TO_BINANCE.put("xem", "XEMUSDT");
        COINGECKO_TO_BINANCE.put("iost", "IOSTUSDT");
        COINGECKO_TO_BINANCE.put("ksm", "KSMUSDT");
        COINGECKO_TO_BINANCE.put("bch", "BCHUSDT");
        COINGECKO_TO_BINANCE.put("ar", "ARUSDT");
        COINGECKO_TO_BINANCE.put("ankr", "ANKRUSDT");
        COINGECKO_TO_BINANCE.put("cvc", "CVCUSDT");
        COINGECKO_TO_BINANCE.put("flow", "FLOWUSDT");
        COINGECKO_TO_BINANCE.put("rose", "ROSEUSDT");
        COINGECKO_TO_BINANCE.put("klay", "KLAYUSDT");
        COINGECKO_TO_BINANCE.put("mina", "MINAUSDT");
        COINGECKO_TO_BINANCE.put("ray", "RAYUSDT");
        COINGECKO_TO_BINANCE.put("audio", "AUDIOUSDT");
        COINGECKO_TO_BINANCE.put("skl", "SKLUSDT");
        COINGECKO_TO_BINANCE.put("waxp", "WAXPUSDT");
        COINGECKO_TO_BINANCE.put("dydx", "DYDXUSDT");
        COINGECKO_TO_BINANCE.put("lpt", "LPTUSDT");
        COINGECKO_TO_BINANCE.put("gno", "GNOUSDT");
        COINGECKO_TO_BINANCE.put("rsr", "RSRUSDT");
        COINGECKO_TO_BINANCE.put("ocean", "OCEANUSDT");
        COINGECKO_TO_BINANCE.put("glm", "GLMUSDT");
        COINGECKO_TO_BINANCE.put("storj", "STORJUSDT");
        COINGECKO_TO_BINANCE.put("celr", "CELRUSDT");
        COINGECKO_TO_BINANCE.put("yfi", "YFIUSDT");
        COINGECKO_TO_BINANCE.put("bal", "BALUSDT");
        COINGECKO_TO_BINANCE.put("perp", "PERPUSDT");
        COINGECKO_TO_BINANCE.put("ctk", "CTKUSDT");
        COINGECKO_TO_BINANCE.put("tribe", "TRIBEUSDT");
        COINGECKO_TO_BINANCE.put("ogn", "OGNUSDT");
        COINGECKO_TO_BINANCE.put("slp", "SLPUSDT");
        COINGECKO_TO_BINANCE.put("sxp", "SXPUSDT");
        COINGECKO_TO_BINANCE.put("kava", "KAVAUSDT");
        COINGECKO_TO_BINANCE.put("iotx", "IOTXUSDT");
        COINGECKO_TO_BINANCE.put("blz", "BLZUSDT");
        COINGECKO_TO_BINANCE.put("ant", "ANTUSDT");
        COINGECKO_TO_BINANCE.put("rvn", "RVNUSDT");
        COINGECKO_TO_BINANCE.put("zec", "ZECUSDT");
        COINGECKO_TO_BINANCE.put("zen", "ZENUSDT");
        COINGECKO_TO_BINANCE.put("sys", "SYSUSDT");
        COINGECKO_TO_BINANCE.put("nkn", "NKNUSDT");
        COINGECKO_TO_BINANCE.put("sc", "SCUSDT");
        COINGECKO_TO_BINANCE.put("hive", "HIVEUSDT");
        COINGECKO_TO_BINANCE.put("dgb", "DGBUSDT");
        COINGECKO_TO_BINANCE.put("bts", "BTSUSDT");
        COINGECKO_TO_BINANCE.put("stx", "STXUSDT");
        COINGECKO_TO_BINANCE.put("lsk", "LSKUSDT");
        COINGECKO_TO_BINANCE.put("tomo", "TOMOUSDT");
        COINGECKO_TO_BINANCE.put("steem", "STEEMUSDT");
        COINGECKO_TO_BINANCE.put("ark", "ARKUSDT");
        COINGECKO_TO_BINANCE.put("win", "WINUSDT");
        COINGECKO_TO_BINANCE.put("dent", "DENTUSDT");
        COINGECKO_TO_BINANCE.put("fun", "FUNUSDT");
        COINGECKO_TO_BINANCE.put("coti", "COTIUSDT");
        COINGECKO_TO_BINANCE.put("mtl", "MTLUSDT");
        COINGECKO_TO_BINANCE.put("tko", "TKOUSDT");
        COINGECKO_TO_BINANCE.put("paxg", "PAXGUSDT");
        COINGECKO_TO_BINANCE.put("band", "BANDUSDT");
        COINGECKO_TO_BINANCE.put("strax", "STRAXUSDT");
        COINGECKO_TO_BINANCE.put("hard", "HARDUSDT");
        COINGECKO_TO_BINANCE.put("dia", "DIAUSDT");
        COINGECKO_TO_BINANCE.put("rlc", "RLCUSDT");
        COINGECKO_TO_BINANCE.put("torn", "TORNUSDT");
        COINGECKO_TO_BINANCE.put("srm", "SRMUSDT");
        COINGECKO_TO_BINANCE.put("egld", "EGLDUSDT");
        COINGECKO_TO_BINANCE.put("fet", "FETUSDT");
        COINGECKO_TO_BINANCE.put("grt", "GRTUSDT");
        COINGECKO_TO_BINANCE.put("inj", "INJUSDT");
        COINGECKO_TO_BINANCE.put("floki", "FLOKIUSDT");
        COINGECKO_TO_BINANCE.put("pepe", "PEPEUSDT");
        COINGECKO_TO_BINANCE.put("sui", "SUIUSDT");
        COINGECKO_TO_BINANCE.put("arb", "ARBUSDT");
        COINGECKO_TO_BINANCE.put("op", "OPUSDT");
        COINGECKO_TO_BINANCE.put("gmt", "GMTUSDT");
        COINGECKO_TO_BINANCE.put("ape", "APEUSDT");
        COINGECKO_TO_BINANCE.put("ldo", "LDOUSDT");
        COINGECKO_TO_BINANCE.put("mkr", "MKRUSDT");
        COINGECKO_TO_BINANCE.put("snx", "SNXUSDT");
        COINGECKO_TO_BINANCE.put("rndr", "RNDRUSDT");
        COINGECKO_TO_BINANCE.put("qnt", "QNTUSDT");
        COINGECKO_TO_BINANCE.put("mask", "MASKUSDT");
        COINGECKO_TO_BINANCE.put("apt", "APTUSDT");
        COINGECKO_TO_BINANCE.put("rpl", "RPLUSDT");
        COINGECKO_TO_BINANCE.put("fxs", "FXSUSDT");
        COINGECKO_TO_BINANCE.put("woo", "WOOUSDT");
        COINGECKO_TO_BINANCE.put("gmx", "GMXUSDT");
        COINGECKO_TO_BINANCE.put("cfx", "CFXUSDT");
        COINGECKO_TO_BINANCE.put("high", "HIGHUSDT");
        COINGECKO_TO_BINANCE.put("cvx", "CVXUSDT");
        COINGECKO_TO_BINANCE.put("alice", "ALICEUSDT");
        COINGECKO_TO_BINANCE.put("ach", "ACHUSDT");
        COINGECKO_TO_BINANCE.put("dar", "DARUSDT");
        COINGECKO_TO_BINANCE.put("id", "IDUSDT");
        COINGECKO_TO_BINANCE.put("agix", "AGIXUSDT");
        COINGECKO_TO_BINANCE.put("pendle", "PENDLEUSDT");
        COINGECKO_TO_BINANCE.put("t", "TUSDT");
        COINGECKO_TO_BINANCE.put("edu", "EDUUSDT");
        COINGECKO_TO_BINANCE.put("hook", "HOOKUSDT");
        COINGECKO_TO_BINANCE.put("magic", "MAGICUSDT");
        COINGECKO_TO_BINANCE.put("joe", "JOEUSDT");
        COINGECKO_TO_BINANCE.put("lina", "LINAUSDT");
        COINGECKO_TO_BINANCE.put("tomo", "TOMOUSDT");
        COINGECKO_TO_BINANCE.put("lever", "LEVERUSDT");
        COINGECKO_TO_BINANCE.put("trx", "TRXUSDT");
        COINGECKO_TO_BINANCE.put("key", "KEYUSDT");
        COINGECKO_TO_BINANCE.put("tru", "TRUUSDT");
        COINGECKO_TO_BINANCE.put("amb", "AMBUSDT");
        COINGECKO_TO_BINANCE.put("rad", "RADUSDT");
        COINGECKO_TO_BINANCE.put("phb", "PHBUSDT");
        COINGECKO_TO_BINANCE.put("rdnt", "RDNTUSDT");
        COINGECKO_TO_BINANCE.put("hft", "HFTUSDT");
        COINGECKO_TO_BINANCE.put("stpt", "STPTUSDT");
        COINGECKO_TO_BINANCE.put("uma", "UMAUSDT");
        COINGECKO_TO_BINANCE.put("syn", "SYNUSDT");
        COINGECKO_TO_BINANCE.put("lqty", "LQTYUSDT");
        COINGECKO_TO_BINANCE.put("jst", "JSTUSDT");
        COINGECKO_TO_BINANCE.put("prom", "PROMUSDT");
        COINGECKO_TO_BINANCE.put("oxt", "OXTUSDT");
        COINGECKO_TO_BINANCE.put("zrx", "ZRXUSDT");
        COINGECKO_TO_BINANCE.put("alcx", "ALCXUSDT");
        COINGECKO_TO_BINANCE.put("idex", "IDEXUSDT");
        COINGECKO_TO_BINANCE.put("pundix", "PUNDIXUSDT");
        COINGECKO_TO_BINANCE.put("clv", "CLVUSDT");
        COINGECKO_TO_BINANCE.put("mln", "MLNUSDT");
        COINGECKO_TO_BINANCE.put("fida", "FIDAUSDT");
        COINGECKO_TO_BINANCE.put("farm", "FARMUSDT");
        COINGECKO_TO_BINANCE.put("ghst", "GHSTUSDT");
        COINGECKO_TO_BINANCE.put("fis", "FISUSDT");
        COINGECKO_TO_BINANCE.put("front", "FRONTUSDT");
        COINGECKO_TO_BINANCE.put("voxel", "VOXELUSDT");
        COINGECKO_TO_BINANCE.put("bake", "BAKEUSDT");
        COINGECKO_TO_BINANCE.put("wrx", "WRXUSDT");
        COINGECKO_TO_BINANCE.put("mbl", "MBLUSDT");
        COINGECKO_TO_BINANCE.put("mob", "MOBUSDT");
        COINGECKO_TO_BINANCE.put("nmr", "NMRUSDT");
        COINGECKO_TO_BINANCE.put("quick", "QUICKUSDT");
        COINGECKO_TO_BINANCE.put("unfi", "UNFIUSDT");
        COINGECKO_TO_BINANCE.put("beta", "BETAUSDT");
        COINGECKO_TO_BINANCE.put("rgt", "RGTUSDT");
        COINGECKO_TO_BINANCE.put("pnt", "PNTUSDT");
        COINGECKO_TO_BINANCE.put("xvs", "XVSUSDT");
        COINGECKO_TO_BINANCE.put("alpha", "ALPHAUSDT");
        COINGECKO_TO_BINANCE.put("vidt", "VIDTUSDT");
        COINGECKO_TO_BINANCE.put("ava", "AVAUSDT");
        COINGECKO_TO_BINANCE.put("orbs", "ORBSUSDT");
        COINGECKO_TO_BINANCE.put("city", "CITYUSDT");
        COINGECKO_TO_BINANCE.put("bel", "BELUSDT");
        COINGECKO_TO_BINANCE.put("fio", "FIOUSDT");
        COINGECKO_TO_BINANCE.put("wing", "WINGUSDT");
        COINGECKO_TO_BINANCE.put("bnx", "BNXUSDT");
        COINGECKO_TO_BINANCE.put("chess", "CHESSUSDT");
        COINGECKO_TO_BINANCE.put("tlm", "TLMUSDT");
        COINGECKO_TO_BINANCE.put("gft", "GFTUSDT");
        COINGECKO_TO_BINANCE.put("mdt", "MDTUSDT");
        COINGECKO_TO_BINANCE.put("reef", "REEFUSDT");
        COINGECKO_TO_BINANCE.put("token", "TOKENUSDT");
        COINGECKO_TO_BINANCE.put("adx", "ADXUSDT");
        COINGECKO_TO_BINANCE.put("auction", "AUCTIONUSDT");
        COINGECKO_TO_BINANCE.put("rare", "RAREUSDT");
        COINGECKO_TO_BINANCE.put("slrs", "SLRSUSDT");
        COINGECKO_TO_BINANCE.put("santos", "SANTOSUSDT");
        COINGECKO_TO_BINANCE.put("porto", "PORTOUSDT");
        COINGECKO_TO_BINANCE.put("lazio", "LAZIOUSDT");
        COINGECKO_TO_BINANCE.put("acm", "ACMUSDT");
        COINGECKO_TO_BINANCE.put("bar", "BARUSDT");
        COINGECKO_TO_BINANCE.put("psg", "PSGUSDT");
        COINGECKO_TO_BINANCE.put("juv", "JUVUSDT");
        COINGECKO_TO_BINANCE.put("atm", "ATMUSDT");
        COINGECKO_TO_BINANCE.put("og", "OGUSDT");
        COINGECKO_TO_BINANCE.put("asr", "ASRUSDT");
        COINGECKO_TO_BINANCE.put("mcl", "MCLUSDT");
        COINGECKO_TO_BINANCE.put("for", "FORUSDT");
        COINGECKO_TO_BINANCE.put("vtho", "VTHOUSDT");
        COINGECKO_TO_BINANCE.put("polkadot", "DOTUSDT");
        COINGECKO_TO_BINANCE.put("polydoge", "POLYDOGEUSDT");
        COINGECKO_TO_BINANCE.put("woop", "WOOPUSDT");
        COINGECKO_TO_BINANCE.put("pla", "PLAUSDT");
        COINGECKO_TO_BINANCE.put("pols", "POLSUSDT");
        COINGECKO_TO_BINANCE.put("mdx", "MDXUSDT");
        COINGECKO_TO_BINANCE.put("mask", "MASKUSDT");
        COINGECKO_TO_BINANCE.put("xno", "XNOUSDT");
        COINGECKO_TO_BINANCE.put("gas", "GASUSDT");
        COINGECKO_TO_BINANCE.put("flux", "FLUXUSDT");
        COINGECKO_TO_BINANCE.put("dexe", "DEXEUSDT");
        COINGECKO_TO_BINANCE.put("c98", "C98USDT");
        COINGECKO_TO_BINANCE.put("clv", "CLVUSDT");
        COINGECKO_TO_BINANCE.put("qkc", "QKCUSDT");
        COINGECKO_TO_BINANCE.put("powr", "POWRUSDT");
        COINGECKO_TO_BINANCE.put("pyth", "PYTHUSDT");
        COINGECKO_TO_BINANCE.put("nxm", "NXMUSDT");
        COINGECKO_TO_BINANCE.put("sys", "SYSUSDT");
        COINGECKO_TO_BINANCE.put("mln", "MLNUSDT");
        COINGECKO_TO_BINANCE.put("usdp", "USDPUSDT");
        COINGECKO_TO_BINANCE.put("vox", "VOXELUSDT");
        COINGECKO_TO_BINANCE.put("mc", "MCUSDT");
        COINGECKO_TO_BINANCE.put("perp", "PERPUSDT");
        COINGECKO_TO_BINANCE.put("alpaca", "ALPACAUSDT");
        COINGECKO_TO_BINANCE.put("erg", "ERGUSDT");
        COINGECKO_TO_BINANCE.put("mkr", "MKRUSDT");
        COINGECKO_TO_BINANCE.put("kp3r", "KP3RUSDT");
        COINGECKO_TO_BINANCE.put("dodo", "DODOUSDT");
        COINGECKO_TO_BINANCE.put("badger", "BADGERUSDT");
        COINGECKO_TO_BINANCE.put("nexo", "NEXOUSDT");
        COINGECKO_TO_BINANCE.put("elf", "ELFUSDT");
        COINGECKO_TO_BINANCE.put("kda", "KDAUSDT");
        COINGECKO_TO_BINANCE.put("bsw", "BSWUSDT");
        COINGECKO_TO_BINANCE.put("yfii", "YFIIUSDT");
        COINGECKO_TO_BINANCE.put("ata", "ATAUSDT");
        COINGECKO_TO_BINANCE.put("mlk", "MLKUSDT");
        COINGECKO_TO_BINANCE.put("ark", "ARKUSDT");
        COINGECKO_TO_BINANCE.put("ilv", "ILVUSDT");
        COINGECKO_TO_BINANCE.put("pyr", "PYRUSDT");
        COINGECKO_TO_BINANCE.put("glmr", "GLMRUSDT");
        COINGECKO_TO_BINANCE.put("aca", "ACAUSDT");
        COINGECKO_TO_BINANCE.put("ron", "RONUSDT");
        COINGECKO_TO_BINANCE.put("mult", "MULTUSDT");
        COINGECKO_TO_BINANCE.put("gmt", "GMTUSDT");
        COINGECKO_TO_BINANCE.put("jto", "JTOUSDT");
        COINGECKO_TO_BINANCE.put("bonk", "BONKUSDT");
        COINGECKO_TO_BINANCE.put("wld", "WLDUSDT");
        COINGECKO_TO_BINANCE.put("memecoin", "MEMEUSDT");
        COINGECKO_TO_BINANCE.put("ordi", "ORDIUSDT");
        COINGECKO_TO_BINANCE.put("tia", "TIAUSDT");
        COINGECKO_TO_BINANCE.put("beamx", "BEAMXUSDT");
        COINGECKO_TO_BINANCE.put("sei", "SEIUSDT");
        COINGECKO_TO_BINANCE.put("cyber", "CYBERUSDT");
        COINGECKO_TO_BINANCE.put("gas", "GASUSDT");
        COINGECKO_TO_BINANCE.put("strk", "STRKUSDT");
        COINGECKO_TO_BINANCE.put("port3", "PORT3USDT");
        COINGECKO_TO_BINANCE.put("alt", "ALTUSDT");
        COINGECKO_TO_BINANCE.put("ai", "AIUSDT");
        COINGECKO_TO_BINANCE.put("xai", "XAIUSDT");
        COINGECKO_TO_BINANCE.put("manta", "MANTAUSDT");
        COINGECKO_TO_BINANCE.put("ondop", "ONDOUSDT");
        COINGECKO_TO_BINANCE.put("nym", "NYMUSDT");
        COINGECKO_TO_BINANCE.put("silly", "SILLYUSDT");
        COINGECKO_TO_BINANCE.put("myro", "MYROUSDT");
        COINGECKO_TO_BINANCE.put("sats", "SATSUSDT");
        COINGECKO_TO_BINANCE.put("rat", "RATSUSDT");
        COINGECKO_TO_BINANCE.put("vic", "VICUSDT");
        COINGECKO_TO_BINANCE.put("bake", "BAKEUSDT");
        COINGECKO_TO_BINANCE.put("chr", "CHRUSDT");
        COINGECKO_TO_BINANCE.put("dego", "DEGOUSDT");
        COINGECKO_TO_BINANCE.put("df", "DFUSDT");
        COINGECKO_TO_BINANCE.put("dusk", "DUSKUSDT");
        COINGECKO_TO_BINANCE.put("gme", "GMEUSDT");
        COINGECKO_TO_BINANCE.put("gmt", "GMTUSDT");
        COINGECKO_TO_BINANCE.put("gmx", "GMXUSDT");
        COINGECKO_TO_BINANCE.put("gno", "GNOUSDT");
        COINGECKO_TO_BINANCE.put("gnt", "GNTUSDT");
        COINGECKO_TO_BINANCE.put("gto", "GTOUSDT");
        COINGECKO_TO_BINANCE.put("iris", "IRISUSDT");
        COINGECKO_TO_BINANCE.put("loom", "LOOMUSDT");
        COINGECKO_TO_BINANCE.put("lto", "LTOUSDT");
        COINGECKO_TO_BINANCE.put("mft", "MFTUSDT");
        COINGECKO_TO_BINANCE.put("mith", "MITHUSDT");
        COINGECKO_TO_BINANCE.put("nbs", "NBSUSDT");
        COINGECKO_TO_BINANCE.put("nuls", "NULSUSDT");
        COINGECKO_TO_BINANCE.put("oax", "OAXUSDT");
        COINGECKO_TO_BINANCE.put("ogn", "OGNUSDT");
        COINGECKO_TO_BINANCE.put("ong", "ONGUSDT");
        COINGECKO_TO_BINANCE.put("ont", "ONTUSDT");
        COINGECKO_TO_BINANCE.put("pax", "PAXUSDT");
        COINGECKO_TO_BINANCE.put("poly", "POLYUSDT");
        COINGECKO_TO_BINANCE.put("powr", "POWRUSDT");
        COINGECKO_TO_BINANCE.put("ppt", "PPTUSDT");
        COINGECKO_TO_BINANCE.put("qsp", "QSPUSDT");
        COINGECKO_TO_BINANCE.put("rdn", "RDNUSDT");
        COINGECKO_TO_BINANCE.put("ren", "RENUSDT");
        COINGECKO_TO_BINANCE.put("rep", "REPUSDT");
        COINGECKO_TO_BINANCE.put("req", "REQUSDT");
        COINGECKO_TO_BINANCE.put("rif", "RIFUSDT");
        COINGECKO_TO_BINANCE.put("sbd", "SBDUSDT");
        COINGECKO_TO_BINANCE.put("sngls", "SNGLSUSDT");
        COINGECKO_TO_BINANCE.put("snx", "SNXUSDT");
        COINGECKO_TO_BINANCE.put("snm", "SNMUSDT");
        COINGECKO_TO_BINANCE.put("storm", "STORMUSDT");
        COINGECKO_TO_BINANCE.put("stpt", "STPTUSDT");
        COINGECKO_TO_BINANCE.put("strat", "STRATUSDT");
        COINGECKO_TO_BINANCE.put("sys", "SYSUSDT");
        COINGECKO_TO_BINANCE.put("tct", "TCTUSDT");
        COINGECKO_TO_BINANCE.put("tnt", "TNTUSDT");
        COINGECKO_TO_BINANCE.put("troy", "TROYUSDT");
        COINGECKO_TO_BINANCE.put("tusd", "TUSDUSDT");
        COINGECKO_TO_BINANCE.put("utk", "UTKUSDT");
        COINGECKO_TO_BINANCE.put("vet", "VETUSDT");
        COINGECKO_TO_BINANCE.put("via", "VIAUSDT");
        COINGECKO_TO_BINANCE.put("vib", "VIBUSDT");
        COINGECKO_TO_BINANCE.put("vibe", "VIBEUSDT");
        COINGECKO_TO_BINANCE.put("wicc", "WICCUSDT");
        COINGECKO_TO_BINANCE.put("wings", "WINGSUSDT");
        COINGECKO_TO_BINANCE.put("wnxm", "WNXMUSDT");
        COINGECKO_TO_BINANCE.put("xvg", "XVGUSDT");
        COINGECKO_TO_BINANCE.put("yoyow", "YOYOWUSDT");
        COINGECKO_TO_BINANCE.put("zrx", "ZRXUSDT");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Load theme preference
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean nightMode = prefs.getBoolean("night_mode", false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Initialize UI elements
        rvTopGainers = findViewById(R.id.rvTopGainers);
        rvCryptoList = findViewById(R.id.rvCryptoList);

        // Set up Top Gainers RecyclerView
        rvTopGainers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        topGainerAdapter = new TopGainerAdapter(topGainersList);
        rvTopGainers.setAdapter(topGainerAdapter);

        // Set up Crypto List (Trending) RecyclerView
        rvCryptoList.setLayoutManager(new LinearLayoutManager(this));
        cryptoAdapter = new CryptoAdapter(cryptoList, this);
        rvCryptoList.setAdapter(cryptoAdapter);

        // Fetch icon URLs and market caps from CoinGecko first, then start updating crypto data
        fetchCoinGeckoData();
    }

    private void fetchCoinGeckoData() {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=250&page=1&sparkline=false";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro ao carregar dados da CoinGecko", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();

                symbolToIconUrl.clear();
                symbolToMarketCap.clear();
                for (int i = 0; i < array.size(); i++) {
                    JsonObject obj = array.get(i).getAsJsonObject();
                    String cgSymbol = obj.get("symbol").getAsString().toLowerCase();
                    String iconUrl = obj.get("image").getAsString();
                    double marketCap = obj.get("market_cap").getAsDouble();

                    // Substituir "thumb" por "large" para obter imagens de maior qualidade
                    iconUrl = iconUrl.replace("/thumb/", "/large/");

                    // Formatar a capitalização de mercado
                    String marketCapFormatted = formatMarketCap(marketCap);

                    // Mapear o símbolo da CoinGecko para o formato da Binance
                    String binanceSymbol = COINGECKO_TO_BINANCE.get(cgSymbol);
                    if (binanceSymbol != null) {
                        symbolToIconUrl.put(binanceSymbol, iconUrl);
                        symbolToMarketCap.put(binanceSymbol, marketCapFormatted);
                        Log.d("MainActivity", "Mapeado: " + binanceSymbol + " -> Icon: " + iconUrl + ", Market Cap: " + marketCapFormatted);
                    }
                }

                runOnUiThread(() -> {
                    Log.d("MainActivity", "Dados da CoinGecko carregados: " + symbolToIconUrl.size());
                    // Após carregar os dados, inicia a atualização dos preços
                    startUpdatingCryptoData();
                });
            }
        });
    }

    private String formatMarketCap(double marketCap) {
        if (marketCap >= 1_000_000_000_000L) {
            return String.format(Locale.US, "$%.1fT MKT CAP", marketCap / 1_000_000_000_000L);
        } else if (marketCap >= 1_000_000_000) {
            return String.format(Locale.US, "$%.1fB MKT CAP", marketCap / 1_000_000_000);
        } else if (marketCap >= 1_000_000) {
            return String.format(Locale.US, "$%.1fM MKT CAP", marketCap / 1_000_000);
        } else {
            return String.format(Locale.US, "$%.1fK MKT CAP", marketCap / 1_000);
        }
    }

    private void startUpdatingCryptoData() {
        updateRunnable = () -> {
            fetchCryptoData();
            handler.postDelayed(updateRunnable, 1000); // Atualiza a cada 1 segundo
        };
        handler.post(updateRunnable);

        // Atualizar a variação percentual a cada 15 segundos
        fetchPriceChangeData();
    }

    private void stopUpdatingCryptoData() {
        handler.removeCallbacksAndMessages(null);
    }

    private void fetchCryptoData() {
        String url = "https://api.binance.com/api/v3/ticker/price";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();

                List<Crypto> newCryptoList = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    JsonObject obj = array.get(i).getAsJsonObject();
                    String symbol = obj.get("symbol").getAsString();
                    // Filtrar apenas pares com USDT
                    if (!symbol.endsWith("USDT")) {
                        Log.d("MainActivity", "Moeda filtrada (não termina com USDT): " + symbol);
                        continue;
                    }

                    // Verificar se o símbolo está mapeado
                    if (!symbolToIconUrl.containsKey(symbol)) {
                        Log.d("MainActivity", "Moeda filtrada (sem ícone mapeado): " + symbol);
                        continue;
                    }

                    String lastPrice = obj.get("price").getAsString();

                    // Buscar a variação percentual do último dado conhecido ou usar 0 se for a primeira atualização
                    String priceChangePercent = "0.00";
                    for (Crypto crypto : cryptoList) {
                        if (crypto.getSymbol().equals(symbol)) {
                            priceChangePercent = crypto.getPriceChangePercent();
                            break;
                        }
                    }

                    try {
                        Double.parseDouble(lastPrice);
                        String iconUrl = symbolToIconUrl.get(symbol);
                        String marketCap = symbolToMarketCap.get(symbol);
                        newCryptoList.add(new Crypto(symbol, lastPrice, priceChangePercent, iconUrl, marketCap));
                    } catch (NumberFormatException e) {
                        Log.e("MainActivity", "Erro ao parsear dados para " + symbol + ": " + e.getMessage());
                    }
                }

                // Filtrar moedas com variação positiva para Top Gainers
                List<Crypto> positiveChangeList = new ArrayList<>();
                for (Crypto crypto : newCryptoList) {
                    try {
                        double changePercent = Double.parseDouble(crypto.getPriceChangePercent());
                        if (changePercent > 0) {
                            positiveChangeList.add(crypto);
                        }
                    } catch (NumberFormatException e) {
                        Log.e("MainActivity", "Erro ao parsear priceChangePercent para " + crypto.getSymbol() + ": " + e.getMessage());
                    }
                }

                // Sort para top gainers (top 3 por priceChangePercent)
                List<Crypto> sortedList = new ArrayList<>(positiveChangeList);
                Collections.sort(sortedList, (a, b) -> {
                    try {
                        return Double.compare(
                                Double.parseDouble(b.getPriceChangePercent()),
                                Double.parseDouble(a.getPriceChangePercent())
                        );
                    } catch (NumberFormatException e) {
                        Log.e("MainActivity", "Erro ao ordenar: " + e.getMessage());
                        return 0;
                    }
                });

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    // Update full crypto list
                    cryptoList.clear();
                    cryptoList.addAll(newCryptoList);
                    cryptoAdapter.updateList(newCryptoList);

                    // Update top gainers (top 3)
                    topGainersList.clear();
                    topGainersList.addAll(sortedList.subList(0, Math.min(3, sortedList.size())));
                    topGainerAdapter.updateList(topGainersList);

                    // Log para depuração
                    Log.d("MainActivity", "Top Gainers: " + topGainersList.toString());
                    Log.d("MainActivity", "Total moedas carregadas: " + newCryptoList.size());
                });
            }
        });
    }

    private void fetchPriceChangeData() {
        String url = "https://api.binance.com/api/v3/ticker/24hr";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro ao carregar variação", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();

                for (int i = 0; i < array.size(); i++) {
                    JsonObject obj = array.get(i).getAsJsonObject();
                    String symbol = obj.get("symbol").getAsString();
                    if (!symbol.endsWith("USDT")) continue;

                    String priceChangePercent = obj.get("priceChangePercent").getAsString();

                    for (Crypto crypto : cryptoList) {
                        if (crypto.getSymbol().equals(symbol)) {
                            crypto.setPriceChangePercent(priceChangePercent);
                            break;
                        }
                    }
                }

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    // Atualizar as listas com as novas variações
                    List<Crypto> positiveChangeList = new ArrayList<>();
                    for (Crypto crypto : cryptoList) {
                        try {
                            double changePercent = Double.parseDouble(crypto.getPriceChangePercent());
                            if (changePercent > 0) {
                                positiveChangeList.add(crypto);
                            }
                        } catch (NumberFormatException e) {
                            Log.e("MainActivity", "Erro ao parsear priceChangePercent para " + crypto.getSymbol() + ": " + e.getMessage());
                        }
                    }

                    List<Crypto> sortedList = new ArrayList<>(positiveChangeList);
                    Collections.sort(sortedList, (a, b) -> {
                        try {
                            return Double.compare(
                                    Double.parseDouble(b.getPriceChangePercent()),
                                    Double.parseDouble(a.getPriceChangePercent())
                            );
                        } catch (NumberFormatException e) {
                            Log.e("MainActivity", "Erro ao ordenar: " + e.getMessage());
                            return 0;
                        }
                    });

                    cryptoAdapter.updateList(cryptoList);
                    topGainersList.clear();
                    topGainersList.addAll(sortedList.subList(0, Math.min(3, sortedList.size())));
                    topGainerAdapter.updateList(topGainersList);
                });

                // Agendar a próxima atualização da variação
                handler.postDelayed(() -> fetchPriceChangeData(), 15000);
            }
        });
    }

    @Override
    public void onItemClick(Crypto crypto) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("symbol", crypto.getSymbol());
        intent.putExtra("iconUrl", crypto.getIconUrl());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdatingCryptoData();
    }
}