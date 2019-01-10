package speer.lucas.rfcommbluetoohhandler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DeviceFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.main_list_frag, container, false);
        mRecyclerView = result.findViewById(R.id.mainListRecycler);    //find the recyclerView
        mLayoutManager = new LinearLayoutManager(getContext());     //Get and set a new layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        refreshList();
        return result;
    }

    public void refreshList(){
        mAdapter = new DeviceListAdapter(MainActivity.nameList);            //Get and set the adapter for String[] -> RecyclerView as defined in DeviceListAdapter
        mRecyclerView.setAdapter(mAdapter);
    }
}
