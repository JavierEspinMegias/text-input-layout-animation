package com.android.login_input_animations;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;import com.facebook.Profile;

public class MainActivity extends AppCompatActivity {
    TextInputLayout til_nombre, til_pass;
    TextInputEditText tiet_nombre, tiet_pass;
    Button enviar;


    private LoginButton FBloginButton;
    private ImageView fbImage;
    private CallbackManager callBackMng;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(MainActivity.this);
//        AppEventsLogger.activateApp(MainActivity.this);

        setContentView(R.layout.activity_main);

        til_nombre = (TextInputLayout) findViewById(R.id.til);
        tiet_nombre = (TextInputEditText)findViewById(R.id.tiet);

        til_pass = (TextInputLayout) findViewById(R.id.til_2);
        tiet_pass = (TextInputEditText)findViewById(R.id.tiet_2);



        enviar = (Button)findViewById(R.id.button_enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarFormulario();
            }
        });



        FBloginButton = (LoginButton)findViewById(R.id.loginFB);
        fbImage = (ImageView)findViewById(R.id.circleImageView2);
        callBackMng = CallbackManager.Factory.create();

        FBloginButton.setReadPermissions(Arrays.asList("email"));

        FBloginButton.registerCallback(callBackMng, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callBackMng.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken==null){
                tiet_nombre.setText("");
                tiet_pass.setText("");
                Toast.makeText(MainActivity.this, "Usuario deslogueado", Toast.LENGTH_SHORT).show();
            }else{
                loadUserProfile(currentAccessToken);
            }

        }
    };
    private void loadUserProfile(AccessToken newAccesToken){

        final GraphRequest request = GraphRequest.newMeRequest(newAccesToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");


                    String UserImageUrl="https://graph.facebook.com/" + id + "/picture?type=large";
                    Glide.with(getApplicationContext()).load(UserImageUrl).placeholder(R.drawable.ic_launcher_background).into(fbImage);

                    tiet_nombre.setText(email);

                    RequestOptions reqOptions = new RequestOptions();
                    reqOptions.dontAnimate();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    // Metodo request focus
    public void requestFocus(View v){
        if (v.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validarFormulario(){
        if (tiet_nombre.getText().toString().trim().isEmpty()){
            til_nombre.setError("Introduce tu nombre");
            requestFocus(tiet_nombre);
            return false;
        }else{
            if (tiet_nombre.getText().toString().trim().length() <3){
                til_nombre.setError("Minimo 3 caracteres");
                requestFocus(tiet_nombre);
                return false;
            }else{
                til_nombre.setErrorEnabled(false);
            }
        }

        if (tiet_pass.getText().toString().trim().isEmpty()){
            til_pass.setError("Introduce tu password");
            requestFocus(tiet_pass);
            return false;
        }else{
            if (tiet_pass.getText().toString().trim().length() < 6){
                til_pass.setError("Minimo 6 caracteres");
                requestFocus(tiet_pass);
                return false;
            }else{
                til_pass.setErrorEnabled(false);
            }
        }
        return true;
    }

    private void enviarFormulario(){
        if (!validarFormulario()){
            return;
        }
        String nombre = tiet_nombre.getText().toString().trim()+"";
        String pass = tiet_pass.getText().toString().trim()+"";
        Toast.makeText(this, nombre+" - "+pass, Toast.LENGTH_SHORT).show();
    }
}
