package com.h.test.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.h.test.fragments.CancelOrderFragment;
import com.h.test.fragments.ReceiveOrderFragment;
import com.h.test.fragments.ShippingFragment;
import com.h.test.fragments.WaitForProductFragment;
import com.h.test.fragments.WaitForReviewFragment;

public class PurchaseOrderAdapter extends FragmentStateAdapter {

    public PurchaseOrderAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new ReceiveOrderFragment();
            case 1:
                return new WaitForProductFragment();
            case 2:
                return new ShippingFragment();
            case 3:
                return new WaitForReviewFragment();
            case 4:
                return new CancelOrderFragment();
            default:
                return new ReceiveOrderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
