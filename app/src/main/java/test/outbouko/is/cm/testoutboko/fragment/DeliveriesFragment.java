package test.outbouko.is.cm.testoutboko.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.List;

import test.outbouko.is.cm.testoutboko.Pickup;
import test.outbouko.is.cm.testoutboko.R;
import test.outbouko.is.cm.testoutboko.adapter.DeliveryAdapter;
import test.outbouko.is.cm.testoutboko.model.Delivery;
import test.outbouko.is.cm.testoutboko.sqlite.DeliveryDataSource;
import test.outbouko.is.cm.testoutboko.util.Util;

/**
 * Created by Ange_Douki on 21/08/2016.
 */
public class DeliveriesFragment extends Fragment {

    private List<Delivery> deliveryList ;

    private DragSortListView dynamicListView;

    private DeliveryDataSource deliveryDataSource;// = new DeliveryDataSource(context);

    private DeliveryAdapter adapter;

    public DeliveriesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_deliveries_content, container, false);
        dynamicListView = (DragSortListView) view.findViewById(R.id.deliveries_list);

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();

        deliveryDataSource = new DeliveryDataSource(getActivity().getApplicationContext());
        deliveryDataSource.open();

        deliveryList = deliveryDataSource.getAllDelivery();
        adapter = new DeliveryAdapter(getActivity().getApplicationContext(), deliveryList);

        dynamicListView.setAdapter(adapter);

        dynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Pickup.class);
                SharedPreferences prefs = getActivity().getSharedPreferences(Util.PreferenceTokenName, getActivity().MODE_PRIVATE);
                String email = prefs.getString(Util.EMAIL, null);
                if (email != null)
                    intent.putExtra(Util.EMAIL, email);
                String password = prefs.getString(Util.PASSWORD, null);
                if (password != null)
                    intent.putExtra(Util.PASSWORD, password);
                else Toast.makeText(getActivity().getApplicationContext(), "Null password", Toast.LENGTH_LONG).show();

                Delivery delivery = deliveryList.get(i);
                intent.putExtra(Util.TRACKING_CODE, delivery.getTracking_code());
                startActivity(intent);
            }
        });

        dynamicListView.setDropListener(new DragSortListView.DropListener() {
            @Override public void drop(int from, int to) {
                Delivery delivery = deliveryList.get(from);
                deliveryList.remove(from);
                if (from > to) --from;
                deliveryList.add(to, delivery);
                adapter.notifyDataSetChanged();
            }
        });

        DragSortController controller = new DragSortController(dynamicListView);
        controller.setDragHandleId(R.id.handler);
        //controller.setClickRemoveId(R.id.);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(1);
        //controller.setRemoveMode(removeMode);

        dynamicListView.setFloatViewManager(controller);
        dynamicListView.setOnTouchListener(controller);
        dynamicListView.setDragEnabled(true);


        /*ArrayList<String> keys = bundle.getStringArrayList(Util.KEYS);
        if (keys != null){
            for (String string : keys){
                bundle.remove(string);
            }
        }*/
        deliveryDataSource.close();
    }

}
