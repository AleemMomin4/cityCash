package com.assignment.citycash.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.assignment.citycash.constant.Constant;
import com.assignment.citycash.constant.NetworkConstant;
import com.assignment.citycash.R;
import com.assignment.citycash.adapter.ItemListAdapter;
import com.assignment.citycash.model.ItemModel;
import com.assignment.citycash.model.SortPropsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemListAdapter adapter;
    private List<ItemModel> listArray = new ArrayList<>();
    private List<ItemModel> sortedListArray = new ArrayList<>();
    private ProgressDialog progressDialog;
    private TextView txtNoDataFound;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private boolean ascendingA = true;
    private boolean ascendingB = true;
    private boolean ascendingC = true;
    private boolean isSearchVisible = false;
    private boolean isWebServiceRespond = false;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private LinearLayout sortingLayout;
    private RelativeLayout rl_search;
    private EditText etSearch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        txtNoDataFound = view.findViewById(R.id.txt_no_data_found);
        recyclerView = view.findViewById(R.id.recycler_view);
        Button sortPropsA = view.findViewById(R.id.props_a);
        Button sortPropsB = view.findViewById(R.id.props_b);
        Button sortPropsC = view.findViewById(R.id.props_c);
        mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        sortingLayout = view.findViewById(R.id.sorting_layout);
        rl_search = view.findViewById(R.id.rl_search);
        ImageView searchView = view.findViewById(R.id.search);
        etSearch = view.findViewById(R.id.search_et);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ItemListAdapter(getActivity(),listArray);
        recyclerView.setAdapter(adapter);

        //Sorting Props A
        sortPropsA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortPropsDataA(ascendingA);
                ascendingA = !ascendingA;
            }
        });

        //Sorting Props B
        sortPropsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortPropsDataB(ascendingB);
                ascendingB = !ascendingB;
            }
        });

        //Sorting Props C
        sortPropsC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortPropsDataC(ascendingC);
                ascendingC = !ascendingC;
            }
        });

        //Pull down to refresh
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        onCallingAPI();
                    }
                }
        );

        //Hide and show of search layout
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearchVisible) {
                    rl_search.setVisibility(View.VISIBLE);
                    isSearchVisible = true;
                }else {
                    rl_search.setVisibility(View.GONE);
                    isSearchVisible = false;
                }
            }
        });

        //SearchView on editText field
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sortedListArray = filter(listArray,s.toString());
                adapter.setFilter(sortedListArray);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //get API called first when view create method will call
        onCallingAPI();

        return view;
    }


    private void onCallingAPI(){
        if(listArray!=null){
            listArray.clear();
        }
        mySwipeRefreshLayout.setRefreshing(false);
        showProgressDialog();
        getProductAPI();
    }

    private void sortPropsDataA(final boolean asc){
        Collections.sort(listArray, new Comparator<ItemModel>() {
            @Override
            public int compare(ItemModel lhs, ItemModel rhs) {
                if (asc){
                    return lhs.getSortPropsModel().getA() < rhs.getSortPropsModel().getA() ? -1 : 0;
                }else {
                    return lhs.getSortPropsModel().getA() > rhs.getSortPropsModel().getA() ? -1 : 0;
                }
            }
        });

        sortedListArray = filter(listArray,etSearch.getText().toString());
        adapter.setFilter(sortedListArray);
    }

    private void sortPropsDataB(final boolean asc){
        Collections.sort(listArray, new Comparator<ItemModel>() {
            @Override
            public int compare(ItemModel lhs, ItemModel rhs) {
                if (asc){
                    return lhs.getSortPropsModel().getB() < rhs.getSortPropsModel().getB() ? -1 : 0;
                }else {
                    return lhs.getSortPropsModel().getB() > rhs.getSortPropsModel().getB() ? -1 : 0;
                }
            }
        });

        sortedListArray = filter(listArray,etSearch.getText().toString());
        adapter.setFilter(sortedListArray);
    }

    private void sortPropsDataC(final boolean asc){
        Collections.sort(listArray, new Comparator<ItemModel>() {
            @Override
            public int compare(ItemModel lhs, ItemModel rhs) {
                if (asc){
                    return lhs.getSortPropsModel().getC() < rhs.getSortPropsModel().getC() ? -1 : 0;
                }else {
                    return lhs.getSortPropsModel().getC() > rhs.getSortPropsModel().getC() ? -1 : 0;
                }
            }
        });

        sortedListArray = filter(listArray,etSearch.getText().toString());
        adapter.setFilter(sortedListArray);
    }

    private void getProductAPI(){

        //Handling Handler in background
        handlingHandlerInBackgournd();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.get(NetworkConstant.BASE_URL + NetworkConstant.PRODUCT_LIST_ITEM_END_POINT)
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cancelProgressDialog();
                        isWebServiceRespond = true;
                        try {
                            JSONArray data = response.getJSONArray(Constant.DATA);
                            if (data.length()>0) {
                                txtNoDataFound.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                sortingLayout.setVisibility(View.VISIBLE);
                                ItemModel[] model = new ItemModel[data.length()];
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject jsonObject = data.getJSONObject(i);
                                    model[i] = new ItemModel(
                                            jsonObject.getString(Constant.ID),
                                            jsonObject.getString(Constant.CATEGORY_ID),
                                            jsonObject.getString(Constant.NAME),
                                            jsonObject.getString(Constant.CATEGORY_NAME),
                                            jsonObject.getString(Constant.DESCRIPTION),
                                            jsonObject.getString(Constant.PRICE),
                                            jsonObject.getString(Constant.QTY),
                                            jsonObject.getString(Constant.IMAGE));

                                    JSONObject object = jsonObject.getJSONObject(Constant.SORT_PROPS);
                                    SortPropsModel sort = new SortPropsModel();
                                    sort.setA(object.getInt(Constant.SORT_PROPS_A));
                                    sort.setB(object.getInt(Constant.SORT_PROPS_B));
                                    sort.setC(object.getInt(Constant.SORT_PROPS_C));
                                    model[i].setSortPropsModel(sort);
                                    listArray.add(model[i]);
                                }
                                recyclerView.getRecycledViewPool().clear();
                                adapter.notifyDataSetChanged();

                            }else {
                                txtNoDataFound.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG,e.getMessage());
                            txtNoDataFound.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            sortingLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        cancelProgressDialog();
                        isWebServiceRespond = true;
                        txtNoDataFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        sortingLayout.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),error.getErrorBody(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<ItemModel> filter(List<ItemModel> al, String query) {
        query = query.toLowerCase();
        List<ItemModel> filteredModeList = new ArrayList<>();
        for (ItemModel model:al){
            String name = model.getName().toLowerCase();
            if(name.contains(query)){
                filteredModeList.add(model);
            }
        }
        return filteredModeList;
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIcon(0);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void cancelProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    private void handlingHandlerInBackgournd(){
        int TIME_OUT = 1000 * 60;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isWebServiceRespond) {
                    txtNoDataFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    sortingLayout.setVisibility(View.GONE);
                }
            }
        }, TIME_OUT);
    }
}