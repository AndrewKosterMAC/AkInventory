package com.webfactional.andrewk.akinventory;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsViewHolder extends RecyclerView.ViewHolder
{
    View view;

    @BindView(R.id.nameDisplay) TextView nameDisplay;

    public void setName(String value)
    {
        nameDisplay.setText(value);
    }

    @BindView(R.id.descriptionDisplay) TextView descriptionDisplay;

    public void setDescription(String value)
    {
        descriptionDisplay.setText(value);
    }

    @BindView(R.id.imageDisplay) ImageView imageDisplay;

    public void setIsExpanded(boolean value)
    {
        isExpanded = value;

        if (isExpanded)
        {
            int sizeDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, view.getResources().getDisplayMetrics());

            imageDisplay.getLayoutParams().height = sizeDp;
            imageDisplay.getLayoutParams().width = sizeDp;
            imageDisplay.requestLayout();

            expandedSection.setVisibility(View.VISIBLE);
        }
        else
        {
            int sizeDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, view.getResources().getDisplayMetrics());

            imageDisplay.getLayoutParams().height = sizeDp;
            imageDisplay.getLayoutParams().width = sizeDp;
            imageDisplay.requestLayout();

            expandedSection.setVisibility(View.GONE);
        }
    }

    boolean isExpanded;

    @BindView(R.id.expandedSection) LinearLayout expandedSection;

    @BindDrawable(R.drawable.box) Drawable boxDrawable;

    public void setImageUri(String value)
    {
        if (null == value || value.length() < 1)
        {
            imageDisplay.setImageDrawable(boxDrawable);
        }
        else  if (value.startsWith("http://") || value.startsWith("https://"))
        {
            Glide.with(view.getContext()).load(Uri.parse(value)).into(imageDisplay);
        }
        else
        {
            Glide.with(view.getContext()).load(Uri.parse(value)).listener(new RequestListener<Uri, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource)
                {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                {
                    return false;
                }
            }).into(imageDisplay);
        }
    }

    @BindView(R.id.locationDisplay) TextView locationDisplay;

    public void setLocation(LocationSerializable location)
    {
        locationDisplay.setText(
            null == location
                ? ""
                : "Latitude: " + String.format("%.2f", location.getLatitude()) + ", " +
                    "Longitude: " + String.format("%.2f", location.getLongitude()));
    }

    public ItemsViewHolder(View itemView)
    {
        super(itemView);

        view = itemView;

        view.setTag(this);

        ButterKnife.bind(this, view);

        setIsExpanded(false);
        setImageUri(null);
    }
}