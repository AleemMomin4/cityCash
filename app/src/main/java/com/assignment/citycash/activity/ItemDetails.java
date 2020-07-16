package com.assignment.citycash.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.assignment.citycash.R;
import com.assignment.citycash.constant.Constant;
import com.assignment.citycash.constant.NetworkConstant;
import com.assignment.citycash.model.ItemModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ItemDetails extends AppCompatActivity {

    private String productId;
    private TextView productName,categoryName,price,size;
    private ImageView imageView;
    private RelativeLayout parentLayout;
    private Context context;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        context = this;

        parentLayout = findViewById(R.id.parent_layout);
        imageView = findViewById(R.id.img);
        productName = findViewById(R.id.item_name);
        categoryName = findViewById(R.id.category_name);
        price = findViewById(R.id.price);
        size = findViewById(R.id.size);
        progressBar = findViewById(R.id.progressbar);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
            }
        });

        productId = getIntent().getStringExtra(Constant.ID);

        getProductDetailsAPI();
    }

    private void getProductDetailsAPI(){
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.get(NetworkConstant.BASE_URL + NetworkConstant.PRODUCT_DETAIL_ITEM_END_POINT+productId+"?format=json")
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        parentLayout.setVisibility(View.VISIBLE);
                        //Log.e("json-res",response.toString());
                        try {
                            productName.setText(response.getString(Constant.NAME));
                            categoryName.setText(response.getString(Constant.CATEGORY_NAME));
                            price.setText("₹ ".concat(response.getString(Constant.PRICE)));
                            size.setText(response.getString(Constant.SIZE));

                            RequestOptions options = new RequestOptions()
                                    .placeholder(R.drawable.ic_gallery)
                                    .error(R.drawable.ic_gallery)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL);

                            Glide.with(context).load(response.getString(Constant.IMAGE))
                                    .apply(options)
                                    .into(imageView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressBar.setVisibility(View.GONE);
                        parentLayout.setVisibility(View.GONE);
                        Toast.makeText(ItemDetails.this,error.getErrorBody(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }
}