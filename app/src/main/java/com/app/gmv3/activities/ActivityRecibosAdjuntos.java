package com.app.gmv3.activities;

import static com.app.gmv3.utilities.Constant.GET_RECIBOS_COLECTOR;
import static com.app.gmv3.utilities.Constant.POST_ADJUNTOS;
import static com.app.gmv3.utilities.Constant.GET_RECIBOS_ADJUNTO;
import static com.app.gmv3.utilities.Constant.POST_ANULAR_RECIBO;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.adapters.RecibosAdapter;
import com.app.gmv3.models.ItemHistorico;
import com.app.gmv3.models.ItemRecibosAttach;
import com.app.gmv3.models.ItemsAttach;
import com.app.gmv3.utilities.Constant;
import com.app.gmv3.utilities.ImageTransformation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;

import org.json.JSONArray;


public class ActivityRecibosAdjuntos extends AppCompatActivity {
    private static final int PICK_FROM_GALLERY = 101;

    RecyclerView newAttachmentListView;
    RelativeLayout lyt_order;
    View lyt_empty_cart;
    private ArrayList<ItemRecibosAttach> newAttachmentList = new ArrayList<>();
    RecibosAdapter attachmentListAdapter;
    AppCompatButton btn_checkout;
    ProgressDialog progressDialog;
    ImageView btn_add_photos;

    List<Uri> listaImagenes = new ArrayList<>();
    List<String> listaBase64Imagenes = new ArrayList<>();

    private String _idRecibo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recibos_adjuntos);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Comprobantes.");
        }

        Intent intent = getIntent();
        _idRecibo     = intent.getStringExtra("id_recibo");

        progressDialog = new ProgressDialog(ActivityRecibosAdjuntos.this);
        lyt_order               = findViewById(R.id.lyt_history);
        lyt_empty_cart          = findViewById(R.id.lyt_empty_history);
        newAttachmentListView   = findViewById(R.id.newAttachmentList);
        btn_checkout            = findViewById(R.id.buttonAttachment);
        btn_add_photos              = findViewById(R.id.id_add_photos);

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send();

            }
        });
        btn_add_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFolder();
            }
        });
        getAdjuntos();

    }

    private void getAdjuntos() {
        JsonArrayRequest request = new JsonArrayRequest(GET_RECIBOS_ADJUNTO + "/" + _idRecibo, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.failed_fetch_data, Toast.LENGTH_LONG).show();
                    return;
                }

                List<ItemsAttach> items = new Gson().fromJson(response.toString(), new TypeToken<List<ItemsAttach>>() {
                }.getType());

                for (int i = 0; i < items.size(); i++) {

                    ItemRecibosAttach itemRecibosAttach = new ItemRecibosAttach();
                    itemRecibosAttach.setImageName(items.get(i).getmNombreImagen());
                    itemRecibosAttach.setImageID(items.get(i).getImagen_url());
                    itemRecibosAttach.setmDelete(false);
                    newAttachmentList.add(itemRecibosAttach);
                }

                generateNewAttachmentList(newAttachmentList);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                Log.e("INFO", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
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
    public void openFolder() {
        if (check_ReadStoragepermission()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "SELECCIONA LAS IMAGENES"), PICK_FROM_GALLERY);
        }

    }
    private boolean check_ReadStoragepermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constant.permission_Read_data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }



    public void Send(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRecibosAdjuntos.this);
        builder.setTitle(R.string.post_dialog_title);
        builder.setMessage(R.string.post_dialog_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.dialog_option_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listaBase64Imagenes.clear();
                for(int i = 0 ; i < listaImagenes.size() ; i++) {
                    try {
                        InputStream is = getContentResolver().openInputStream(listaImagenes.get(i));
                        //Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Bitmap bitmap = ImageTransformation.getResizedBitmap(BitmapFactory.decodeStream(is), 600);

                        String cadena = convertirUriToBase64(bitmap);

                        enviarImagenes("nomIma"+i, cadena);

                        bitmap.recycle();

                    } catch (IOException e) { }

                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.dialog_option_no), null);
        builder.setCancelable(false);
        builder.show();


    }

    public String convertirUriToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String encode = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encode;
    }
    public void dialogSuccessOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.post_success_title);
        builder.setMessage(R.string.post_success_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.checkout_option_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void enviarImagenes(final String nombre, final String cadena) {




        progressDialog.setTitle(getString(R.string.post_submit_title));
        progressDialog.setMessage(getString(R.string.post_submit_msg));
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_ADJUNTOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                dialogSuccessOrder();
                            }
                        }, 2000);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                progressDialog.dismiss();
                Log.e("TAG_error", "onErrorResponse: " + e.getMessage());
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("nom", nombre);
                params.put("imagenes", cadena);
                params.put("Id_Recibo", _idRecibo);

                return params;
            }
        };

        requestQueue.add(stringRequest);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (int i = 0; i < count; i++) {

                    Uri returnUri = data.getClipData().getItemAt(i).getUri();

                    Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                    returnCursor.moveToFirst();


                    ItemRecibosAttach itemRecibosAttach = new ItemRecibosAttach();
                    itemRecibosAttach.setImageName(returnCursor.getString(nameIndex));
                    itemRecibosAttach.setImageID(returnUri.toString());

                    itemRecibosAttach.setmDelete(true);
                    newAttachmentList.add(itemRecibosAttach);

                    listaImagenes.add(returnUri);

                }

            } else if (data.getData() != null) {



                Uri returnUri = data.getData();
                Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();

                System.out.println("PIYUSH NAME IS" + returnCursor.getString(nameIndex));
                System.out.println("PIYUSH SIZE IS" + Long.toString(returnCursor.getLong(sizeIndex)));

                Log.e("TAG_", "PIYUSH NAME IS " + returnCursor.getString(nameIndex) );
                Log.e("TAG_", "PIYUSH SIZE IS " + Long.toString(returnCursor.getLong(sizeIndex)) );

                ItemRecibosAttach itemRecibosAttach = new ItemRecibosAttach();
                itemRecibosAttach.setImageName(returnCursor.getString(nameIndex));
                itemRecibosAttach.setImageID(returnUri.toString());
                itemRecibosAttach.setmDelete(true);
                newAttachmentList.add(itemRecibosAttach);

                listaImagenes.add(returnUri);

            }

            generateNewAttachmentList(newAttachmentList);
        }

    }




    private void generateNewAttachmentList(ArrayList<ItemRecibosAttach> newAttachmentList) {
        newAttachmentListView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(ActivityRecibosAdjuntos.this);
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newAttachmentListView.setLayoutManager(MyLayoutManager);
        attachmentListAdapter = new RecibosAdapter(newAttachmentList, ActivityRecibosAdjuntos.this);
        newAttachmentListView.setAdapter(attachmentListAdapter);

        /*if (attachmentListAdapter.getItemCount() > 0) {
            lyt_order.setVisibility(View.VISIBLE);
        } else {
            lyt_empty_cart.setVisibility(View.VISIBLE);
        }*/
    }


}