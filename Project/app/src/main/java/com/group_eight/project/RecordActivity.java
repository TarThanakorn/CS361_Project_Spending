package com.group_eight.project;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    public int type_input = 0;
    public Bitmap bitmap;

    ActivityResultLauncher<Intent> activityResultLauncher1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            ImageView imageView = findViewById(R.id.uploadImage);
                            Bundle extras = data.getExtras();
                            bitmap = (Bitmap) extras.get("data");
                            imageView.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            Log.e("Log", "Error from Camera Activity");
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);


        ImageView btn1 = (ImageView) findViewById(R.id.camerabtn);
        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher1.launch(intent);
            }
        });

        RadioButton radio_income = (RadioButton) findViewById(R.id.radio_income);
        RadioButton radio_spent = (RadioButton) findViewById(R.id.radio_spent);
        radio_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checked = ((RadioButton) v).isChecked();
                if(checked){
                    type_input = 1;
                }
            }
        });
        radio_spent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean checked = ((RadioButton) v).isChecked();
                if(checked){
                    type_input = 2;
                }
            }
        });
        ImageView imageView = findViewById(R.id.uploadImage);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(o.getResultCode() == Activity.RESULT_OK){
                    Intent data = o.getData();
                    Uri uri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        });


        EditText amount_input = findViewById(R.id.amount_input);
        EditText desc_input = findViewById(R.id.desc_input);

        Button submit = findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type_input != 0 && !amount_input.getText().toString().isEmpty() && !desc_input.getText().toString().isEmpty()){
                    hideKeyboard();
                    saveData();
                }else{
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(RecordActivity.this);
                    dialog.setPositiveButton("Close", null);
                    dialog.setTitle(R.string.empty_title);
                    dialog.setMessage(R.string.empty_result);
                    dialog.show();
                }
            }
        });
    }

    public boolean saveData() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if(bitmap != null){
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            final String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

            EditText amount_input = findViewById(R.id.amount_input);
            EditText desc_input = findViewById(R.id.desc_input);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setPositiveButton("Close", null);
            String url = "http://10.0.2.2:80/project_g8/uploadData.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @SuppressLint("ResourceType")
                @Override
                public void onResponse(String response) {
                    Log.i("DbVolley", response);
                    try {
                        String strStatusID = "0";
                        String strError = "Unknown Status!";
                        JSONObject c;

                        JSONArray data = new JSONArray("["+response.toString()+"]");
                        for(int i = 0; i < data.length(); i++){
                            System.out.println(data);
                            c = data.getJSONObject(i);
                            strStatusID = c.getString("StatusID");
                            strError = c.getString("Error");
                        }
                        if(strStatusID.equals("0")){
                            dialog.setMessage(strError);
                            dialog.show();
                        } else {
                            dialog.setTitle(R.string.submit_title);
                            dialog.setMessage(R.string.submit_result);
                            dialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RecordActivity.this, "Submission Error!" , Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DbVolley", "Volley::onErrorResponse():"+error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams(){
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("sType", String.valueOf(type_input));
                    params.put("sDesc",desc_input.getText().toString());
                    params.put("sAmount",amount_input.getText().toString());
                    params.put("sImage", base64Image);
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        }else{
            EditText amount_input = findViewById(R.id.amount_input);
            EditText desc_input = findViewById(R.id.desc_input);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setPositiveButton("Close", null);
            String url = "http://10.0.2.2:80/project_g8/uploadData.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @SuppressLint("ResourceType")
                @Override
                public void onResponse(String response) {
                    Log.i("DbVolley", response);
                    try {
                        String strStatusID = "0";
                        String strError = "Unknown Status!";
                        JSONObject c;

                        JSONArray data = new JSONArray("["+response.toString()+"]");
                        for(int i = 0; i < data.length(); i++){
                            System.out.println(data);
                            c = data.getJSONObject(i);
                            strStatusID = c.getString("StatusID");
                            strError = c.getString("Error");
                        }
                        if(strStatusID.equals("0")){
                            dialog.setMessage(strError);
                            dialog.show();
                        } else {
                            dialog.setTitle(R.string.submit_title);
                            dialog.setMessage(R.string.submit_result);
                            dialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RecordActivity.this, "Submission Error!" , Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("DbVolley", "Volley::onErrorResponse():"+error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams(){
                    Map<String,String> params = new HashMap<String,String>();
                    params.put("sType", String.valueOf(type_input));
                    params.put("sDesc",desc_input.getText().toString());
                    params.put("sAmount",amount_input.getText().toString());
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        }
        return true;
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void hideKeyboard(){
        View view = getCurrentFocus(); // returns the view that has focus or null
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}