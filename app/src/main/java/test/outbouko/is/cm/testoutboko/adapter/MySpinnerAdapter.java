package test.outbouko.is.cm.testoutboko.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import test.outbouko.is.cm.testoutboko.R;
import test.outbouko.is.cm.testoutboko.model.Agence;

/**
 * Created by Ange_Douki on 24/08/2016.
 */
public class MySpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<Agence> listItems;

    public MySpinnerAdapter(Context context, List<Agence> agences){
        this.context = context;
        this.listItems = agences;
    }
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater li=(LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=li.inflate(R.layout.spinner_agences_item, null);
        }

        TextView textView = (TextView)view.findViewById(R.id.agence_name);
        textView.setText(listItems.get(i).getName());
        return null;
    }
}