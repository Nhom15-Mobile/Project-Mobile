package com.example.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appointment.ui.AppointmentActivity;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        //Listener btnAppointment
        MaterialButton btnAppointment = view.findViewById(R.id.btnAppointment);
        btnAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AppointmentActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
