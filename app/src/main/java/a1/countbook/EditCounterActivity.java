package a1.countbook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import a1.countbook.R;


public class EditCounterActivity extends AppCompatActivity {
    private int position;
    private Counter counter;

    private TextView c_name;
    private TextView c_value;
    private TextView c_comment;
    private TextView lastDate;
    private TextView createDate;
    private TextView initial;
    private Button minus;
    private Button plus;
    private Button delete;
    private Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_counter);

        Intent intent = getIntent();
        position = Integer.parseInt(intent.getStringExtra(CounterActivity.CURRENT_POSITION));

        counter = CounterActivity.counters.get(position); //get the counter in a specific position

        c_name = (TextView) findViewById(R.id.counter_name);
        c_value = (TextView) findViewById(R.id.counter_value);
        c_comment = (TextView) findViewById(R.id.counter_comment);
        lastDate = (TextView) findViewById(R.id.counter_date);
        createDate = (TextView) findViewById(R.id.create_date);
        initial = (TextView)findViewById(R.id.init_value);

        minus = (Button) findViewById(R.id.minus_btn);
        plus = (Button) findViewById(R.id.plus_btn);
        delete = (Button) findViewById(R.id.delete_btn);
        reset = (Button) findViewById(R.id.reset_btn);
        //set all attributes in a counter to edit counter window
        c_name.setText(counter.getName());
        c_value.setText(String.valueOf(counter.getValue()));
        c_comment.setText(CounterActivity.counters.get(position).getComment());
        lastDate.setText("Last modified: "+counter.getLastDate());
        createDate.setText("Created on: "+counter.getDate());
        initial.setText("Initial value: "+String.valueOf(counter.getInitial()));

        //handle edit name event
        c_name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                editAttributes(c_name, "Edit name");
            }
        });
        //handle edit number event
        c_value.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                editAttributes(c_value, "Edit number");
            }
        });
        //handle edit comment event
        c_comment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                editAttributes(c_comment, "Edit comment");
            }
        });
        //handle "-1" button event to decrease counter number by 1 in a valid range
        minus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                unitChange(-1);
            }
        });
        //handle "+1" button event to increase counter number by 1 in a valid range
        plus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                unitChange(1);
            }
        });
        //handle delete button event to delete a counter
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                CounterActivity.counters.remove(position);
                CounterActivity.saveInFile(EditCounterActivity.this);
                finish();
            }
        });
        //handle reset button event to reset counter to it's initial value
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                int init = counter.getInitial();
                c_value.setText(String.valueOf(init));
                counter.setValue(init);
                CounterActivity.saveInFile(EditCounterActivity.this);
            }
        });

    }

    private void editAttributes(final TextView attr, String editType){
        //This function would allow user to edit each attribute in a counter,
        //and update the last modify date
        LayoutInflater inflater = LayoutInflater.from(EditCounterActivity.this);
        View dialog = inflater.inflate(R.layout.edit_counter, null);

        final EditText input = (EditText)dialog.findViewById(R.id.edit_name);

        // Check if the input field is for editing counter number
        if(editType == "Edit number"){
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        // set edit comment input type to multiLines
        else if(editType == "Edit comment"){
            input.setSingleLine(false);
            input.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        }

        AlertDialog.Builder dialog_build = new AlertDialog.Builder(EditCounterActivity.this);

        dialog_build.setView(dialog);

        dialog_build
                .setTitle(editType)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int d){
                        //handle "Confirm" button event on alert diagram
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int d) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = dialog_build.create();
        alertDialog.show();
        // Change positive button default setting
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Boolean closeDialog = true;
                String change = input.getText().toString();
                //check if user editing name or value of counter
                if (attr == c_name || attr == c_value){
                    if(!change.isEmpty()){
                        //if input name is not empty, update the change
                        if (attr == c_name){
                            lastDate.setText("Last modified: "+counter.getLastDate());
                            attr.setText(change);
                            counter.setName(change);
                        }
                        else if(attr == c_value){
                            //if input value is valid, update the change
                            //else, display an alert
                            if(change.length() < 10) {
                                lastDate.setText("Last modified: "+counter.getLastDate());
                                attr.setText(change);
                                counter.setValue(Integer.parseInt(change));
                            }else{
                                closeDialog = false;
                                Toast.makeText(EditCounterActivity.this,"Cannot over 9 digits", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        closeDialog = false;
                        Toast.makeText(EditCounterActivity.this,"Cannot leave empty", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //update the change of comment and its modified date
                    lastDate.setText("Last modified: "+counter.getLastDate());
                    attr.setText(change);
                    counter.setComment(change);
                }

                if (closeDialog) {
                    alertDialog.dismiss();
                    CounterActivity.saveInFile(EditCounterActivity.this); //store the change in file
                }
            }
        });
    }

    private void unitChange(int unit) {
        // This class hold for unit change of one counter's number
        // Every time it's called, counter's value increase 1 or decrease 1
        // Value won't change if it out of range between (0, maximum 9 digits integer)
        int newValue = Integer.parseInt(c_value.getText().toString()) + unit;

        if (String.valueOf(newValue).length() >= 10) {
            Toast.makeText(EditCounterActivity.this, "Counter digits cannot over 9", Toast.LENGTH_SHORT).show();
        } else if (newValue < 0) {
            Toast.makeText(EditCounterActivity.this, "Counter number cannot smaller than 0", Toast.LENGTH_SHORT).show();
        } else {
            lastDate.setText("Last modified: "+counter.getLastDate());
            c_value.setText(String.valueOf(newValue));
            counter.setValue(newValue);
            CounterActivity.saveInFile(EditCounterActivity.this); //store the change in file
        }
    }
}