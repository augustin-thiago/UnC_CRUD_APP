package com.example.unccrudapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.unccrudapp.model.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
    private SwipeRefreshLayout refreshLayout;
    private RequestQueue requestQueue;
    private JsonArrayRequest arrayRequest;
    private Context context;
    private ArrayList<Store> store;
    private String url = "";
    private StoreAdapter storeAdapter;
    private RecyclerView recyclerView;

    public StoreAdapter(Context context, ArrayList<Store> store) {
        this.context = context;
        this.store = store;
    }

    @NonNull
    @Override
    public StoreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.store_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.MyViewHolder holder, int position) {
        holder.txtName.setText(store.get(position).getName());
        holder.txtNumber.setText(String.valueOf(position + 1));
        holder.edtStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = store.get(position).getId();
                String name = store.get(position).getName();
                String site = store.get(position).getSite();
                String type = store.get(position).getType();
                String city = store.get(position).getCity();
                String state = store.get(position).getState();
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", id);
                    object.put("name", name);
                    object.put("site", site);
                    object.put("type", type);
                    object.put("city", city);
                    object.put("state", state);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editStore(id, object);
            }
        });
        holder.deleteStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = store.get(position).getId();
                deleteStore(id);
            }
        });
    }

    private void deleteStore(final String id) {
        TextView txtStore, txtClose;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.store_delete);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtStore = (TextView) dialog.findViewById(R.id.txtStore);

        txtStore.setText("Excluir Loja");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave = (Button) dialog.findViewById(R.id.btnDelete);
        String storeId = id;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(dialog, storeId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void delete(Dialog dialog, String storeId) {
        String url = "http://10.0.2.2:3000/stores/delete/" + storeId;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Toast.makeText(context, "Dados excluídos com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void editStore(final String id, JSONObject object) {
        TextView txtStore, txtClose;
        EditText edtName, edtSite, edtType, edtCity, edtState;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_modstore);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtStore = (TextView) dialog.findViewById(R.id.txtStore);

        txtStore.setText("Alterar Loja");

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
        String storeId = null;
        try {
            storeId = object.getString("_id");
            edtName.setText(object.getString("name"));
            edtSite.setText(object.getString("site"));
            edtType.setText(object.getString("type"));
            edtCity.setText(object.getString("city"));
            edtState.setText(object.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalStoreId = storeId;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", finalStoreId);
                    object.put("name", edtName.getText());
                    object.put("site", edtSite.getText());
                    object.put("type", edtType.getText());
                    object.put("city", edtCity.getText());
                    object.put("state", edtState.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submit(object, dialog, finalStoreId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submit(final JSONObject object, final Dialog dialog, String id) {
        String url = "http://10.0.2.2:3000/stores/update/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                /*
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        store.clear();
                        getData();
                    }
                });
                 */
                Toast.makeText(context, "Dados alterados com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Erro ao alterar dados!", Toast.LENGTH_LONG).show();
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
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
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        //requestQueue = Volley.newRequestQueue(StoreActivity.this);
        //requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Store> store) {
        //storeAdapter = new Store(this, store);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(storeAdapter);
    }

    @Override
    public int getItemCount() {
        return store.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtNumber;
        private ImageView edtStore, deleteStore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumber = (TextView) itemView.findViewById(R.id.idNumber);
            txtName = (TextView) itemView.findViewById(R.id.nameStore);
            edtStore = (ImageView) itemView.findViewById(R.id.editStore);
            deleteStore = (ImageView) itemView.findViewById(R.id.deleteStore);
        }
    }
}
