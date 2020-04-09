package com.example.cmpt_cobalt.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cmpt_cobalt.R;
import com.example.cmpt_cobalt.model.RestaurantManager;

public class SearchActivity extends AppCompatActivity {

    //Search filters
    private EditText searchField;
    private EditText violationCountField;
    private Button searchSumbitBtn;
    private Button clearBtn;
    private Spinner hazardSpinner;
    private Spinner comparatorSpinner;
    private CheckBox favouriteCheckBox;

    private RestaurantManager manager = RestaurantManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setDefaultIntent();
        setupSearch();
    }

    private void setupSearch() {
        setupFields();
        setupButtons();
        setupSpinners();
        setupCheckBox();
    }

    private void setupFields() {
        searchField = (EditText) findViewById(R.id.search_field2);
        violationCountField = (EditText) findViewById(R.id.count_text_search);
    }

    private void setupButtons() {
        searchSumbitBtn = (Button) findViewById(R.id.btn_search);
        clearBtn = (Button) findViewById(R.id.clear_button_search);
        searchSumbitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSearch();
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilters();
                finish();
            }
        });
    }

    private void setupSpinners() {
        hazardSpinner = (Spinner) findViewById(R.id.hazard_spinner_search);
        ArrayAdapter<CharSequence> hazardAdapter = ArrayAdapter.createFromResource(this,
                R.array.hazard_level_array, android.R.layout.simple_spinner_dropdown_item);
        hazardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hazardSpinner.setAdapter(hazardAdapter);
        hazardSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                manager.setHazardLevelFilter(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                manager.setHazardLevelFilter(0);
            }
        });

        comparatorSpinner = (Spinner) findViewById(R.id.count_hazard_spinner_search);
        ArrayAdapter<CharSequence> comparatorAdapter = ArrayAdapter.createFromResource(this,
                R.array.comparator, android.R.layout.simple_spinner_dropdown_item);
        comparatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comparatorSpinner.setAdapter(comparatorAdapter);
        comparatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                manager.setComparator(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                manager.setComparator(0);
            }
        });
    }

    private void setupCheckBox() {
        favouriteCheckBox = findViewById(R.id.checkBox_search);
        favouriteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouriteCheckBox.isChecked()) manager.setFavouriteOnly(true);
                else manager.setFavouriteOnly(false);
            }
        });
    }

    private void submitSearch() {
        updateViolationCountRestriction();
        String searchTerm = searchField.getText().toString();
        manager.setSearchTerm(searchTerm);

        this.finish();
    }

    private void updateViolationCountRestriction() {
        try{
            int limit = Integer.parseInt(violationCountField.getText().toString());
            manager.setViolationLimit(limit);
        }
        catch (Exception e) {}
    }

    private void clearFilters() {
        manager.setSearchTerm("");
        manager.setHazardLevelFilter(0);
        manager.setComparator(0);
        manager.setFavouriteOnly(false);
    }

    private void setDefaultIntent() {
        Intent i = new Intent();
        setResult(Activity.RESULT_OK, i);
    }

}
