package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lksnext.parkingplantilla.R;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar el layout de perfil (cambia R.layout.activity_profile por el que uses)
        setContentView(R.layout.activity_profile);
    }
}
