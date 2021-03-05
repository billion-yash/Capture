package com.example.capture.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capture.DownloadImage.DownloadImageBitmap;
import com.example.capture.DownloadImage.Permission;
import com.example.capture.MainActivity;
import com.example.capture.R;
import com.example.capture.Services.GetStorageFileNames;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xyz.belvi.blurhash.BlurHashDecoder;

import static com.example.capture.DownloadImage.Permission.checkPermission;
import static com.example.capture.ui.home.HomeFragment.TotalPages;
import static com.example.capture.ui.home.HomeFragment.currentPageNumber;

public class ImageAdapter extends  RecyclerView.Adapter<ImageAdapter.ImageAdapterHolder> {
    JSONArray jsonArray;
    Context mContext;

    public ImageAdapter(JSONArray jsonArray){
        this.jsonArray = jsonArray;

    }
    @NonNull
    @Override
    public ImageAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        mContext = parent.getContext();
        return new ImageAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapterHolder holder, int position) {
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonArray.get(position);
            JSONObject jsonObject1 = jsonObject.getJSONObject("urls");
            holder.textView.setText(jsonObject
                    .getString("alt_description"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && GetStorageFileNames.DownloadedFilesName.contains(jsonObject.getString("id")+ ".jpg")) {
                holder.downloadButton.setForeground(ContextCompat.getDrawable(mContext , R.drawable.ic_baseline_done_24));
            }

            Bitmap bitmap = (Bitmap) BlurHashDecoder
                    .INSTANCE.decode(jsonObject
                    .getString("blur_hash"), 20, 12 ,1.0f , false , null );
            Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);

            Picasso.get()
                    .load(jsonObject1.getString("small"))
                    .placeholder(d)
                    .into(holder.imageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ImageAdapterHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        Context mcontext;
        Button downloadButton;
        TextView textView ;
        public ImageAdapterHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewForItem);
            mcontext = itemView.getContext();
            downloadButton = itemView.findViewById(R.id.downloadButton);
            textView = itemView.findViewById(R.id.imageTitleItem);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkPermission(mcontext , Manifest.permission.WRITE_EXTERNAL_STORAGE )){
                        try {
                            JSONObject jsonObject = null;
                            jsonObject = (JSONObject) jsonArray.get(getAdapterPosition());
                            JSONObject jsonObject1 = jsonObject.getJSONObject("urls");
                            new DownloadImageBitmap(jsonObject1.getString("full"), jsonObject.getString("id") , mcontext);
                        }catch (Exception e){

                        }
                    }
                }
            });
        }
    }
}
