package com.h.test.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.h.test.ManageProductActivity;
import com.h.test.R;
import com.h.test.models.Product;
import com.bumptech.glide.Glide;

import java.util.List;

public class SimpleGalleryRecyclerAdapter extends RecyclerView.Adapter<SimpleGalleryRecyclerAdapter.ImageViewHolder> {
    private List<Uri> listUri;
    private Context context;
    private Product product;

    public SimpleGalleryRecyclerAdapter(List<Uri> listUri, Context context) {
        this.listUri = listUri;
        this.context = context;
    }

    public SimpleGalleryRecyclerAdapter(List<Uri> listUri, Product product, Context context) {
        this.listUri = listUri;
        this.context = context;
        this.product = product;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_simplegallery_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = listUri.get(position);
        if (uri == null) {
            return;
        }
        setValueOfEachItem(holder, uri);

        //set event
        holder.imageViewSimpleGallery.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm")
                    .setMessage("Confirm deletion of images")
                    .setCancelable(false)
                    .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ManageProductActivity activity = (ManageProductActivity) context;
                            activity.removeImage(holder.getBindingAdapterPosition(), product);
                            notifyDataSetChanged();
                        }
                    }).setNegativeButton("Refuse", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        });
    }

    private void setValueOfEachItem(ImageViewHolder holder, Uri uri) {
//        holder.imageViewSimpleGallery.setImageURI(uri);
        Glide.with(context)
                .load(uri).into(holder.imageViewSimpleGallery);


    }

    @Override
    public int getItemCount() {
        if (listUri != null)
            return listUri.size();
        return 0;
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewSimpleGallery;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            //init
            imageViewSimpleGallery = (ImageView) itemView.findViewById(R.id.imageViewSimpleGallery);

        }
    }
}
