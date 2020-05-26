package com.sizii.homty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sizii.homty.Model.Photos;
import com.sizii.homty.Model.PlaceDetail;
import com.sizii.homty.Remote.IGoogleApiService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlaceActivity extends AppCompatActivity {

    ImageView photo;
    RatingBar ratingBar;
    TextView opening_hours, place_address, place_name;
    Button menu;

    IGoogleApiService service;

    PlaceDetail placeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        service = Common.getGoogleApiService();

        photo = (ImageView) findViewById(R.id.photo);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        place_address = (TextView) findViewById(R.id.place_address);
        place_name = (TextView) findViewById(R.id.place_name);
        opening_hours = (TextView) findViewById(R.id.place_open_hour);
        menu = (Button) findViewById(R.id.menu_btn);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewPlaceActivity.this, MenuActivity.class));
            }
        });

        place_name.setText("");
        place_address.setText("");
        opening_hours.setText("");

        if (Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length > 0) {
            Picasso.with(this).load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(), 1000))
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_error_black_24dp)
                    .into(photo);
        }

        if (Common.currentResult.getRating() != null && !TextUtils.isEmpty(Common.currentResult.getRating())) {
            ratingBar.setRating(Float.parseFloat(Common.currentResult.getRating()));
        } else {
            ratingBar.setVisibility(View.GONE);
        }

        if (Common.currentResult.getOpening_hours() != null) {
            opening_hours.setText(getString(R.string.open_now) + Common.currentResult.getOpening_hours().getOpen_now());
        } else {
            opening_hours.setVisibility(View.GONE);
        }

        if (Common.currentResult.getName() != null) {
            place_name.setText(Common.currentResult.getName());
        } else {
            place_name.setVisibility(View.GONE);
        }

        if (Common.currentResult.getFormatted_address() != null) {
            place_address.setText(Common.currentResult.getFormatted_address());
        } else {
            place_address.setVisibility(View.GONE);
        }

        service.getDetailPlaces(getPlaceDetailUrl(Common.currentResult.getPlace_id())).enqueue(new Callback<PlaceDetail>() {
            @Override
            public void onResponse(Call<PlaceDetail> call, Response<PlaceDetail> response) {
                placeDetail = response.body();
            }

            @Override
            public void onFailure(Call<PlaceDetail> call, Throwable t) {

            }
        });
    }

    private String getPlaceDetailUrl(String place_id) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
        url.append("?placeid=" + place_id);
        url.append("&fields=name,formatted_address,rating,photo,opening_hours");
        url.append("&key=" + getResources().getString(R.string.places_key));
        return url.toString();
    }

    private String getPhotoOfPlace(String photo_reference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth=" + maxWidth);
        url.append("&photoreference=" + photo_reference);
        url.append("&key=" + getResources().getString(R.string.places_key));
        return url.toString();
    }
}
