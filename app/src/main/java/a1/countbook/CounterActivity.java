package a1.countbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CounterActivity extends AppCompatActivity {

    public static final String CURRENT_POSITION = "Counter Position for clicked item";

    private static final String filename = "file.sav";
    private ListView countersList;
    Context context = this;
    private TextView numText;
    public static ArrayList<Counter> counters = new ArrayList<Counter>();
    public static ArrayAdapter<Counter> adapter;

    private EditText input_name;
    private EditText input_value;
    private EditText input_comment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button createButton = (Button) findViewById(R.id.create);
        countersList = (ListView) findViewById(R.id.countersList);

        createButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Create an alert dialog to create a new counter when "create" button is clicked
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialog = inflater.inflate(R.layout.initial_counter, null);

                AlertDialog.Builder dialog_build = new AlertDialog.Builder(context);

                dialog_build.setView(dialog);

                //These three variables are the values that user inputs
                input_name = (EditText)dialog.findViewById(R.id.name);
                input_value = (EditText)dialog.findViewById(R.id.value);
                input_comment = (EditText)dialog.findViewById(R.id.comment);

                dialog_build
                        .setTitle("New Counter")
                        .setPositiveButton("Create", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int d){
                                //handle "Create" button activity on alert dialog
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int d) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alertDialog = dialog_build.create();
                alertDialog.show();

                //This function check if the input fits requirement
                checkInput(alertDialog);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadFromFile();
        counterSummary(); //Update counterSummary for each time a new activity occurs
        adapter = new MyAdapter(CounterActivity.this, R.layout.custom_list_item, counters); //Custom adapter
        countersList.setAdapter(adapter);
    }

    private void loadFromFile() {
        try {
            FileInputStream fis = openFileInput(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Counter>>() {}.getType();
            counters = gson.fromJson(in, listType);
        } catch (FileNotFoundException e) {
            counters = new ArrayList<Counter>();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void saveInFile(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(counters, writer);
            writer.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    private void checkInput(final AlertDialog dialog){
        //Change positive button default setting
        /*This function check if the input is valid
        If the input counter name is empty or
        the input counter value is empty or
        the input counter value is out of range
        (0, max 9 digits num), a toast dialog with
        reminder would display, and return to alert dialog
          However,
        if the input is valid, it would update the new
        counter name, value and comment to counters list
        and save it into file*/


        //
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean closeDialog = true;
                String name = input_name.getText().toString();
                String value = input_value.getText().toString();
                String comment = input_comment.getText().toString();
                if (name.isEmpty()) {
                    //if input name is empty, show toast alert
                    closeDialog = false;
                    Toast.makeText(context, "Cannot leave name empty", Toast.LENGTH_SHORT).show();
                } else if(value.isEmpty()) {
                    //if input value is empty, show toast alert
                    closeDialog = false;
                    Toast.makeText(context, "Cannot leave number empty", Toast.LENGTH_SHORT).show();
                } else if(value.length() > 10) {
                    //if input value more than 9 digits, show alert
                    closeDialog = false;
                    Toast.makeText(context, "Cannot over 9 digits", Toast.LENGTH_SHORT).show();
                } else {
                    //else, store the new counter's values to counters list, and notify adapter data
                    //changed
                    counters.add(new Counter(name, Integer.parseInt(value), comment));
                    adapter.notifyDataSetChanged();
                }

                if (closeDialog) {
                    dialog.dismiss();
                    counterSummary(); //update summary of counter for each time creates a new counter
                    CounterActivity.saveInFile(context);
                }
            }
        });
    }

    public void counterSummary(){
        /* This function check the total numbers of counter from counters list, and update
        summary on main windows of this application
         */
        numText = (TextView) findViewById(R.id.numOfCount);
        numText.setText(counters.size()+" Counters");
    }
}
