package com.h.test.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.h.test.ChangeUserInfoActivity;
import com.h.test.R;
import com.h.test.models.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class UserInformationFragment extends Fragment {

    private TextView tvFullNameInfo, tvDateOfBirthInfo, tvPhoneNumberInfo, tvSexInfo, tvEmailInfo;
    private ImageView ivChangeUserInfo;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private UserInformation userInformation;

    public UserInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_information, container, false);

        tvFullNameInfo = (TextView) view.findViewById(R.id.tvFullNameInfo);
        tvEmailInfo = (TextView) view.findViewById(R.id.tvEmailInfo);
        tvSexInfo = (TextView) view.findViewById(R.id.tvSexInfo);
        tvDateOfBirthInfo = (TextView) view.findViewById(R.id.tvDateOfBirthInfo);
        tvPhoneNumberInfo = (TextView) view.findViewById(R.id.tvPhoneNumberInfo);
        ivChangeUserInfo = view.findViewById(R.id.ivChangeUserInfo);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null)
            db.collection("Users").document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null && value.exists()) {
                                userInformation = value.toObject(UserInformation.class);

                                if (!Objects.requireNonNull(userInformation).getLastName().equals("")) {
                                    tvFullNameInfo.setText(userInformation.getLastName() + " " + userInformation.getFirstName());
                                } else {
                                    tvFullNameInfo.setText(userInformation.getFirstName());
                                }
                                tvEmailInfo.setText(userInformation.getEmail());
                                tvSexInfo.setText(userInformation.getSex());
                                tvDateOfBirthInfo.setText(new SimpleDateFormat("dd/MM/yyyy").format(userInformation.getDateOfBirth()));
                                tvPhoneNumberInfo.setText(userInformation.getPhone());

                                ivChangeUserInfo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(getActivity(), ChangeUserInfoActivity.class).putExtra("userInformation", userInformation));
                                    }
                                });
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        }
                    });


        return view;
    }

}