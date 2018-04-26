package com.easyschools.teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Login extends AppCompatActivity {

    private Button login;
    private Typeface custom_font;
    private RelativeLayout loginLayout;
    private String lang = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView en;
    private ImageView ar;
    public static Apis apis;
    private EditText user_email;
    private EditText user_psd;
    private TextView forget_psd;
    public static String PREF_USERNAME = "email";
    public static String PREF_PASSWORD = "password";
    CheckBox remember_me;
    private String email, password = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.loginBtn);
        apis = new Apis();
        user_email = findViewById(R.id.user_email);
        user_psd = findViewById(R.id.user_psd);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Login.this, MainTabsActivity.class);
//                startActivity(intent);
                login();
            }
        });
        lang = Locale.getDefault().getDisplayLanguage();
        sharedPreferences = getSharedPreferences("DATA",
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (lang.equals("العربية"))
            lang = "ar";
        else
            lang = "en";
        editor.putString("LANG", lang).apply();
        en = findViewById(R.id.en);
        ar = findViewById(R.id.ar);
        ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String arabic = "ar";
                changeLang(arabic);
                startActivity(new Intent(getBaseContext(), Login.class));
                Log.d("arabic", arabic);
                editor.putString("LANG", arabic).apply();
            }
        });
        en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String english = "en";
                changeLang(english);
                startActivity(new Intent(getBaseContext(), Login.class));
                Log.d("english", english);
                editor.putString("LANG", english).apply();

            }
        });
        remember_me = findViewById(R.id.remember_me);
        remember_me.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putString(PREF_USERNAME, user_email.getText().toString()).apply();
                    editor.putString(PREF_PASSWORD, user_psd.getText().toString()).apply();
                }
            }
        });
        editor.putBoolean("FIRST", true);
        editor.apply();
        forget_psd = findViewById(R.id.forget_psd);
        forget_psd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            final String[] permission = new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.CAMERA,
            };
            ActivityCompat.requestPermissions(this, permission, 0);
        }
    }

    private void changeLang(String lang) {
        if (lang.equalsIgnoreCase("")) {
            return;
        }
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration configuration = new Configuration();
        configuration.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().
                getResources().getDisplayMetrics());
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(Login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.forget_psd);
        ImageButton ok = (ImageButton) dialog.findViewById(R.id.done);
        ImageButton cancel = (ImageButton) dialog.findViewById(R.id.cancel);
        final EditText forget_email = (EditText) dialog.findViewById(R.id.forget_psd_email);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forget_psd(forget_email.getText().toString());
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void forget_psd(final String email) {
        StringRequest sr = new StringRequest(Request.Method.POST,
                apis.getUrl() + lang + "/student/ForgetPassword?email=" + email.trim()
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject result = new JSONObject(response);
                    for (int i = 0; i < result.length(); i++) {
                        Toast.makeText(getApplicationContext(),
                                (getResources().getString(R.string.psd_updated_msg))
                                , Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("FORGETPSD", error.toString());
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(sr);
    }
    @Override
    protected void onStart() {
        super.onStart();
        email = sharedPreferences.getString(PREF_USERNAME, null);
        password = sharedPreferences.getString(PREF_PASSWORD, null);
        user_email.setText(email);
        user_psd.setText(password);
        if (email != null && password != null) {
            remember_me.setChecked(true);
            Log.d("remember", email + " " + password);
        } else {
            remember_me.setChecked(false);
        }

    }
    private void login() {
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST,
                        apis.getUrl() + lang + "/users/login?email=" + user_email.getText().toString().trim() +
                                "&password=" + user_psd.getText().toString(),
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Boolean status = response.getBoolean("status");
                            if (status) {
                                editor.putString("API_TOKEN", response.getString("token")).apply();
                                Log.d("USER_TOKEN", response.getString("token"));
                                JSONObject dataObject = response.getJSONObject("user");
                                for (int i = 0; i < dataObject.length(); i++) {
                                    editor.putString("ID", dataObject.getString("id")).apply();
                                    editor.putString("SCHOOL_ID", dataObject.getString("school_id")).apply();
                                    editor.putString("NAME", dataObject.getString("name")).apply();
                                    apis.setSchool_logo("http://crm.easyschools.org/uploads/"
                                            +dataObject.getString("schoo_iamge"));
                                    Log.d("IMAGE","http://crm.easyschools.org/uploads/"
                                            +dataObject.getString("schoo_iamge"));
                                }
                                Intent intent = new Intent(Login.this, MainTabsActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Log.d("LOGIN_RESPONSE", response.toString());
                                Toast.makeText(Login.this,response.getString("message").toString(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR_LOGIN", error.toString());
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);


    }
}
