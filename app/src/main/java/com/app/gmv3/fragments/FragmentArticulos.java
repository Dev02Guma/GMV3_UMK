package com.app.gmv3.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityProductDetail;
import com.app.gmv3.activities.MainActivity;
import com.app.gmv3.activities.MyApplication;
import com.app.gmv3.adapters.AdapterProduct;
import com.app.gmv3.models.Product;
import com.app.gmv3.utilities.ItemOffsetDecoration;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.app.gmv3.utilities.Constant.GET_RECENT_PRODUCT;

public class FragmentArticulos extends Fragment implements AdapterProduct.ContactsAdapterListener  {

    private RecyclerView recyclerView;
    private List<Product> productList;

    private AdapterProduct mAdapter;
    private SearchView searchView;
    View lyt_empty_history;
    SwipeRefreshLayout swipeRefreshLayout = null;
    LinearLayout lyt_root;

    SharedPref sharedPref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        setHasOptionsMenu(true);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);
        lyt_empty_history = view.findViewById(R.id.lyt_empty_history);

        sharedPref = new SharedPref(getContext());

        lyt_root = view.findViewById(R.id.lyt_root);
        if (Config.ENABLE_RTL_MODE) {
            lyt_root.setRotationY(180);
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new AdapterProduct(getActivity(), productList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        view.findViewById(R.id.bt_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Articulos (  )");

        fetchData();
        onRefresh();


        return view;
    }

    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(getActivity())) {
                            swipeRefreshLayout.setRefreshing(false);
                            fetchData();
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 1500);
            }
        });
    }

    private void fetchData() {
        final String[] RutaAsignada = new String[1];
        JsonArrayRequest request = new JsonArrayRequest(GET_RECENT_PRODUCT + sharedPref.getYourName(), new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Product> items = new Gson().fromJson(response.toString(), new TypeToken<List<Product>>() {
                        }.getType());

                        // adding contacts to contacts list



                        productList.clear();
                        productList.addAll(items);

                        if (productList.size() > 0) {
                            List<String> sVinneta = Arrays.asList(items.get(0).getISPROMO().split(":"));
                            RutaAsignada[0] = sVinneta.get(2);


                            sharedPref.setPathAssigned(RutaAsignada[0]);
                            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("ARTICULOS ( "+ sharedPref.getPathAssigned() +" )");


                            lyt_empty_history.setVisibility(View.GONE);
                        } else {
                            lyt_empty_history.setVisibility(View.VISIBLE);
                        }

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json

                Log.e("INFO", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onContactSelected(Product product) {
        Intent intent = new Intent(getActivity(), ActivityProductDetail.class);
        intent.putExtra("product_id", product.getProduct_id());
        intent.putExtra("title", product.getProduct_name());
        intent.putExtra("image", product.getProduct_image());
        intent.putExtra("product_price", product.getProduct_price());
        intent.putExtra("product_description", product.getProduct_description());
        intent.putExtra("product_quantity", product.getProduct_quantity());
        intent.putExtra("product_status", product.getProduct_status());
        intent.putExtra("currency_code", product.getCurrency_code());
        intent.putExtra("category_name", product.getCategory_name());
        intent.putExtra("product_bonificado", product.getProduct_bonificado());
        intent.putExtra("product_lotes", product.getProduct_lotes());
        intent.putExtra("product_und", product.getProduct_und());


        startActivity(intent);
    }

}