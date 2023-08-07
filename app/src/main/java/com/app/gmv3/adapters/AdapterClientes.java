package com.app.gmv3.adapters;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.gmv3.R;
import com.app.gmv3.models.Clients;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterClientes extends RecyclerView.Adapter<AdapterClientes.MyViewHolder> implements Filterable {

    private Context context;
    private List<Clients> categoryList;
    private List<Clients> categoryListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category_name, product_count,txt_cliente_codigo,txt_cliente_limite,txt_cliente_saldo,txt_cliente_disponible;
        public CardView cardView;
        public TextView relativeLayout;
        public CircleImageView ImgVerication;
        public LinearLayout lytPin;

        public ImageView imgPlan;


        public MyViewHolder(View view) {
            super(view);
            imgPlan                 = view.findViewById(R.id.id_img_plan);
            relativeLayout          = view.findViewById(R.id.id_lyt_moroso);
            lytPin                  = view.findViewById(R.id.id_lyt_pin);
            category_name           = view.findViewById(R.id.category_name);
            product_count           = view.findViewById(R.id.product_count);
            txt_cliente_codigo      = view.findViewById(R.id.id_cliente_codigo);
            txt_cliente_limite      = view.findViewById(R.id.id_cliente_limite);
            txt_cliente_saldo       = view.findViewById(R.id.id_cliente_saldo);
            txt_cliente_disponible  = view.findViewById(R.id.id_cliente_disponible);
            cardView                = view.findViewById(R.id.id_element_cardview);
            ImgVerication           = view.findViewById(R.id.btn_verificacion);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(categoryListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public AdapterClientes(Context context, List<Clients> categoryList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.categoryList = categoryList;
        this.categoryListFiltered = categoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clientes, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Clients clients = categoryListFiltered.get(position);

        holder.category_name.setText(clients.getCLIENTE().concat(" - ").concat(clients.getNOMBRE()));
        holder.cardView.setBackgroundColor(context.getResources().getColor(((clients.getMOROSO().equals("S")) ? R.color.red_light : R.color.white)));
        holder.imgPlan.setVisibility((clients.getPLAN().equals("S") ? View.VISIBLE : View.GONE));
        holder.relativeLayout.setVisibility((clients.getMOROSO().equals("S") ? View.VISIBLE : View.GONE));
        holder.lytPin.setVisibility((clients.getPIN().equals("S") ? View.VISIBLE : View.GONE));
        holder.product_count.setText(clients.getDIRECCION());
        holder.txt_cliente_codigo.setText(clients.getNIVEL_PRECIO());
        holder.ImgVerication.setVisibility((clients.getVERIFICADO().contains("S;") ? View.VISIBLE : View.GONE));
        holder.txt_cliente_limite.setText(("C$ ").concat(clients.getLIMITE()));
        holder.txt_cliente_saldo.setText(("C$ ").concat(clients.getSALDO()));
        holder.txt_cliente_disponible.setText(("C$ ").concat(clients.getDIPONIBLE()));


    }

    @Override
    public int getItemCount() {
        return categoryListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    categoryListFiltered = categoryList;
                } else {
                    List<Clients> filteredList = new ArrayList<>();

                    for (Clients row : categoryList) {
                        if (row.getNOMBRE().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    categoryListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = categoryListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                categoryListFiltered = (ArrayList<Clients>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Clients clients);
    }
}
