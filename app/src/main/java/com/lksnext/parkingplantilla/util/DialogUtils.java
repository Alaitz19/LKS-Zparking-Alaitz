package com.lksnext.parkingplantilla.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.lksnext.parkingplantilla.R;

import java.util.List;
import java.util.Locale;

public class DialogUtils {

    public static void setupHoraButtons(Context context, GridLayout gridHoras, List<MaterialButton> horasSeleccionadas) {
        int columnas = 4;
        gridHoras.setColumnCount(columnas);

        int inicioMinutos = 8 * 60;
        int finMinutos = 21 * 60;
        int intervalo = 30;

        for (int minutos = inicioMinutos; minutos <= finMinutos; minutos += intervalo) {
            int horas = minutos / 60;
            int mins = minutos % 60;
            String texto = String.format(Locale.getDefault(), "%02d:%02d", horas, mins);

            MaterialButton btn = new MaterialButton(context, null);

            btn.setText(texto);
            btn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            btn.setGravity(Gravity.CENTER);
            btn.setTextSize(12);
            btn.setAllCaps(false);
            btn.setCheckable(true);
            btn.setChecked(false);
            btn.setSingleLine(true);

            // Borde, radio y color base
            btn.setStrokeWidth(2);
            btn.setStrokeColor(ColorStateList.valueOf(Color.GRAY));
            btn.setCornerRadius(16);

            btn.setBackgroundColor(Color.WHITE);
            btn.setTextColor(Color.BLACK);

            // Cambia apariencia según estado checked
            btn.addOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    btn.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    btn.setTextColor(Color.WHITE);
                } else {
                    btn.setBackgroundColor(Color.WHITE);
                    btn.setTextColor(Color.BLACK);
                }
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                if (horasSeleccionadas.contains(btn)) {
                    btn.setChecked(false);
                    horasSeleccionadas.remove(btn);
                } else {
                    if (horasSeleccionadas.size() < 7) {
                        btn.setChecked(true);
                        horasSeleccionadas.add(btn);
                    } else {
                        Toast.makeText(context, context.getString(R.string.max_hours_limit_message), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            gridHoras.addView(btn);
        }
    }
}
