package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.search.SearchBar;
import com.google.android.material.textview.MaterialTextView;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.view.activity.MainActivity;


public class MainFragment extends Fragment {
    private TextView welcomeText;
    private SearchBar searchBar;

    public MainFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        TextView tvWelcomeUser = view.findViewById(R.id.tvWelcomeUser);

        // Obtiene el nombre del usuario de la actividad
        if (getActivity() instanceof MainActivity) {
            String userName = ((MainActivity) getActivity()).getUserName();
            if (userName != null && !userName.isEmpty()) {
                tvWelcomeUser.setText("Welcome " + userName + "!");
            }
        }

        return view;
    }
}
