package speer.lucas.rfcommbluetoohhandler;
/*
    This adapter takes a string array of strings and returns an adapter for a recycler view
*/
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CommandListAdapter extends RecyclerView.Adapter<CommandListAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private String[] mDataSet;              //String array of wifi networks to be shown
    private Resources resources;            //Resources for getting colors
    private static TextView textViewArr[];         //TextView array for looping through to highlight/unhighlight

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectedActivity.connCurrentSelection = ConnectedActivity.commandList[getAdapterPosition()];
                }
            });
            textView = v.findViewById(R.id.simpleText);     //Get the xml element to display the text in
        }
        public TextView getTextView() {
            return textView;
        }
    }

    public CommandListAdapter(String[] dataSet){
        mDataSet = dataSet;
        textViewArr = new TextView[dataSet.length]; //initialize the TextView array when the adapter is first created
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        resources = parent.getResources();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final int pos = holder.getAdapterPosition();
        holder.getTextView().setText(mDataSet[pos]);
        textViewArr[pos] = holder.getTextView();       //Add each TextView into the textViewArr array
        holder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectedActivity.connCurrentSelection = mDataSet[pos];   //Get the Device selected
                ConnectedActivity.selectionPosition = pos;            //and the position in the array

                for(int i = 0; i < textViewArr.length; i++) {
                    textViewArr[i].setBackground(null);
                }
                textViewArr[pos].setBackgroundColor(resources.getColor(R.color.colorAccent)); //Highlight the selected option

            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.length;

    }
}