package com.example.mediaplayer.ui;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.mediaplayer.PlantWateringService;
import com.example.mediaplayer.R;
import com.example.mediaplayer.provider.PlantContract;
import com.example.mediaplayer.utils.PlantUtils;

import  static com.example.mediaplayer.provider.PlantContract.BASE_CONTENT_URI;
import  static com.example.mediaplayer.provider.PlantContract.PATH_PLANTS;


public class PlantDetailActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int SINGLE_LOADER_ID = 200;
    public static final String EXTRA_PLANT_ID = "com.example.android.mygarden.extra.PLANT_ID";
    long mPlantId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitty_plant_detail);
        mPlantId = getIntent().getLongExtra(EXTRA_PLANT_ID, PlantContract.INVALID_PLANT_ID);
        // This activity displays single plant information that is loaded using a cursor loader
          getSupportLoaderManager().initLoader(SINGLE_LOADER_ID, null, this);

    }

    public void onBackButtonClick(View view) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    public void onWaterButtonClick(View view) {
        //check if already dead then can't water
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), mPlantId);
        Cursor cursor = getContentResolver().query(SINGLE_PLANT_URI, null, null, null, null);
        if (cursor == null || cursor.getCount() < 1) return; //can't find this plant!
        cursor.moveToFirst();
        long lastWatered = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
        long timeNow = System.currentTimeMillis();
        if ((timeNow - lastWatered) > PlantUtils.MAX_AGE_WITHOUT_WATER)
            return; // plant already dead

        ContentValues contentValues = new ContentValues();
        // Update the watered timestamp
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().update(SINGLE_PLANT_URI, contentValues, null, null);
        cursor.close();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), mPlantId);
        return new CursorLoader(this, SINGLE_PLANT_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) return;
        cursor.moveToFirst();
        int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int planTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        int plantType = cursor.getInt(planTypeIndex);
        long createdAt = cursor.getLong(createTimeIndex);
        long wateredAt = cursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();

        int plantImgRes = PlantUtils.getPlantImageRes(this, timeNow - createdAt, timeNow - wateredAt, plantType);

        ((ImageView) findViewById(R.id.plant_detail_image)).setImageResource(plantImgRes);
        ((TextView) findViewById(R.id.plant_detail_name)).setText(String.valueOf(mPlantId));
        ((TextView) findViewById(R.id.plant_age_number)).setText(
                String.valueOf(PlantUtils.getDisplayAgeInt(timeNow - createdAt))
        );
        ((TextView) findViewById(R.id.plant_age_unit)).setText(
                PlantUtils.getDisplayAgeUnit(this, timeNow - createdAt)
        );
        ((TextView) findViewById(R.id.last_watered_number)).setText(
                String.valueOf(PlantUtils.getDisplayAgeInt(timeNow - wateredAt))
        );
        ((TextView) findViewById(R.id.last_watered_unit)).setText(
                PlantUtils.getDisplayAgeUnit(this, timeNow - wateredAt)
        );
        int waterPercent = 100 - ((int) (100 * (timeNow - wateredAt) / PlantUtils.MAX_AGE_WITHOUT_WATER));
        ((WaterLevelView) findViewById(R.id.water_level)).setValue(waterPercent);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public void onCutButtonClick(View view) {
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), mPlantId);
        getContentResolver().delete(SINGLE_PLANT_URI, null, null);
        PlantWateringService.startActionUpdatePlantWidgets(this);
        Toast.makeText(this, "Plant is Deleted ", Toast.LENGTH_SHORT).show();
    }


}
