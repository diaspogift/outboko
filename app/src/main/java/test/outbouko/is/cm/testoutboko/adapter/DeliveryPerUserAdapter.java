package test.outbouko.is.cm.testoutboko.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import test.outbouko.is.cm.testoutboko.Deliveries;
import test.outbouko.is.cm.testoutboko.R;
import test.outbouko.is.cm.testoutboko.model.Delivery;

/**
 * Created by Ange_Douki on 24/08/2016.
 */
public class DeliveryPerUserAdapter extends BaseAdapter {
    private Context context;
    private List<Delivery> deliveryList;

    public DeliveryPerUserAdapter(Context context, List<Delivery> deliveryList) {
        this.context = context;
        this.deliveryList = deliveryList;
    }

    @Override
    public int getCount() {
        return this.deliveryList.size();
    }

    @Override
    public Object getItem(int i) {
        return deliveryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return deliveryList.get(i).getId();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.delivery_list_item_for_agent, null);
        }
        final Delivery delivery = deliveryList.get(i);

        TextView trackingcode = (TextView) view.findViewById(R.id.trackingcode_1);
        trackingcode.setText(delivery.getTracking_code());

        TextView nom = (TextView) view.findViewById(R.id.nom_1);
        nom.setText(delivery.getName_surname());

        TextView tel = (TextView) view.findViewById(R.id.tel_1);
        tel.setText(delivery.getPhonenumber());

        TextView email = (TextView) view.findViewById(R.id.email_1);
        email.setText(delivery.getEmail());

        TextView quarter = (TextView) view.findViewById(R.id.quarter_1);
        quarter.setText(delivery.getQuarter());

        TextView description_1 = (TextView) view.findViewById(R.id.description_1);
        description_1.setText(delivery.getDescription());

        final CheckBox checkbox_choosen = (CheckBox) view.findViewById(R.id.checkbox_choosen);

        checkbox_choosen.setChecked(delivery.isChecked());

        checkbox_choosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Delivery del = deliveryList.get(i);
                del.setChecked(checkbox_choosen.isChecked());

                /*LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);*/
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View fragment_decharge_view = layoutInflater.inflate(R.layout.fragment_decharge, null);
                CheckBox checkbox_all = (CheckBox) fragment_decharge_view.findViewById(R.id.checkbox_all);
                if (checkbox_choosen.isChecked() == false){
                    checkbox_all.setChecked(checkbox_choosen.isChecked());
                }else{
                    boolean trovato = false;
                    for (Delivery d : deliveryList){
                        if (d.isChecked() == false){
                            trovato = true; break;
                        }
                    }
                    if (trovato){
                        checkbox_all.setChecked(false);
                    }else {
                        checkbox_all.setChecked(true);
                    }
                }

                notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }
}
