package com.app.gmv3.activities;

import static com.app.gmv3.utilities.Constant.GET_ITEMS8020;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.R;
import com.app.gmv3.adapters.AdapterItems8020;
import com.app.gmv3.models.Producto_lista8020;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityItems8020 extends AppCompatActivity implements AdapterItems8020.ContactsAdapterListener {
    String RUTA;
    int nMonth,nYear;

    private AdapterItems8020 mAdapter;
    private RecyclerView recyclerView;
    private List<Producto_lista8020> productList;
    TextView txt_factura_total,count_80,count_20,id_porcent_80,id_porcent_20;
    SwipeRefreshLayout swipeRefreshLayout = null;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_8020);
        initToolbar();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }
    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(ActivityItems8020.this)) {
                            swipeRefreshLayout.setRefreshing(false);
                            fetchData();
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(ActivityItems8020.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 1500);
            }
        });
    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        RUTA    = intent.getStringExtra("RUTA");
        nMonth  = intent.getIntExtra("nMonth",0);
        nYear   = intent.getIntExtra("nYear",0);



        String strTitulo = ("ARTICULOS 80/20 - ").concat(Utils.getShortNameMonth(nMonth)).concat(String.valueOf(nYear)) ;

        getSupportActionBar().setTitle(strTitulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.grey_5));
        }
        Utils.setSystemBarLight(this);

        txt_factura_total = findViewById(R.id.id_factura_total);
        count_80 =  findViewById(R.id.count_80);
        count_20 =  findViewById(R.id.count_20);
        id_porcent_80=  findViewById(R.id.id_porcent_80);
        id_porcent_20=  findViewById(R.id.id_porcent_20);




        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new AdapterItems8020( this,productList,this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        fetchData();
    }


    private void fetchData() {
        JsonArrayRequest request = new JsonArrayRequest(GET_ITEMS8020 + RUTA.concat("/").concat(String.valueOf(nMonth)).concat("/").concat(String.valueOf(nYear)), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }

                List<Producto_lista8020> items = new Gson().fromJson(response.toString(), new TypeToken<List<Producto_lista8020>>() {
                }.getType());

                float lst80 = 0 ;
                float lst20 = 0 ;
                float Vnt80 = 0 ;
                float Vnt20 = 0 ;
                float v80 = 0;
                float v20 = 0;



                for (int i = 0; i < items.size(); i++) {
                    float i1 = (items.get(i).getLISTA().equals("80")) ? lst80++ : lst20++;

                    float i2 = (items.get(i).getLISTA().equals("80") && Double.parseDouble(items.get(i).getVENTAVAL()) > 0.0) ? Vnt80++ : 0;
                    float i3 = (items.get(i).getLISTA().equals("20") && Double.parseDouble(items.get(i).getVENTAVAL()) > 0.0) ? Vnt20++ : 0;

                }

                v80 = (Vnt80 / lst80 ) * 100 ;
                v20 = (Vnt20 / lst20 ) * 100 ;

                String _v80 = String.format(Locale.ENGLISH, "%1$,.0f",  v80);
                String _v20 = String.format(Locale.ENGLISH, "%1$,.0f",  v20);


                id_porcent_80.setText(_v80.concat(" %"));
                id_porcent_20.setText(_v20.concat(" %"));


                String vt80 = String.format(Locale.ENGLISH, "%1$,.0f",  Vnt80);
                String vt20 = String.format(Locale.ENGLISH, "%1$,.0f",  Vnt20);

                String ls80 = String.format(Locale.ENGLISH, "%1$,.0f",  lst80);
                String ls20 = String.format(Locale.ENGLISH, "%1$,.0f",  lst20);


                count_80.setText(("").concat(String.valueOf(vt80)).concat("/").concat(String.valueOf(ls80)));
                count_20.setText(("").concat(String.valueOf(vt20)).concat("/").concat(String.valueOf(ls20)));



                productList.clear();
                productList.addAll(items);


                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_no_facturado, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onContactSelected(Producto_lista8020 product) {

    }
}
