package speer.lucas.rfcommbluetoohhandler;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private String[] mDataSet;              //String array of wifi networks to be shown
    private TextView clickedBackground;     //for highlighting the selection
    private TextView unclickedBackground;   //for unhighlighting
    private Resources resources;
    private static TextView deviceOptions[];         //TextView array for looping through to highlight/unhighlight
    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfActivity.currentSelection = ConfActivity.nameList[getAdapterPosition()];
                }
            });
            textView = (TextView) v.findViewById(R.id.simpleText);     //Get the xml element to display the text in
        }
        public TextView getTextView() {
            return textView;
        }
    }

    public MyAdapter(String[] dataSet){
        mDataSet = dataSet;
        deviceOptions = new TextView[dataSet.length]; //initialize the TextView array when the adapter is first created
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        clickedBackground = view.findViewById(R.id.clickedText);       //get the TextViews from item_simple_itemview.xml for highlighting/unhighlighting
        unclickedBackground = view.findViewById(R.id.simpleText);
        resources = parent.getResources();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.getTextView().setText(mDataSet[position]);
        deviceOptions[position] = holder.getTextView();       //Add each TextView into the deviceOptions array
        holder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfActivity.currentSelection = mDataSet[position];   //Get the Device selected
                ConfActivity.selectionPosition = position;
                for(int i = 0; i < deviceOptions.length; i++) {
                    deviceOptions[i].setBackgroundColor(resources.getColor(R.color.white));
                }
                deviceOptions[position].setBackgroundColor(resources.getColor(R.color.colorAccent)); //Highlight the selected option

            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.length;

    }
}