package com.example.snehapantam.runtime;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;

import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IALocationListenerSupport;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

public class CommonActivity extends FragmentActivity implements OnMapReadyCallback{

    private static final String TAG = "MapsOverLay";



    /* used to decide when bitmap should be downscaled */
    private static final int MAX_DIMENSION = 2048;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mMarker;



    private IARegion mOverlayFloorPlan = null;
    private GroundOverlay mGroundOverlay = null;
    private IALocationManager mIALocationManager;
    private IAResourceManager mResourceManager;
    private IATask<IAFloorPlan> mFetchFloorPlanTask;
    private Target mLoadTarget;
    private boolean mCameraPositionNeedsUpdating = true; // update on first location



    /**
     * Listener that handles location change events.
     */
    public IALocationListener mListener = new IALocationListenerSupport() {

        /**
         * Location changed, move marker and camera position.
         */
        @Override
        public void onLocationChanged(final IALocation location) {

            Log.d(TAG, "new location received with coordinates: " + location.getLatitude()
                    + "," + location.getLongitude());



            if (mMap == null) {
                // location received before map is initialized, ignoring update here
                return;
            }

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            if (mMarker == null) {
                // first location, add marker
                mMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon2)));

            } else {
                // move existing markers position to received location
                mMarker.setPosition(latLng);
            }

            // our camera position needs updating if location has significantly changed
            if (mCameraPositionNeedsUpdating) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
                mCameraPositionNeedsUpdating = false;
            }









                }







    };





    /**
     * Listener that changes overlay if needed
     */
    private IARegion.Listener mRegionListener = new IARegion.Listener() {

        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                final String newId = region.getId();
                // Are we entering a new floor plan or coming back the floor plan we just left?
                if (mGroundOverlay == null || !region.equals(mOverlayFloorPlan)) {
                    mCameraPositionNeedsUpdating = true; // entering new fp, need to move camera
                    if (mGroundOverlay != null) {
                        mGroundOverlay.remove();
                        mGroundOverlay = null;
                    }
                    mOverlayFloorPlan = region; // overlay will be this (unless error in loading)
                    fetchFloorPlan(newId);


                } else {
                    mGroundOverlay.setTransparency(0.0f);
                }
            }
            Log.d(TAG, "Enter " + (region.getType() == IARegion.TYPE_VENUE
                    ? "VENUE "
                    : "FLOOR_PLAN ") + region.getId());




        }

        @Override
        public void onExitRegion(IARegion region) {
            if (mGroundOverlay != null) {
                // Indicate we left this floor plan but leave it there for reference
                // If we enter another floor plan, this one will be removed and another one loaded
                mGroundOverlay.setTransparency(0.5f);
            }
            Log.d(TAG, "Enter " + (region.getType() == IARegion.TYPE_VENUE
                    ? "VENUE "
                    : "FLOOR_PLAN ") + region.getId());
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String someVariable = extras.getString("someVariable");


            Log.d(TAG, "value" + someVariable);

            switch (someVariable) {

                case "0":
                    break;

                case "1":
                    Toast.makeText(CommonActivity.this, "Your book Mercury is in the First floor", Toast.LENGTH_LONG).show();
                    Toast.makeText(CommonActivity.this, "Click Mark my Book button after you reach First Floor", Toast.LENGTH_LONG).show();


                    break;


                case "2":
                    Toast.makeText(CommonActivity.this, "Your book Venus is in the Fourth floor", Toast.LENGTH_LONG).show();
                    Toast.makeText(CommonActivity.this, "Click Mark my Book button after you reach Fourth Floor", Toast.LENGTH_LONG).show();


                    break;

                case "3":
                    Toast.makeText(CommonActivity.this, "Your book Earth is in the Third floor", Toast.LENGTH_LONG).show();
                    Toast.makeText(CommonActivity.this, "Click Mark my Book button after you reach Third Floor", Toast.LENGTH_LONG).show();


                    break;

                case "4":
                    Toast.makeText(CommonActivity.this, "Your book Mars is in the Third floor", Toast.LENGTH_LONG).show();
                    Toast.makeText(CommonActivity.this, "Click Mark my Book button after you reach Third Floor", Toast.LENGTH_LONG).show();


                    break;
                case "5":
                    Toast.makeText(CommonActivity.this, "Your book Jupiter is in the Fourth floor", Toast.LENGTH_LONG).show();
                    Toast.makeText(CommonActivity.this, "Click Mark my Book button after you reach Fourth Floor", Toast.LENGTH_LONG).show();


                    break;

            }


            // prevent the screen going to sleep while app is on foreground
            findViewById(android.R.id.content).setKeepScreenOn(false);

            // instantiate IALocationManager and IAResourceManager
            mIALocationManager = IALocationManager.create(this);
            mResourceManager = IAResourceManager.create(this);


            final Button button = (Button) findViewById(R.id.buttoncommon);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(CommonActivity.this);
                    alertdialog.setTitle("Location");

                    switch (someVariable) {

                        case "0":
                            break;

                        case "1":
                            alertdialog.setMessage("Are you in the First floor?");
                            break;


                        case "2":
                            alertdialog.setMessage("Are you in the Fourth floor?");
                            break;

                        case "3":
                            alertdialog.setMessage("Are you in the Third floor?");
                            break;

                        case "4":
                            alertdialog.setMessage("Are you in the Third floor?");
                            break;
                        case "5":
                            alertdialog.setMessage("Are you in the Fourth floor?");
                            break;

                    }

                    alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final Intent intent;

                            switch (someVariable) {

                                case "0":
                                    break;

                                case "1":
                                    intent = new Intent(CommonActivity.this, MapsOverlayActivity.class);

                                    startActivity(intent);
                                    break;


                                case "2":
                                    intent = new Intent(CommonActivity.this, VenusActivity.class);
                                    startActivity(intent);
                                    break;

                                case "3":
                                    intent = new Intent(CommonActivity.this, Earth.class);
                                    startActivity(intent);
                                    break;

                                case "4":
                                    intent = new Intent(CommonActivity.this, MarsActivity2.class);
                                    startActivity(intent);
                                    break;
                                case "5":
                                    intent = new Intent(CommonActivity.this, JupiterActivity.class);
                                    startActivity(intent);
                                    break;

                            }




                        }
                    });
                    alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (someVariable) {

                                case "0":
                                    break;

                                case "1":
                                    Toast.makeText(CommonActivity.this, "Please go to the First floor", Toast.LENGTH_LONG).show();


                                    break;


                                case "2":
                                    Toast.makeText(CommonActivity.this, "Please go to the Fourth floor", Toast.LENGTH_LONG).show();


                                    break;

                                case "3":
                                    Toast.makeText(CommonActivity.this, "Please go to the Third floor", Toast.LENGTH_LONG).show();


                                    break;

                                case "4":
                                    Toast.makeText(CommonActivity.this, "Please go to the Third floor", Toast.LENGTH_LONG).show();


                                    break;
                                case "5":
                                    Toast.makeText(CommonActivity.this, "Please go to the Fourth floor", Toast.LENGTH_LONG).show();


                                    break;

                            }





                        }
                    });
                    AlertDialog alert = alertdialog.create();
                    alert.show();


                }
            });


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // remember to clean up after ourselves
        mIALocationManager.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(this);

        }

        // start receiving location updates & monitor region changes
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mListener);
        mIALocationManager.registerRegionListener(mRegionListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister location & region changes
        mIALocationManager.removeLocationUpdates(mListener);
        mIALocationManager.registerRegionListener(mRegionListener);
    }


    /**
     * Sets bitmap of floor plan as ground overlay on Google Maps
     */
    private void setupGroundOverlay(IAFloorPlan floorPlan, Bitmap bitmap) {

        if (mGroundOverlay != null) {
            mGroundOverlay.remove();
        }

        if (mMap != null) {
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            IALatLng iaLatLng = floorPlan.getCenter();
            LatLng center = new LatLng(iaLatLng.latitude, iaLatLng.longitude);
            GroundOverlayOptions fpOverlay = new GroundOverlayOptions()
                    .image(bitmapDescriptor)
                    .position(center, floorPlan.getWidthMeters(), floorPlan.getHeightMeters())
                    .bearing(floorPlan.getBearing());

            mGroundOverlay = mMap.addGroundOverlay(fpOverlay);
        }
    }

    /**
     * Download floor plan using Picasso library.
     */
    private void fetchFloorPlanBitmap(final IAFloorPlan floorPlan) {

        final String url = floorPlan.getUrl();

        if (mLoadTarget == null) {
            mLoadTarget = new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Log.d(TAG, "onBitmap loaded with dimensions: " + bitmap.getWidth() + "x"
                            + bitmap.getHeight());
                    setupGroundOverlay(floorPlan, bitmap);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    // N/A
                }

                @Override
                public void onBitmapFailed(Drawable placeHolderDraweble) {
                    Log.d(TAG, "Failed to load bitmap");
                    mOverlayFloorPlan = null;
                }
            };
        }

        RequestCreator request = Picasso.with(this).load(url);

        final int bitmapWidth = floorPlan.getBitmapWidth();
        final int bitmapHeight = floorPlan.getBitmapHeight();

        if (bitmapHeight > MAX_DIMENSION) {
            request.resize(0, MAX_DIMENSION);
        } else if (bitmapWidth > MAX_DIMENSION) {
            request.resize(MAX_DIMENSION, 0);
        }

        request.into(mLoadTarget);
    }


    /**
     * Fetches floor plan data from IndoorAtlas server.
     */
    private void fetchFloorPlan(String id) {

        // if there is already running task, cancel it
        cancelPendingNetworkCalls();

        final IATask<IAFloorPlan> task = mResourceManager.fetchFloorPlanWithId(id);

        task.setCallback(new IAResultCallback<IAFloorPlan>() {

            @Override
            public void onResult(IAResult<IAFloorPlan> result) {

                if (result.isSuccess() && result.getResult() != null) {
                    // retrieve bitmap for this floor plan metadata
                    fetchFloorPlanBitmap(result.getResult());
                } else {
                    // ignore errors if this task was already canceled
                    if (!task.isCancelled()) {
                        // do something with error
                        Log.d(TAG, "Loading floor plan failed: " + result.getError());
                        mOverlayFloorPlan = null;
                    }
                }
            }
        }, Looper.getMainLooper()); // deliver callbacks using main looper

        // keep reference to task so that it can be canceled if needed
        mFetchFloorPlanTask = task;

    }

    /**
     * Helper method to cancel current task if any.
     */
    private void cancelPendingNetworkCalls() {
        if (mFetchFloorPlanTask != null && !mFetchFloorPlanTask.isCancelled()) {
            mFetchFloorPlanTask.cancel();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }










}


















