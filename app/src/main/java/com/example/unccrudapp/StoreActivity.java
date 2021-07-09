package com.example.unccrudapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unccrudapp.model.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<Store> store = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView;
    private Dialog dialog;
    private StoreAdapter storeAdapter;
    private String url = "http://10.0.2.2:3000/stores/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeDown);
        recyclerView = (RecyclerView) findViewById(R.id.store);

        dialog = new Dialog(this);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                store.clear();
                getData();
            }
        });
    }

    private void getData() {
        refreshLayout.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Store s = new Store();
                        s.setId(jsonObject.getString("_id"));
                        s.setName(jsonObject.getString("name"));
                        s.setSite(jsonObject.getString("site"));
                        s.setType(jsonObject.getString("type"));
                        s.setCity(jsonObject.getString("city"));
                        s.setState(jsonObject.getString("state"));
                        store.add(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(store);
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue = Volley.newRequestQueue(StoreActivity.this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Store> store) {
        storeAdapter = new StoreAdapter(this, store);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(storeAdapter);
    }

    public void addStore(View v) {
        TextView txtStore, txtClose;
        EditText edtName, edtSite, edtType, edtCity, edtState;
        Button btnSave;

        dialog.setContentView(R.layout.activity_modstore);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtStore = (TextView) dialog.findViewById(R.id.txtStore);

        txtStore.setText("Nova Loja");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtName = (EditText) dialog.findViewById(R.id.edtName);
        edtSite = (EditText) dialog.findViewById(R.id.edtSite);
        edtType = (EditText) dialog.findViewById(R.id.edtType);
        edtCity = (EditText) dialog.findViewById(R.id.edtCity);
        edtState = (EditText) dialog.findViewById(R.id.edtState);

        btnSave = (Button) dialog.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("name", edtName.getText());
                    object.put("site", edtSite.getText());
                    object.put("type", edtType.getText());
                    object.put("city", edtCity.getText());
                    object.put("state", edtState.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submit(object);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submit(JSONObject object) {
        String url = "http://10.0.2.2:3000/stores/create";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        store.clear();
                        getData();
                    }
                });
                Toast.makeText(getApplicationContext(), "Dados gravados com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Erro ao gravar dados!", Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @Override
    public void onRefresh() {
        store.clear();
        getData();
    }
}