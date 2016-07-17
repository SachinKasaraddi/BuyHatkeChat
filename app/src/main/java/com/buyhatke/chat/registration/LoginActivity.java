package com.buyhatke.chat.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.buyhatke.chat.R;
import com.buyhatke.chat.messenger.MessengerActivity;
import com.buyhatke.chat.util.BHApplication;
import com.buyhatke.chat.util.BHConstants;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.xmpp.BHService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class LoginActivity extends AppCompatActivity implements LoginView {


    //private Button btnRegister;

    @Bind(R.id.btn_login)
    Button btnLogin;

    @Bind(R.id.edit_user_name)
    EditText edtUsername;

    @Bind(R.id.pb_registration)
    ProgressBar progressBar;

    @Bind(R.id.layout_btn)
    LinearLayout linButton;

    private LoginPresenter loginPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginPresenter = RegistrationPresenterImpl.getInstance(this);
    }

    @OnClick(R.id.btn_login)
    public void checkAndLogin() {
        boolean flag = true;
        String username = edtUsername.getText().toString();
        String password = username;
        if (username.length() < 3) {
            edtUsername.setError("Min 3 characters");
            flag = false;
        }
        if (!BHApplication.isOnline()){
            BHUtility.getErrorDialog(this,"Not connected to internet !").show();
            return;
        }
        if (flag) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            loginPresenter.startConnection(username, password);
            linButton.setVisibility(View.GONE);
        }
    }



    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    public void goToDash() {
        BHUtility.writeBooleanToPreferences(this, BHConstants.SHARED_PREFERENCE_KEY_REG, true);
        startActivity(MessengerActivity.createIntent(LoginActivity.this));
        finish();
    }

    @Override
    public void showError() {
        runOnUiThread(() -> {
            BHUtility.getErrorDialog(this,
                    "Connection/Login failed try again").show();
            linButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        });
    }

    @Override
    public void startBHService() {
        startService(BHService.createIntent(LoginActivity.this));
    }
}

