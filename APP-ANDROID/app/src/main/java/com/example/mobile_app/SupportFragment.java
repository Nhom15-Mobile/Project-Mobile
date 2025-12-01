package com.example.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;



public class SupportFragment extends Fragment {


    CardView how_to_book,how_to_cancel ;

    public SupportFragment() {super(R.layout.support_fragment);}

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        //quy trình đặt khám
        how_to_book = v.findViewById(R.id.how_to_book);
        how_to_book.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), BookingActivity.class);
            startActivity(intent);
        });


        // cách huỷ
        how_to_cancel = v.findViewById(R.id.how_to_cancel);
        how_to_cancel.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), CancelActivity.class);
            startActivity(intent);
        });


    }
    
}
