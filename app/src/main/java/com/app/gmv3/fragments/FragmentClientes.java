package com.app.gmv3.fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.activities.MyApplication;
import com.app.gmv3.activities.ActivityPerfilCliente;
import com.app.gmv3.adapters.AdapterClientes;
import com.app.gmv3.models.Clients;
import com.app.gmv3.utilities.SharedPref;
import com.app.gmv3.utilities.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.app.gmv3.utilities.Constant.GET_CLIENTS;

public class FragmentClientes extends Fragment implements AdapterClientes.ContactsAdapterListener {

    private RecyclerView recyclerView;
    private List<Clients> categoryList;
    private AdapterClientes mAdapter;
    private SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout = null;
    LinearLayout lyt_root;
    View lyt_empty_history;
    SharedPref sharedPref;
    private static final String[] ANIMATION_TYPE = new String[]{
            "Todos","Ver Solo Moroso"
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        setHasOptionsMenu(true);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        sharedPref = new SharedPref(getContext());
        lyt_empty_history = view.findViewById(R.id.lyt_empty_history);
        lyt_root = view.findViewById(R.id.lyt_root);
        if (Config.ENABLE_RTL_MODE) {
            lyt_root.setRotationY(180);
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        categoryList = new ArrayList<>();
        mAdapter = new AdapterClientes(getActivity(), categoryList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        view.findViewById(R.id.bt_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchContacts();
            }
        });

        fetchContacts();
        onRefresh();



        return view;
    }

    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                categoryList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(getActivity())) {
                            swipeRefreshLayout.setRefreshing(false);
                            fetchContacts();
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, 1500);
            }
        });
    }

    private void fetchContacts() {
        JsonArrayRequest request = new JsonArrayRequest(GET_CLIENTS + sharedPref.getYourName(), new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.failed_fetch_data), Toast.LENGTH_LONG).show();
                            return;
                        }

                        List<Clients> items = new Gson().fromJson(response.toString(), new TypeToken<List<Clients>>() {
                        }.getType());

                        // adding contacts to contacts list
                        categoryList.clear();
                        categoryList.addAll(items);
                        if (categoryList.size() > 0) {
                            lyt_empty_history.setVisibility(View.GONE);
                        } else {
                            lyt_empty_history.setVisibility(View.VISIBLE);
                        }

                        // refreshing recycler view
                        mAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e("INFO", "Error: " + error.getMessage());
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_cliente, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
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

            case R.id.action_filtro:

                showSingleChoiceDialog();
                return true;




            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showSingleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ordenar por: ");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(ANIMATION_TYPE, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selected = ANIMATION_TYPE[i];
                if (selected.equalsIgnoreCase("Todos")) {
                    mAdapter.getFilter().filter("");
                } else if (selected.equalsIgnoreCase("Ver Solo Moroso")) {
                    mAdapter.getFilter().filter("MOROSO");
                }

                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onContactSelected(Clients clients) {
        //Toast.makeText(getActivity(), "Selected: " + category.getCategory_name(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), ActivityPerfilCliente.class);
        intent.putExtra("Client_Code", clients.getCLIENTE());
        intent.putExtra("CLient_name", clients.getNOMBRE());
        intent.putExtra("Telefono", clients.getTELE());
        intent.putExtra("Condicion_pago", clients.getCONDPA());
        intent.putExtra("Limite", clients.getLIMITE());
        intent.putExtra("Saldo", clients.getSALDO());
        intent.putExtra("Disponible", clients.getDIPONIBLE());
        intent.putExtra("Verificado", clients.getVERIFICADO());
        intent.putExtra("pin", clients.getPIN());
        intent.putExtra("Direccion", clients.getDIRECCION());
        intent.putExtra("vineta_saldo", clients.getVineta());
        intent.putExtra("moroso", clients.getMOROSO());
        intent.putExtra("PLAN", clients.getPLAN());
        startActivity(intent);
    }

}
