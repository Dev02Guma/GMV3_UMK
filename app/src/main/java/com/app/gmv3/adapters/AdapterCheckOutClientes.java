package com.app.gmv3.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.ImageView;
import android.widget.TextView;

import com.app.gmv3.Config;
import com.app.gmv3.R;
import com.app.gmv3.models.Clients;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterCheckOutClientes extends RecyclerView.Adapter<AdapterCheckOutClientes.MyViewHolder> implements Filterable {

    private Context context;
    private List<Clients> ClientList;
    private List<Clients> ClientListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cliente_Nombre, cliente_direccion,cliente_disponible,cliente_saldo,cliente_limite,cliente_moroso;
        public ImageView ly;
        public MyViewHolder(View view) {
            super(view);
            cliente_Nombre = view.findViewById(R.id.client_name);
            cliente_direccion = view.findViewById(R.id.client_dir);
            cliente_disponible = view.findViewById(R.id.id_cliente_disponible);
            cliente_saldo = view.findViewById(R.id.id_cliente_saldo);
            cliente_limite = view.findViewById(R.id.id_cliente_limite);
            cliente_moroso = view.findViewById(R.id.id_lyt_moroso);

            ly = view.findViewById(R.id.id_moroso_img);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(ClientListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public AdapterCheckOutClientes(Context context, List<Clients> productList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.ClientList = productList;
        this.ClientListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Clients clients = ClientListFiltered.get(position);
        holder.cliente_Nombre.setText(clients.getCLIENTE() + " - " + clients.getNOMBRE());
        if (clients.getMOROSO().equals("S")){
           // holder.ly.setBackgroundColor(context.getResources().getColor(R.color.red_light));;
            holder.ly.setImageDrawable(context.getResources().getDrawable(R.drawable.img_person_morosa));
        }else{

            holder.ly.setImageDrawable(context.getResources().getDrawable(R.drawable.img_person));
        }
        holder.cliente_direccion.setText(clients.getDIRECCION());

        holder.cliente_moroso.setVisibility((clients.getCONDPA().contains("Contado") ? View.VISIBLE : View.GONE));

        if (Config.ENABLE_DECIMAL_ROUNDING) {
            String p1 = String.format(Locale.ENGLISH, "%1$,.0f", clients.getDIPONIBLE());
            String p2 = String.format(Locale.ENGLISH, "%1$,.0f", clients.getSALDO());
            String p3 = String.format(Locale.ENGLISH, "%1$,.0f", clients.getLIMITE());

            holder.cliente_disponible.setText("C$ " + p1);
            holder.cliente_saldo.setText("C$ " + p2);
            holder.cliente_limite.setText("C$ " + p3);
        } else {
            holder.cliente_disponible.setText("C$ " + clients.getDIPONIBLE());
            holder.cliente_saldo.setText("C$ " + clients.getSALDO());
            holder.cliente_limite.setText("C$ " + clients.getLIMITE());
        }
    }

    @Override
    public int getItemCount() {
        return ClientListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    ClientListFiltered = ClientList;
                } else {
                    List<Clients> filteredList = new ArrayList<>();
                    for (Clients row : ClientList) {
                        if (row.getNOMBRE().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    ClientListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = ClientListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ClientListFiltered = (ArrayList<Clients>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Clients product);
    }
}
