package com.junipersys.stormy.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.junipersys.stormy.R;
import com.junipersys.stormy.adapters.HourlyAdapter;
import com.junipersys.stormy.databinding.ActivityHourlyForecastBinding;
import com.junipersys.stormy.weather.Hour;

import java.util.ArrayList;
import java.util.List;

public class HourlyForecastActivity extends AppCompatActivity {

    private HourlyAdapter adapter;
    private ActivityHourlyForecastBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        List<Hour> hoursList = (ArrayList<Hour>) intent.getSerializableExtra("HourlyList");

        binding = DataBindingUtil.setContentView(this,R.layout.activity_hourly_forecast);

        adapter = new HourlyAdapter(hoursList, this);

        binding.hourlyListItems.setAdapter(adapter);
        binding.hourlyListItems.setHasFixedSize(true);
        binding.hourlyListItems.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.hourlyListItems.setLayoutManager(new LinearLayoutManager(this));

    }



}
