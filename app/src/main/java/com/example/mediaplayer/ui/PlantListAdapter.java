package com.example.mediaplayer.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayer.R;
import com.example.mediaplayer.provider.PlantContract;
import com.example.mediaplayer.utils.PlantUtils;

public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.PlantViewHolder> {
    private Context context;
    private Cursor cursor;

    public PlantListAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public PlantListAdapter.PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.plant_list_item,parent,false);


        return new PlantViewHolder(view);
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (cursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PlantListAdapter.PlantViewHolder holder, int position) {
        cursor.moveToPosition(position);
        int idIndex = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        long plantId = cursor.getLong(idIndex);
        int plantType = cursor.getInt(plantTypeIndex);
        long createdAt = cursor.getLong(createTimeIndex);
        long wateredAt = cursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();

        int imgRes = PlantUtils.getPlantImageRes(context, timeNow - createdAt, timeNow - wateredAt, plantType);

        holder.plantImageView.setImageResource(imgRes);
        holder.plantNameView.setText(String.valueOf(plantId));
        holder.plantImageView.setTag(plantId);
    }

    @Override
    public int getItemCount() {
       if (cursor==null)return 0;
       return cursor.getCount();
    }

    public class PlantViewHolder extends RecyclerView.ViewHolder {
        ImageView plantImageView;
        TextView plantNameView;
        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            plantImageView = (ImageView) itemView.findViewById(R.id.plant_list_item_image);
            plantNameView = (TextView) itemView.findViewById(R.id.plant_list_item_name);
        }
    }
}
