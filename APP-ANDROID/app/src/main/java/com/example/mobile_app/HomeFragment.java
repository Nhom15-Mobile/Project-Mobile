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
import com.example.records.ui.AppointmentHistoryActivity;
import com.example.records.ui.RecordsActivity;
import com.example.results.ui.ChooseResultActivity;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {
    MaterialButton btnContact;

    MaterialButton btnAppointment, btnRecords, btnBookingHistory,btnAppointmentResult;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        //Listener btnAppointment
        btnAppointment = view.findViewById(R.id.btnAppointment);
        btnRecords = view.findViewById(R.id.btnRecord);

        btnAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AppointmentActivity.class);
            startActivity(intent);
        });

        btnContact = view.findViewById(R.id.btnContact);
        btnContact.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ContactActivity.class);
            startActivity(intent);
        });

        btnRecords.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecordsActivity.class);
            startActivity(intent);
        });

        btnBookingHistory = view.findViewById(R.id.btnBookingHistory);
        btnBookingHistory.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), AppointmentHistoryActivity.class);
            startActivity(intent);
        });

        btnAppointmentResult = view.findViewById(R.id.btnAppointmentResult);
        btnAppointmentResult.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), ChooseResultActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
