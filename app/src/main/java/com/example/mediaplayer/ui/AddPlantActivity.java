package com.example.mediaplayer.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayer.PlantWateringService;
import com.example.mediaplayer.R;
import com.example.mediaplayer.provider.PlantContract;

public class AddPlantActivity extends AppCompatActivity {

    private RecyclerView mTypesRecyclerView;
    private PlantTypesAdapter mTypesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        // Plant types are displayed as a recycler view using PlantTypesAdapter
        mTypesAdapter = new PlantTypesAdapter(this);
        mTypesRecyclerView = (RecyclerView) findViewById(R.id.plant_types_recycler_view);
        mTypesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        mTypesRecyclerView.setAdapter(mTypesAdapter);

    }
    /**
     * Event handler to handle clicking on a plant type
     *
     * @param view
     */
    public void onPlantTypeClick(View view) {
        // When the chosen plant type is clicked, create a new plant and set the creation time and
        // water time to now
        // Extract the plant type from the tag
        ImageView imgView = (ImageView) view.findViewById(R.id.plant_type_image);
        int plantType = (int) imgView.getTag();
        long timeNow = System.currentTimeMillis();
        // Insert the new plant into DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlantContract.PlantEntry.COLUMN_PLANT_TYPE, plantType);
        contentValues.put(PlantContract.PlantEntry.COLUMN_CREATION_TIME, timeNow);
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().insert(PlantContract.PlantEntry.CONTENT_URI, contentValues);
        PlantWateringService.startActionUpdatePlantWidgets(this);
        // Close this activity
        if (plantType==0){
            Toast.makeText(this, "Vine plant is added", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this, "Cactus plant is added", Toast.LENGTH_SHORT).show();

        }

    }

    public void onBackButtonClick(View view) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}
