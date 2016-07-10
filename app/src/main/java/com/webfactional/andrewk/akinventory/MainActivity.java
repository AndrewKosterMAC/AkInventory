package com.webfactional.andrewk.akinventory;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{
    @BindView(R.id.itemsList) RecyclerView itemsList;

    FirebaseRecyclerAdapter<InventoryItem, ItemsViewHolder> itemsListAdapter;

    @BindView(R.id.itemNameEditor) EditText itemNameEditor;

    @BindView(R.id.itemDescriptionEditor) EditText itemDescriptionEditor;

    int expandedPosition = -1;

    LocationService locationService;

    GoogleApiClient googleApiClientForLocationService;

    public GoogleApiClient getGoogleApiClientForLocationService()
    {
        return googleApiClientForLocationService;
    }

    private String[] getSelectedProviders()
    {
        ArrayList<String> result = new ArrayList<>();

        result.add(AuthUI.EMAIL_PROVIDER);

        result.add(AuthUI.GOOGLE_PROVIDER);

        return result.toArray(new String[result.size()]);
    }

    private static final int RESULT_CODE_SIGN_IN = 666;

    private static final String FIREBASE_TOS_URL = "https://www.firebase.com/terms/terms-of-service.html";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        /*
        if (null == FirebaseAuth.getInstance().getCurrentUser())
        {
            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setTheme(R.style.AppTheme)
                            .setProviders(getSelectedProviders())
                            .setTosUrl(FIREBASE_TOS_URL)
                            .build(),
                    RESULT_CODE_SIGN_IN);
        }
        else
        {
            Toast.makeText(this, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
        }
        */

        itemsList.setLayoutManager(new LinearLayoutManager(this));

        itemsList.setItemAnimator(new DefaultItemAnimator());

        itemsList.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        itemsListAdapter = new FirebaseRecyclerAdapter<InventoryItem, ItemsViewHolder>(
                InventoryItem.class,
                R.layout.inventory_item,
                ItemsViewHolder.class,
                InventoryItemManager.getItemsTable()) {
            
            @Override
            protected void populateViewHolder(ItemsViewHolder viewHolder, InventoryItem model, int position)
            {
                if (null == model.getKey())
                {
                    model.setKey(itemsListAdapter.getRef(position).getKey());
                    InventoryItemManager.getItemsTable().child(model.getKey()).setValue(model);
                }
                viewHolder.setName(model.getName());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setImageUri(model.getImageUri());
                viewHolder.setLocation(model.getLocation());

                if (position == expandedPosition)
                {
                    viewHolder.setIsExpanded(true);
                }
                else
                {
                    viewHolder.setIsExpanded(false);
                }
            }
        };
        itemsList.setAdapter(itemsListAdapter);

        itemsList.addOnItemTouchListener(new RecyclerTouchListener(this, itemsList, new RecyclerClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                if (expandedPosition > -1)
                {
                    itemsListAdapter.notifyItemChanged(expandedPosition);
                }

                ItemsViewHolder viewHolder = (ItemsViewHolder)view.getTag();
                viewHolder.setIsExpanded(true);
                expandedPosition = position;
            }

            @Override
            public void onLongClick(View view, int position)
            {

            }
        }));

        locationService = new LocationService(this);

        googleApiClientForLocationService = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(locationService)
                .addOnConnectionFailedListener(locationService)
                .build();
        googleApiClientForLocationService.connect();
    }

    public void addItemButtonOnClick(View view)
    {
        if (itemNameEditor.getText().toString().length() > 0)
        {
            Intent intent = new Intent("com.webfactional.andrewk.akinventory.ADD_ITEM");
            intent.putExtra("itemName", itemNameEditor.getText().toString());
            intent.putExtra("itemDescription", itemDescriptionEditor.getText().toString());

            sendBroadcast(intent);
        }
    }

    public void pickImageButtonOnClick(View view)
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 998);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 998:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    InventoryItem item = itemsListAdapter.getItem(expandedPosition);
                    item.setImageUri(Uri.fromFile(new File(filePath)).toString());
                    itemsListAdapter.notifyItemChanged(expandedPosition);
                    InventoryItemManager.getItemsTable().child(item.getKey()).setValue(item);
                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
            {
                if (resultCode == RESULT_OK)
                {
                    InventoryItem item = itemsListAdapter.getItem(expandedPosition);
                    item.setImageUri(imageFileUri.toString());
                    itemsListAdapter.notifyItemChanged(expandedPosition);
                    InventoryItemManager.getItemsTable().child(item.getKey()).setValue(item);
                }
                else if (resultCode == RESULT_CANCELED)
                {
                    // User cancelled the image capture
                }
                else
                {
                    // Image capture failed, advise user
                }
            }
            case RESULT_CODE_SIGN_IN:
            {

            }
        }
    }

    public static final int REQUEST_CODE_TAKE_PICTURE = 7267;

    public static final int MEDIA_TYPE_IMAGE = 1;

    public static final int MEDIA_TYPE_VIDEO = 2;

    Uri imageFileUri = null;

    public void addImageButtonOnClick(View view)
    {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        imageFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);

        startActivityForResult(i, REQUEST_CODE_TAKE_PICTURE);
    }

    private static Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type)
    {
        Log.d("AK", "Environment.getExternalStorageState(): " + Environment.getExternalStorageState());

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            throw new RuntimeException("No card is mounted!");
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void uploadImageButtonOnClick(View view)
    {
        locationService.updateLocation();

        View expandedPositionView = itemsList.getLayoutManager().findViewByPosition(expandedPosition);

        if (null != expandedPositionView)
        {
            ImageView imageView = (ImageView) expandedPositionView.findViewById(R.id.imageDisplay);

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageReference = storage.getReferenceFromUrl("gs://akinventory-3838c.appspot.com");

            final InventoryItem item = itemsListAdapter.getItem(expandedPosition);

            StorageReference child = storageReference.child("inventoryItem-" + item.getKey() + "-picture.jpg");
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = imageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = child.putBytes(data);
            uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    item.setImageUri(taskSnapshot.getDownloadUrl().toString());

                    Log.e("AK", "locationService.getLocationFromGps(): " + locationService.getLocationFromGps());
                    Log.e("AK", "locationService.getLocationFromFused(): " + locationService.getLocationFromFused());
                    Log.e("AK", "locationService.getLocationFromNetwork(): " + locationService.getLocationFromNetwork());
                    Log.e("AK", "locationService.getLocationFromPassive(): " + locationService.getLocationFromPassive());

                    item.setLocation(new LocationSerializable(locationService.getLocationFromFused()));
                    itemsListAdapter.notifyItemChanged(expandedPosition);
                    InventoryItemManager.getItemsTable().child(item.getKey()).setValue(item);
                }
            });
        }
    }

    public void saveItemToExternalServerButton(View view)
    {
        View expandedPositionView = itemsList.getLayoutManager().findViewByPosition(expandedPosition);

        if (null != expandedPositionView)
        {
            final InventoryItem item = itemsListAdapter.getItem(expandedPosition);

            Intent intent = new Intent(this, ItemUploadIntentService.class);
            intent.setAction(ItemUploadIntentService.ACTION_POST_TO_ANDREWK);
            intent.putExtra(ItemUploadIntentService.PARAM_URL_PATH, "/inventory.php");
            intent.putExtra(ItemUploadIntentService.PARAM_ITEM, item);

            startService(intent);
        }
    }

    public void editButtonOnClick(View view)
    {
    }
}
