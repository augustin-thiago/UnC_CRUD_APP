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
import com.example.unccrudapp.model.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    private SwipeRefreshLayout refreshLayout;
    private RequestQueue requestQueue;
    private JsonArrayRequest arrayRequest;
    private Context context;
    private ArrayList<Item> item;
    private String url = "";
    private ItemAdapter itemAdapter;
    private RecyclerView recyclerView;

    public ItemAdapter(Context context, ArrayList<Item> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewBrand) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.item_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.MyViewHolder holder, int position) {
        holder.txtName.setText(item.get(position).getName());
        holder.txtNumber.setText(String.valueOf(position + 1));
        holder.edtItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.get(position).getId();
                String name = item.get(position).getName();
                String type = item.get(position).getType();
                String brand = item.get(position).getBrand();
                Double price = item.get(position).getPrice();
                String picture = item.get(position).getPicture();
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", id);
                    object.put("name", name);
                    object.put("type", type);
                    object.put("brand", brand);
                    object.put("price", price);
                    object.put("picture", picture);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editItem(id, object);
            }
        });
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.get(position).getId();
                deleteItem(id);
            }
        });
    }

    private void deleteItem(final String id) {
        TextView txtItem, txtClose;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.item_delete);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtItem = (TextView) dialog.findViewById(R.id.txtItem);

        txtItem.setText("Excluir Produto");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave = (Button) dialog.findViewById(R.id.btnDelete);
        String itemId = id;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(dialog, itemId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void delete(Dialog dialog, String itemId) {
        String url = "http://10.0.2.2:3000/products/delete/" + itemId;
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

    private void editItem(final String id, JSONObject object) {
        TextView txtItem, txtClose;
        EditText edtName, edtType, edtBrand, edtPrice, edtPicture;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_moditem);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtItem = (TextView) dialog.findViewById(R.id.txtItem);

        txtItem.setText("Alterar Produto");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtName = (EditText) dialog.findViewById(R.id.edtName);
        edtType = (EditText) dialog.findViewById(R.id.edtType);
        edtBrand = (EditText) dialog.findViewById(R.id.edtBrand);
        edtPrice = (EditText) dialog.findViewById(R.id.edtPrice);
        edtPicture = (EditText) dialog.findViewById(R.id.edtPicture);

        btnSave = (Button) dialog.findViewById(R.id.btnSave);
        String itemId = null;
        try {
            itemId = object.getString("_id");
            edtName.setText(object.getString("name"));
            edtType.setText(object.getString("type"));
            edtBrand.setText(object.getString("brand"));
            edtPrice.setText(object.getString("price"));
            edtPicture.setText(object.getString("picture"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalItemId = itemId;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", finalItemId);
                    object.put("name", edtName.getText());
                    object.put("type", edtType.getText());
                    object.put("brand", edtBrand.getText());
                    object.put("price", edtPrice.getText());
                    object.put("picture", edtPicture.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submit(object, dialog, finalItemId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submit(final JSONObject object, final Dialog dialog, String id) {
        String url = "http://10.0.2.2:3000/products/update/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                /*
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        item.clear();
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
                        Item s = new Item();
                        s.setId(jsonObject.getString("_id"));
                        s.setName(jsonObject.getString("name"));
                        s.setType(jsonObject.getString("type"));
                        s.setBrand(jsonObject.getString("brand"));
                        s.setPrice(jsonObject.getDouble("price"));
                        s.setPicture(jsonObject.getString("picture"));
                        item.add(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(item);
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        //requestQueue = Volley.newRequestQueue(ItemActivity.this);
        //requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Item> item) {
        //itemAdapter = new Item(this, item);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtNumber;
        private ImageView edtItem, deleteItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumber = (TextView) itemView.findViewById(R.id.idNumber);
            txtName = (TextView) itemView.findViewById(R.id.nameItem);
            edtItem = (ImageView) itemView.findViewById(R.id.editItem);
            deleteItem = (ImageView) itemView.findViewById(R.id.deleteItem);
        }
    }
}
