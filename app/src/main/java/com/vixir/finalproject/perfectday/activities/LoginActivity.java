package com.vixir.finalproject.perfectday.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.vixir.finalproject.perfectday.R;
import com.vixir.finalproject.perfectday.utils.Constants;
import com.vixir.finalproject.perfectday.utils.Utils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.vixir.finalproject.perfectday.utils.Constants.FIRST_TIME_LOGIN;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 274;
    @BindView(R.id.google_button)
    Button googleLoginButton;
    @BindView(R.id.linear_lay)
    LinearLayout linearLayout;
    @BindView(R.id.splash_background)
    ImageView splashBackground;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if (null != actionBar) actionBar.hide();
        ButterKnife.bind(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            showWhiteSnackBar(R.string.sign_in_successful);
            googleLoginButton.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }


    @OnClick(R.id.google_button)
    protected void onClick() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder().setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                SharedPreferences.Editor editor = getSharedPreferences(FIRST_TIME_LOGIN, MODE_PRIVATE).edit();
                editor.putBoolean(Constants.IS_FIRST_TIME, true);
                editor.apply();
                googleLoginButton.setVisibility(View.INVISIBLE);
                showWhiteSnackBar(R.string.sign_in_successful);
                linearLayout.setVisibility(View.GONE);
                Utils.fetchDataFromFirebase(LoginActivity.this);
                return;
            } else {
                if (response == null) {
                    showWhiteSnackBar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showWhiteSnackBar(R.string.no_internet_connection);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showWhiteSnackBar(R.string.unknown_error);
                    return;
                }
            }

            showWhiteSnackBar(R.string.unknown_sign_in_response);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void showWhiteSnackBar(int signed_in_message) {
        LayoutInflater inflater = getLayoutInflater();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId;
        View layout = inflater.inflate(R.layout.custom_toast_view,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView text = (TextView) layout.findViewById(R.id.text);
        if (null != auth.getCurrentUser()) {
            userId = auth.getCurrentUser().getDisplayName();
            String strMeatFormat = getResources().getString(R.string.welcome_format);
            String strMeatMsg = String.format(strMeatFormat, userId);
            text.setText(strMeatMsg);
        } else {
            text.setText(signed_in_message);
        }
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}

