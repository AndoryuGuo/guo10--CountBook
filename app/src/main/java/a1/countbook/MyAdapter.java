package a1.countbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class MyAdapter extends ArrayAdapter<Counter> {
    private ArrayList<Counter> counters;

    public MyAdapter(Context context, int textviewLayoutId, ArrayList<Counter> counters){
        super(context, textviewLayoutId, counters);
        this.counters = counters;
    }
    @Override
    public int getCount() {
        // return the numbers of element in counter list
        return counters.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get all attributes from on counter in specific position
        String name = getItem(position).getName();
        String num = Integer.toString(getItem(position).getValue());
        String date = getItem(position).getLastDate();
        final String pos = Integer.toString(position);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.custom_list_item, parent, false);

        TextView sinName = (TextView) v.findViewById(R.id.list_name);
        TextView sinNum = (TextView) v.findViewById(R.id.list_value);
        TextView sinDate = (TextView) v.findViewById(R.id.list_date);
        //display one counter in counters list in specific position
        sinName.setText(name);
        sinNum.setText(num);
        sinDate.setText(date);
        //once a counter in listview is clicked, a new activity starts
        sinNum.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditCounterActivity.class);
                intent.putExtra(CounterActivity.CURRENT_POSITION, pos); //send the position of current counter to CounterActivity class
                v.getContext().startActivity(intent); //Start new action
            }
        });

        return v;
    }
}
