package com.app.gmv3.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.gmv3.BuildConfig;
import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.activities.ActivityHistory;
import com.app.gmv3.activities.ActivityHistoryRecibos;
import com.app.gmv3.activities.ActivityHistoryVineta;
import com.app.gmv3.activities.ActivityInteligenciaMercado;
import com.app.gmv3.activities.ActivityEstadisticas;
import com.app.gmv3.activities.ActivityReportes;
import com.app.gmv3.activities.ActivitySettings;
import com.app.gmv3.activities.ComisionActivity;
import com.app.gmv3.activities.MyApplication;
import com.app.gmv3.activities.PlanTrabajoActivity;
import com.app.gmv3.utilities.SharedPref;

public class FragmentProfile extends Fragment {

    private SharedPref sharedPref;
    TextView txt_user_email;
    TextView txt_user_phone;
    TextView txt_user_address;
    LinearLayout btn_edit_user;
    LinearLayout lyt_root;
    MyApplication MyApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPref = new SharedPref(getActivity());

        lyt_root = view.findViewById(R.id.lyt_root);
        if (Config.ENABLE_RTL_MODE) {
            lyt_root.setRotationY(180);
        }
        MyApp = MyApplication.getInstance();

        txt_user_email = view.findViewById(R.id.txt_user_email);
        txt_user_phone = view.findViewById(R.id.txt_user_phone);
        txt_user_address = view.findViewById(R.id.txt_user_address);

        btn_edit_user = view.findViewById(R.id.btn_edit_user);
        btn_edit_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder( getActivity())
                        .setTitle("Atención.")
                        .setMessage("¿Quiere Salir de la Aplicación?")
                        .setCancelable(false)
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyApp.saveIsLogin(false);
                                MyApp.saveLogin("", "","","","");
                                getActivity().finish();
                            }
                        }).setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });


        view.findViewById(R.id.btn_order_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityHistory.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.id_mis_stat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityEstadisticas.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.id_fragment_comisiones).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ComisionActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.Reportes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityReportes.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.btn_inteligencia_mercado).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityInteligenciaMercado.class);
                intent.putExtra("id_Ruta", sharedPref.getYourName());
                intent.putExtra("Nombre_ruta", sharedPref.getYourAddress());
                startActivity(intent);
            }
        });
        view.findViewById(R.id.txt_user_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivitySettings.class);
               /* intent.putExtra("id_Ruta", sharedPref.getYourName());
                intent.putExtra("Nombre_ruta", sharedPref.getYourAddress());*/
                startActivity(intent);
            }
        });
        view.findViewById(R.id.AboutUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAbout();
            }
        });

        view.findViewById(R.id.btn_vineta_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityHistoryVineta.class);
                intent.putExtra("id_Ruta", sharedPref.getYourName());
                intent.putExtra("Nombre_ruta", sharedPref.getYourAddress());
                startActivity(intent);
            }
        });

        view.findViewById(R.id.id_plan_trabajo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlanTrabajoActivity.class);
                intent.putExtra("id_Ruta", sharedPref.getYourName());
                startActivity(intent);
            }
        });

        view.findViewById(R.id.btn_recibos_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityHistoryRecibos.class);
                intent.putExtra("id_Ruta", sharedPref.getYourName());
                startActivity(intent);
            }
        });



        return view;
    }

    private void showDialogAbout() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_version)).setText( ("Versión ").concat(BuildConfig.VERSION_NAME).concat( " | ").concat(Config.SERVER));

        (dialog.findViewById(R.id.bt_getcode)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onResume() {
        txt_user_email.setText(sharedPref.getYourEmail());
        txt_user_phone.setText(sharedPref.getYourPhone());
        txt_user_address.setText(sharedPref.getYourName().concat(" - ").concat(sharedPref.getYourAddress()));
        super.onResume();
    }

}
