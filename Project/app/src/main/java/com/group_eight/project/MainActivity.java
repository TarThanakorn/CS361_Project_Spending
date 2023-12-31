package com.group_eight.project;

import static java.lang.Float.parseFloat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    DecimalFormat formatter = new DecimalFormat("#,###.##");
    public JSONArray data;
    public SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView)findViewById(R.id.list);
        final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();

        String url = "http://10.0.2.2:80/project_g8/fetchData.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        TextView valueBalance = findViewById(R.id.valuebalance);
                        try {
                            float balance = 0,income = 0,spent = 0;
                            TextView typeView = findViewById(R.id.col_type);
                            HashMap<String, String> map;
                            data = new JSONArray(response);
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject c = data.getJSONObject(i);
                                String types = "";
                                String amount = "";
                                String image = "";
                                if(c.getString("type").toString().equals("1")){
                                    types = "Income";
                                    income = income + parseFloat(c.getString("amount"));
                                    balance = balance + parseFloat(c.getString("amount"));
                                    amount = "+ "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                                }else if (c.getString("type").toString().equals("2")){
                                    types = "Spent";
                                    spent = spent + parseFloat(c.getString("amount"));
                                    balance = balance - parseFloat(c.getString("amount"));
                                    amount = "- "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                                }
                                if(c.getString("image") != null){
                                    image = c.getString("image");
                                }
                                map = new HashMap<String, String>();
                                map.put("type", types);
                                map.put("desc", c.getString("descs"));
                                map.put("amount", amount);
                                map.put("image", image);
                                map.put("id", c.getString("id"));
                                MyArrList.add(map);
                            }
                            valueBalance = findViewById(R.id.valuebalance);
                            TextView valueIncome = findViewById(R.id.valueincome);
                            TextView valueSpent = findViewById(R.id.valuespent);
                            valueBalance.setText(formatter.format(balance)+" ฿");
                            valueIncome.setText("+ "+formatter.format(income)+" ฿");
                            valueSpent.setText("- "+formatter.format(spent)+" ฿");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        simpleAdapter = new SimpleAdapter(MainActivity.this, MyArrList, R.layout.activity_column,
                                new String[]{"type", "desc", "amount", "image", "id"},
                                new int[]{R.id.col_type, R.id.col_desc, R.id.col_amount, R.id.col_image, R.id.col_id});
                        listView.setAdapter(simpleAdapter);

                        final AlertDialog.Builder viewDetail = new AlertDialog.Builder(MainActivity.this);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> myAdapter, View myView,
                                                    int position, long id) {
                                String sType = MyArrList.get(position).get("type");
                                String sDesc = MyArrList.get(position).get("desc");
                                String sAmount = MyArrList.get(position).get("amount");
                                String sID = MyArrList.get(position).get("id");
                                ImageView image = new ImageView(MainActivity.this);
                                if(MyArrList.get(position).get("image") != null){
                                    String imageUrl = "http://10.0.2.2:80/project_g8/"+MyArrList.get(position).get("image");
                                    Picasso.get()
                                            .load(imageUrl)
                                            .into(image);
                                    viewDetail.setView(image);
                                }

                                viewDetail.setTitle("Order");
                                viewDetail.setMessage("Type : " + sType + "\n"
                                        + "Amount : " + sAmount + "\n" + "Describe : " + sDesc + "\n");
                                viewDetail.setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                viewDetail.setNeutralButton("REMOVE",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                removeData(sID);
                                            }
                                        });
                                viewDetail.show();
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Log", "Volley::onErrorResponse():" + error.getMessage());
                    }
        });

        Button btn1 = findViewById(R.id.button1);
        Button btn2 = findViewById(R.id.button2);
        Button btn3 = findViewById(R.id.button3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setBackground(getResources().getDrawable(R.drawable.round));
                btn2.setBackground(getResources().getDrawable(R.drawable.round_bg));
                btn3.setBackground(getResources().getDrawable(R.drawable.round_bg));
                MyArrList.clear();
                listView.setAdapter(simpleAdapter);
                try {
                    float balance = 0,income = 0,spent = 0;
                    HashMap<String, String> map;
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        String types = "";
                        String amount = "";
                        String image = "";
                        if(c.getString("type").toString().equals("1")){
                            types = "Income";
                            income = income + parseFloat(c.getString("amount"));
                            balance = balance + parseFloat(c.getString("amount"));
                            amount = "+ "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                        }else if (c.getString("type").toString().equals("2")){
                            types = "Spent";
                            spent = spent + parseFloat(c.getString("amount"));
                            balance = balance - parseFloat(c.getString("amount"));
                            amount = "- "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                        }
                        if(c.getString("image") != null){
                            image = c.getString("image");
                        }
                        map = new HashMap<String, String>();
                        map.put("type", types);
                        map.put("desc", c.getString("descs"));
                        map.put("amount", amount);
                        map.put("image", image);
                        map.put("id", c.getString("id"));
                        MyArrList.add(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                simpleAdapter = new SimpleAdapter(MainActivity.this, MyArrList, R.layout.activity_column,
                        new String[]{"type", "desc", "amount", "image", "id"},
                        new int[]{R.id.col_type, R.id.col_desc, R.id.col_amount, R.id.col_image, R.id.col_id});
                listView.setAdapter(simpleAdapter);
                final AlertDialog.Builder viewDetail = new AlertDialog.Builder(MainActivity.this);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long id) {
                        String sType = MyArrList.get(position).get("type");
                        String sDesc = MyArrList.get(position).get("desc");
                        String sAmount = MyArrList.get(position).get("amount");
                        String sID = MyArrList.get(position).get("id");
                        ImageView image = new ImageView(MainActivity.this);
                        if(MyArrList.get(position).get("image") != null){
                            String imageUrl = "http://10.0.2.2:80/project_g8/"+MyArrList.get(position).get("image");
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(image);
                            viewDetail.setView(image);
                        }
                        viewDetail.setTitle("Order");
                        viewDetail.setMessage("Type : " + sType + "\n"
                                + "Amount : " + sAmount + "\n" + "Describe : " + sDesc + "\n");
                        viewDetail.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        viewDetail.setNeutralButton("REMOVE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        removeData(sID);
                                    }
                                });
                        viewDetail.show();
                    }
                });
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn2.setBackground(getResources().getDrawable(R.drawable.round));
                btn1.setBackground(getResources().getDrawable(R.drawable.round_bg));
                btn3.setBackground(getResources().getDrawable(R.drawable.round_bg));
                MyArrList.clear();
                listView.setAdapter(simpleAdapter);
                try {
                    float balance = 0,income = 0,spent = 0;
                    HashMap<String, String> map;
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        String types = "";
                        String amount = "";
                        String image = "";
                        if(c.getString("type").toString().equals("1")){
                            types = "Income";
                            income = income + parseFloat(c.getString("amount"));
                            balance = balance + parseFloat(c.getString("amount"));
                            amount = "+ "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                        }else if (c.getString("type").toString().equals("2")){
                            types = "Spent";
                            spent = spent + parseFloat(c.getString("amount"));
                            balance = balance - parseFloat(c.getString("amount"));
                            amount = "- "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                        }
                        if(c.getString("image") != null){
                            image = c.getString("image");
                        }
                        if(c.getString("type").toString().equals("1")) {
                            map = new HashMap<String, String>();
                            map.put("type", types);
                            map.put("desc", c.getString("descs"));
                            map.put("amount", amount);
                            map.put("image", image);
                            map.put("id", c.getString("id"));
                            MyArrList.add(map);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                simpleAdapter = new SimpleAdapter(MainActivity.this, MyArrList, R.layout.activity_column,
                        new String[]{"type", "desc", "amount", "image", "id"},
                        new int[]{R.id.col_type, R.id.col_desc, R.id.col_amount, R.id.col_image, R.id.col_id});
                listView.setAdapter(simpleAdapter);
                final AlertDialog.Builder viewDetail = new AlertDialog.Builder(MainActivity.this);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long id) {
                        String sType = MyArrList.get(position).get("type");
                        String sDesc = MyArrList.get(position).get("desc");
                        String sAmount = MyArrList.get(position).get("amount");
                        String sID = MyArrList.get(position).get("id");
                        ImageView image = new ImageView(MainActivity.this);
                        if(MyArrList.get(position).get("image") != null){
                            String imageUrl = "http://10.0.2.2:80/project_g8/"+MyArrList.get(position).get("image");
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(image);
                            viewDetail.setView(image);
                        }
                        viewDetail.setTitle("Order");
                        viewDetail.setMessage("Type : " + sType + "\n"
                                + "Amount : " + sAmount + "\n" + "Describe : " + sDesc + "\n");
                        viewDetail.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        viewDetail.setNeutralButton("REMOVE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        removeData(sID);
                                    }
                                });
                        viewDetail.show();
                    }
                });
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn3.setBackground(getResources().getDrawable(R.drawable.round));
                btn1.setBackground(getResources().getDrawable(R.drawable.round_bg));
                btn2.setBackground(getResources().getDrawable(R.drawable.round_bg));
                MyArrList.clear();
                listView.setAdapter(simpleAdapter);
                try {
                    float balance = 0,income = 0,spent = 0;
                    HashMap<String, String> map;
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        String types = "";
                        String amount = "";
                        String image = "";
                        if(c.getString("type").toString().equals("1")){
                            types = "Income";
                            income = income + parseFloat(c.getString("amount"));
                            balance = balance + parseFloat(c.getString("amount"));
                            amount = "+ "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                        }else if (c.getString("type").toString().equals("2")){
                            types = "Spent";
                            spent = spent + parseFloat(c.getString("amount"));
                            balance = balance - parseFloat(c.getString("amount"));
                            amount = "- "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                        }
                        if(c.getString("image") != null){
                            image = c.getString("image");
                        }
                        if(c.getString("type").toString().equals("2")) {
                            map = new HashMap<String, String>();
                            map.put("type", types);
                            map.put("desc", c.getString("descs"));
                            map.put("amount", amount);
                            map.put("image", image);
                            map.put("id", c.getString("id"));
                            MyArrList.add(map);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                simpleAdapter = new SimpleAdapter(MainActivity.this, MyArrList, R.layout.activity_column,
                        new String[]{"type", "desc", "amount", "image", "id"},
                        new int[]{R.id.col_type, R.id.col_desc, R.id.col_amount, R.id.col_image, R.id.col_id});
                listView.setAdapter(simpleAdapter);
                final AlertDialog.Builder viewDetail = new AlertDialog.Builder(MainActivity.this);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView, int position, long id) {
                        String sType = MyArrList.get(position).get("type");
                        String sDesc = MyArrList.get(position).get("desc");
                        String sAmount = MyArrList.get(position).get("amount");
                        String sID = MyArrList.get(position).get("id");
                        ImageView image = new ImageView(MainActivity.this);
                        if(MyArrList.get(position).get("image") != null){
                            String imageUrl = "http://10.0.2.2:80/project_g8/"+MyArrList.get(position).get("image");
                            Picasso.get()
                                    .load(imageUrl)
                                    .into(image);
                            viewDetail.setView(image);
                        }
                        viewDetail.setTitle("Order");
                        viewDetail.setMessage("Type : " + sType + "\n"
                                + "Amount : " + sAmount + "\n" + "Describe : " + sDesc + "\n");
                        viewDetail.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        viewDetail.setNeutralButton("REMOVE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        removeData(sID);
                                    }
                                });
                        viewDetail.show();
                    }
                });
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button btn1 = findViewById(R.id.button1);
        Button btn2 = findViewById(R.id.button2);
        Button btn3 = findViewById(R.id.button3);
        final ListView listView = (ListView)findViewById(R.id.list);
        final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
        TextView x = findViewById(R.id.valuebalance);
        String url = "http://10.0.2.2:80/project_g8/fetchData.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        btn1.setBackground(getResources().getDrawable(R.drawable.round));
                        btn2.setBackground(getResources().getDrawable(R.drawable.round_bg));
                        btn3.setBackground(getResources().getDrawable(R.drawable.round_bg));
                        JSONArray data;
                        TextView valueBalance = findViewById(R.id.valuebalance);
                        try {
                            float balance = 0,income = 0,spent = 0;
                            TextView typeView = findViewById(R.id.col_type);
                            HashMap<String, String> map;
                            data = new JSONArray(response);
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject c = data.getJSONObject(i);
                                String types = "";
                                String amount = "";
                                String image = "";
                                if(c.getString("type").toString().equals("1")){
                                    types = "Income";
                                    income = income + parseFloat(c.getString("amount"));
                                    balance = balance + parseFloat(c.getString("amount"));
                                    amount = "+ "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                                }else if (c.getString("type").toString().equals("2")){
                                    types = "Spent";
                                    spent = spent + parseFloat(c.getString("amount"));
                                    balance = balance - parseFloat(c.getString("amount"));
                                    amount = "- "+formatter.format(parseFloat(c.getString("amount")))+" ฿";
                                }
                                if(c.getString("image") != null){
                                    image = c.getString("image");
                                }
                                map = new HashMap<String, String>();
                                map.put("type", types);
                                map.put("desc", c.getString("descs"));
                                map.put("amount", amount);
                                map.put("image", image);
                                map.put("id", c.getString("id"));
                                MyArrList.add(map);
                            }
                            valueBalance = findViewById(R.id.valuebalance);
                            TextView valueIncome = findViewById(R.id.valueincome);
                            TextView valueSpent = findViewById(R.id.valuespent);
                            valueBalance.setText(formatter.format(balance)+" ฿");
                            valueIncome.setText("+ "+formatter.format(income)+" ฿");
                            valueSpent.setText("- "+formatter.format(spent)+" ฿");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        simpleAdapter = new SimpleAdapter(MainActivity.this, MyArrList, R.layout.activity_column,
                                new String[]{"type", "desc", "amount", "image", "id"},
                                new int[]{R.id.col_type, R.id.col_desc, R.id.col_amount, R.id.col_image, R.id.col_id});
                        listView.setAdapter(simpleAdapter);

                        final AlertDialog.Builder viewDetail = new AlertDialog.Builder(MainActivity.this);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> myAdapter, View myView,
                                                    int position, long id) {
                                String sType = MyArrList.get(position).get("type");
                                String sDesc = MyArrList.get(position).get("desc");
                                String sAmount = MyArrList.get(position).get("amount");
                                String sID = MyArrList.get(position).get("id");
                                ImageView image = new ImageView(MainActivity.this);
                                if(MyArrList.get(position).get("image") != null){
                                    String imageUrl = "http://10.0.2.2:80/project_g8/"+MyArrList.get(position).get("image");
                                    Picasso.get()
                                            .load(imageUrl)
                                            .into(image);
                                    viewDetail.setView(image);
                                }

                                viewDetail.setTitle("Order");
                                viewDetail.setMessage("Type : " + sType + "\n"
                                        + "Amount : " + sAmount + "\n" + "Describe : " + sDesc + "\n");
                                viewDetail.setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                viewDetail.setNeutralButton("REMOVE",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                removeData(sID);
                                            }
                                        });
                                viewDetail.show();
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Log", "Volley::onErrorResponse():" + error.getMessage());
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void removeData(String sID){
        String urls = "http://10.0.2.2:80/project_g8/deleteData.php/"+sID;
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, urls, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                final AlertDialog.Builder dialogs = new AlertDialog.Builder(MainActivity.this);
                dialogs.setPositiveButton("Close", null);
                dialogs.setTitle(R.string.submit_title);
                dialogs.setMessage(R.string.remove_result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Log", "Volley::onErrorResponse():" + error.getMessage());
            }
        });
        Volley.newRequestQueue(MainActivity.this).add(stringRequest2);
        this.recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.view_record) {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
            return false;
        }
        return super.onOptionsItemSelected(item);
    }
}