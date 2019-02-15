package com.bitlink.travelink.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bitlink.travelink.R;
import com.bitlink.travelink.util.AppContants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bitlink.travelink.activity.MainAppActivity.mAuth;
import static com.bitlink.travelink.activity.MainAppActivity.mAuthCurrentUser;
import static com.bitlink.travelink.util.AppContants.UserTransactions;

public class UserTransactionActivity extends Activity {

    View focusView = null;
    @BindView(R.id.reset_password_view)
    TextView resetPasswordView;
    @BindView(R.id.reset_password_message_view)
    TextView resetPasswordMeesageView;
    @BindView(R.id.input_email)
    EditText emailText;
    @BindView(R.id.input_password)
    EditText passwordText;
    @BindView(R.id.btn_change_email)
    Button changeEmailButton;
    @BindView(R.id.btn_change_password)
    Button changePasswordButton;
    @BindView(R.id.btn_reset_password)
    Button resetPasswordButton;
    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_transaction);

        ButterKnife.bind(this);

        // Get transaction key from intent
        int userTransactionId = getIntent().getIntExtra(AppContants.ARG_TRANSACTION, 0);

        UserTransactions userTransaction = UserTransactions.values()[userTransactionId];
        if (userTransaction == null) {
            throw new IllegalArgumentException("Must pass ARG_TRANSACTION");
        }

        if (userTransaction == UserTransactions.ChangeEmail) {
            passwordText.setVisibility(View.GONE);
            changePasswordButton.setVisibility(View.GONE);
            resetPasswordButton.setVisibility(View.GONE);
            resetPasswordView.setVisibility(View.GONE);
            resetPasswordMeesageView.setVisibility(View.GONE);

        } else if (userTransaction == UserTransactions.ChangePassword) {
            emailText.setVisibility(View.GONE);
            changeEmailButton.setVisibility(View.GONE);
            resetPasswordButton.setVisibility(View.GONE);
            resetPasswordView.setVisibility(View.GONE);
            resetPasswordMeesageView.setVisibility(View.GONE);
        } else {
            passwordText.setVisibility(View.GONE);
            changeEmailButton.setVisibility(View.GONE);
            changePasswordButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_change_email)
    void ChangeEmail() {
        if (!validate(UserTransactions.ChangeEmail)) {
            focusView.requestFocus();
            return;
        }

        String email = emailText.getText().toString();

        changeEmailButton.setEnabled(false);

        progressDialog = new MaterialDialog.Builder(UserTransactionActivity.this)
                .content(getResources().getString(R.string.change_email_text))
                .progress(true, 0)
                .cancelable(false)
                .build();
        progressDialog.show();

        mAuthCurrentUser.updateEmail(email.trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            onChangeEmailFailed();
                        } else {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            onChangeEmailSuccess();
                                            progressDialog.dismiss();
                                        }
                                    }, 3000);
                        }
                    }
                });
    }

    @OnClick(R.id.btn_change_password)
    void ChangePassword() {
        if (!validate(UserTransactions.ChangePassword)) {
            focusView.requestFocus();
            return;
        }

        String password = passwordText.getText().toString();

        changePasswordButton.setEnabled(false);

        progressDialog = new MaterialDialog.Builder(UserTransactionActivity.this)
                .content(getResources().getString(R.string.change_password_text))
                .progress(true, 0)
                .cancelable(false)
                .build();
        progressDialog.show();

        mAuthCurrentUser.updatePassword(password.trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            onChangePasswordFailed();
                        } else {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            onChangePasswordSuccess();
                                            progressDialog.dismiss();
                                        }
                                    }, 3000);
                        }
                    }
                });
    }

    @OnClick(R.id.btn_reset_password)
    void ResetPassword() {
        if (!validate(UserTransactions.ResetPassword)) {
            focusView.requestFocus();
            return;
        }

        String email = emailText.getText().toString();

        resetPasswordButton.setEnabled(false);

        progressDialog = new MaterialDialog.Builder(UserTransactionActivity.this)
                .content(getResources().getString(R.string.sending_reset_email_text))
                .progress(true, 0)
                .cancelable(false)
                .build();
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            onSendResetEmailFailed();
                        } else {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            onSendResetEmailSuccess();
                                            progressDialog.dismiss();
                                        }
                                    }, 3000);
                        }
                    }
                });
    }

    private void onChangeEmailSuccess() {
        MainAppActivity.showText(getResources().getString(R.string.change_email_successful));
        changeEmailButton.setEnabled(true);
        finish();
    }

    private void onChangeEmailFailed() {
        MainAppActivity.showText(getResources().getString(R.string.change_email_failed));
        changeEmailButton.setEnabled(true);
        progressDialog.dismiss();
    }

    private void onChangePasswordSuccess() {
        MainAppActivity.showText(getResources().getString(R.string.change_password_successful));
        changePasswordButton.setEnabled(true);
        finish();
    }

    private void onChangePasswordFailed() {
        MainAppActivity.showText(getResources().getString(R.string.change_password_failed));
        changePasswordButton.setEnabled(true);
        progressDialog.dismiss();
    }

    private void onSendResetEmailSuccess() {
        MainAppActivity.showText(getResources().getString(R.string.sent_email_to_reset_password));
        resetPasswordButton.setEnabled(true);
        finish();
    }

    private void onSendResetEmailFailed() {
        MainAppActivity.showText(getResources().getString(R.string.send_reset_email_failed));
        resetPasswordButton.setEnabled(true);
        progressDialog.dismiss();
    }

    private boolean validate(UserTransactions transaction) {

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            MainAppActivity.showText(getResources().getString(R.string.enter_valid_email));
            focusView = emailText;
            return false;
        }

        if (transaction == UserTransactions.ChangePassword) {
            if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
                MainAppActivity.showText(getResources().getString(R.string.enter_valid_password));
                focusView = passwordText;
                return false;
            }
        } else {
            return true;
        }

        return true;
    }
}
