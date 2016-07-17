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
import com.buyhatke.chat.util.BHConstants;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.util.ConnectionDetectorUtility;
import com.buyhatke.chat.xmpp.BHService;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RegistrationActivity extends AppCompatActivity implements RegistrationView{


    private Button btnRegister;
    private EditText editMobile;
    private EditText editPassword;
    private EditText editFullName;
    private ProgressBar progressBar;
    private LinearLayout linButton;
    private RegistrationPresenter registrationPresenter;
    private ConnectionDetectorUtility detectorUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initViews();
        detectorUtility = new ConnectionDetectorUtility(this);
        registrationPresenter = RegistrationPresenterImpl.getInstance(this);
        btnRegister.setOnClickListener(v -> validateAndRegister());
    }

    private void validateAndRegister() {
        boolean flag = true;
        String username = editMobile.getText().toString();
        String password = editPassword.getText().toString();
        String fullName = editFullName.getText().toString();
        if (username.length() < 10) {
            editMobile.setError(getString(R.string.error_mobile_number));
            flag = false;
        }
        if (password.length() < 6) {
            editPassword.setError(getString(R.string.error_password));
            flag = false;
        }
        if (fullName.length() == 0) {
            editFullName.setError(getString(R.string.error_full_name));
        }
        if (flag) {
            runOnUiThread(() -> {
                if (detectorUtility.isConnectingToInternet()) {
                    linButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    registrationPresenter.connectAndRegister(username, password, fullName);
                } else {
                    BHUtility.getErrorDialog(RegistrationActivity.this, "No internet connection")
                            .show();
                }
            });
        }
    }

    private void initViews() {
        btnRegister = (Button) findViewById(R.id.btn_register);
        editMobile = (EditText) findViewById(R.id.txt_jombaycom);
        editPassword = (EditText) findViewById(R.id.edit_password);
        editFullName = (EditText) findViewById(R.id.edit_full_name);
        progressBar = (ProgressBar) findViewById(R.id.pb_registration);
        linButton = (LinearLayout) findViewById(R.id.layout_btn);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
    }

    @Override
    public void startBHService(boolean isRegister) {
        Intent intent = BHService.createIntent(this);
        intent.putExtra(BHConstants.INTENT_KEY_IS_REGISTER, isRegister);
        startService(intent);
    }

    @Override
    public void showError() {
        runOnUiThread(() -> {
            BHUtility.getErrorDialog(this,
                    "Registration failed try again").show();
            linButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void goToDash() {
        BHUtility.writeBooleanToPreferences(this, BHConstants.SHARED_PREFERENCE_KEY_REG, true);
        startActivity(MessengerActivity.createIntent(RegistrationActivity.this));
        finish();
    }

    @Override
    public void showUserAlreadyExistError() {
        runOnUiThread(() -> {
            BHUtility.getErrorDialog(this,
                    "User already exist try with other username").show();
            linButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        });
    }
}

