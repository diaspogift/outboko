package test.outbouko.is.cm.testoutboko.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import test.outbouko.is.cm.testoutboko.R;
import test.outbouko.is.cm.testoutboko.model.Delivery;

/**
 * Created by Ange_Douki on 06/08/2016.
 */
public class DeliveryAdapter extends BaseAdapter {
    private Context context;
    private List<Delivery> deliveryList;

    public DeliveryAdapter(Context context, List<Delivery> deliveryList) {
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.delivery_list_item, null);
        }
        Delivery delivery = deliveryList.get(i);

        TextView trackingcode = (TextView) view.findViewById(R.id.trackingcode);
        trackingcode.setText(delivery.getTracking_code());

        TextView nom = (TextView) view.findViewById(R.id.nom);
        nom.setText(delivery.getName_surname());

        TextView tel = (TextView) view.findViewById(R.id.tel);
        tel.setText(delivery.getPhonenumber());

        TextView email = (TextView) view.findViewById(R.id.email);
        email.setText(delivery.getEmail());

        TextView quarter = (TextView) view.findViewById(R.id.quarter);
        quarter.setText(delivery.getQuarter());
        return view;
    }

    @Override
    public boolean hasStableIds(){
        return true;
    }
}
