package com.example.wallet;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.wallet.sdk.MobileSdk;
import com.example.wallet.sdk.NotOkResponseException;
import com.example.wallet.sdk.model.User;
import com.example.wallet.util.DebugUtils;
import com.example.wallet.util.UiUtils;

/**
 * @author Masoud Taghavian (https://github.com/mtaghavian)
 */

public class MainActivity extends Activity {

    public Context context;
    public MainActivity activity;
    public LinearLayout mainLayout;
    public static MobileSdk mobileSdk;
    private long backKeyPressTime = 0;
    public static final String appName = "Wallet";
    private View signInLayout, signUpLayout;
    private EditText signUpFNField, signUpLNField, signUpUNField, signUpPField;
    private EditText signInUNField, signInPField;
    private FingerPrintDialog fingerPrintDialog;
    public static final int ButtonColor = Color.rgb(76, 139, 245),
            textSize = 14, smallTextSize = 12, headerBgColor = Color.DKGRAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        this.activity = this;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Configuration config = context.getResources().getConfiguration();
        displayMetrics.densityDpi = DisplayMetrics.DENSITY_340;
        config.densityDpi = displayMetrics.densityDpi;
        config.fontScale = 1.6f;
        context.getResources().updateConfiguration(config, displayMetrics);

        mobileSdk = new MobileSdk(context);

        mainLayout = new LinearLayout(this);
        mainLayout.setBackgroundColor(Color.WHITE);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(createHeaderLayout(this, appName));

        signInLayout = createSignInLayout();
        signUpLayout = createSignUpLayout();
        mainLayout.addView(signInLayout);

        fingerPrintDialog = new FingerPrintDialog(context);

        setContentView(mainLayout);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mainLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mobileSdk.checkDevice();
                        } catch (Exception ex) {
                            UiUtils.makeToast(context, ex.getMessage(), mainLayout);
                            finish();
                        }
                    }
                });
            }
        }).start();
    }

    public static View createHeaderLayout(Context context, String header) {
        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setBackgroundColor(headerBgColor);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        headerLayout.addView(UiUtils.createImage(context, R.drawable.logo,
                null, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT), 60, 30, 60, 30));
        headerLayout.addView(UiUtils.createTextView(context, header,
                headerBgColor, Color.WHITE, textSize * 1.25f, true,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT)));
        return headerLayout;
    }

    public View createSignInLayout() {
        int bgColor = Color.WHITE, padding = 80, width = 800;

        LinearLayout signinLayout = new LinearLayout(this);
        signinLayout.setGravity(Gravity.CENTER_VERTICAL
                | Gravity.CENTER_HORIZONTAL);
        signinLayout.setBackgroundColor(bgColor);
        signinLayout.setOrientation(LinearLayout.VERTICAL);
        signinLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        signInUNField = UiUtils.createEditText(context, "Username", "", true,
                true, Color.rgb(255, 249, 232), textSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        signInUNField.setWidth(width);
        signinLayout.addView(signInUNField);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        signInPField = UiUtils.createEditText(context, "Password", "", true,
                true, Color.rgb(255, 249, 232), textSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        signInPField.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        signInPField.setWidth(width);
        signinLayout.addView(signInPField);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        Button signInBut = UiUtils.createButton(context, "SIGN IN",
                new Runnable() {

                    @Override
                    public void run() {
                        User user = new User();
                        user.setUsername("" + signInUNField.getText());
                        user.setPassword("" + signInPField.getText());
                        try {
                            user = mobileSdk.signin(user);
                            Intent intent = new Intent(getApplicationContext(),
                                    DashboardActivity.class);
                            intent.putExtra(
                                    "UserPresentation",
                                    "" + user.getFirstname() + " "
                                            + user.getLastname());
                            startActivityForResult(intent, 0);
                        } catch (IOException e) {
                            UiUtils.makeToast(context, "IOException",
                                    mainLayout);
                        } catch (URISyntaxException e) {
                            UiUtils.makeToast(context, "URISyntaxException",
                                    mainLayout);
                        } catch (JSONException e) {
                            UiUtils.makeToast(context, "JSONException",
                                    mainLayout);
                        } catch (NotOkResponseException e) {
                            UiUtils.makeToast(context, e.getMessage(),
                                    mainLayout);
                        } catch (Exception e) {
                            UiUtils.makeToast(context,
                                    DebugUtils.serializeException(e),
                                    mainLayout);
                        }
                    }
                }, true, Color.rgb(242, 247, 255), ButtonColor, textSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        signInBut.setWidth(width);
        signinLayout.addView(signInBut);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        Button fingerBut = UiUtils.createButton(context,
                "Use fingerprint to fill these credentials", new Runnable() {

                    @Override
                    public void run() {
                        mainLayout.post(new Runnable() {

                            @Override
                            public void run() {
                                fingerPrintDialog.show();
                            }
                        });
                    }
                }, true, Color.WHITE, ButtonColor, smallTextSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        fingerBut.setWidth(width);
        signinLayout.addView(fingerBut);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        signinLayout.addView(UiUtils.createFiller(context,
                Color.rgb(242, 247, 255), width, 10));
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        signinLayout.addView(UiUtils.createTextView(context,
                "You don't have an account?", Color.WHITE, Color.GRAY,
                smallTextSize, true, new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)));
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        Button signUpBut = UiUtils.createButton(context, "SIGN UP",
                new Runnable() {

                    @Override
                    public void run() {
                        changePage(false);
                    }
                }, true, Color.rgb(242, 247, 255), ButtonColor, textSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        signUpBut.setWidth(width);
        signinLayout.addView(signUpBut);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        return UiUtils.createScrollView(context, signinLayout,
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
    }

    public View createSignUpLayout() {
        int bgColor = Color.WHITE, padding = 80, width = 800;

        LinearLayout signinLayout = new LinearLayout(this);
        signinLayout.setGravity(Gravity.CENTER_VERTICAL
                | Gravity.CENTER_HORIZONTAL);
        signinLayout.setBackgroundColor(bgColor);
        signinLayout.setOrientation(LinearLayout.VERTICAL);
        signinLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        signUpFNField = UiUtils.createEditText(context, "Firstname", "", true,
                true, Color.rgb(255, 249, 232), textSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        signUpFNField.setWidth(width);
        signinLayout.addView(signUpFNField);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        signUpLNField = UiUtils.createEditText(context, "Lastname", "", true,
                true, Color.rgb(255, 249, 232), textSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        signUpLNField.setWidth(width);
        signinLayout.addView(signUpLNField);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        signUpUNField = UiUtils.createEditText(context, "Username", "", true,
                true, Color.rgb(255, 249, 232), textSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        signUpUNField.setWidth(width);
        signinLayout.addView(signUpUNField);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        signUpPField = UiUtils.createEditText(context, "Password", "", true,
                true, Color.rgb(255, 249, 232), textSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        signUpPField.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        signUpPField.setWidth(width);
        signinLayout.addView(signUpPField);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        Button signUpBut = UiUtils.createButton(context, "SIGN UP",
                new Runnable() {

                    @Override
                    public void run() {
                        User user = new User();
                        user.setFirstname("" + signUpFNField.getText());
                        user.setLastname("" + signUpLNField.getText());
                        user.setUsername("" + signUpUNField.getText());
                        user.setPassword("" + signUpPField.getText());
                        try {
                            user = mobileSdk.signup(user);
                            Intent intent = new Intent(getApplicationContext(),
                                    DashboardActivity.class);
                            intent.putExtra(
                                    "UserPresentation",
                                    "" + user.getFirstname() + " "
                                            + user.getLastname());
                            startActivityForResult(intent, 0);
                        } catch (IOException e) {
                            UiUtils.makeToast(context, "IOException",
                                    mainLayout);
                        } catch (URISyntaxException e) {
                            UiUtils.makeToast(context, "URISyntaxException",
                                    mainLayout);
                        } catch (JSONException e) {
                            UiUtils.makeToast(context, "JSONException",
                                    mainLayout);
                        } catch (NotOkResponseException e) {
                            UiUtils.makeToast(context, e.getMessage(),
                                    mainLayout);
                        } catch (Exception e) {
                            UiUtils.makeToast(context, DebugUtils.serializeException(e), mainLayout);
                        }
                    }
                }, true, Color.rgb(242, 247, 255), ButtonColor, textSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        signUpBut.setWidth(width);
        signinLayout.addView(signUpBut);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        Button backBut = UiUtils.createButton(context, "Return",
                new Runnable() {

                    @Override
                    public void run() {
                        changePage(true);
                    }
                }, true, Color.WHITE, ButtonColor, smallTextSize,
                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
        backBut.setWidth(width);
        signinLayout.addView(backBut);
        signinLayout.addView(UiUtils.createSpace(context, 10, padding));

        return UiUtils.createScrollView(context, signinLayout,
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mainLayout.indexOfChild(signUpLayout) >= 0) {
                changePage(true);
            } else {
                long ct = System.currentTimeMillis();
                if (ct - backKeyPressTime > 3000) {
                    backKeyPressTime = ct;
                    UiUtils.makeToast(context, "Tap again to exit " + appName,
                            mainLayout);
                } else {
                    finish();
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            finish();
        }
    }

    public void changePage(final boolean showSignIn) {
        mainLayout.post(new Runnable() {

            @Override
            public void run() {
                signInUNField.setText("");
                signInPField.setText("");
                signUpFNField.setText("");
                signUpLNField.setText("");
                signUpUNField.setText("");
                signUpPField.setText("");
                if (showSignIn) {
                    mainLayout.removeView(signUpLayout);
                    mainLayout.addView(signInLayout);
                } else {
                    mainLayout.removeView(signInLayout);
                    mainLayout.addView(signUpLayout);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }

    private void destroy() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mobileSdk.signout();
            }
        }).start();
    }
}
