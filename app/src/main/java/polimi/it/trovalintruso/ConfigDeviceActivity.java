package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;

/**
 * Created by poool on 09/03/15.
 */
public class ConfigDeviceActivity extends Activity {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String NAME_PATTERN = "[a-zA-Z_0-9]+";

    private Pattern email_pattern;
    private Matcher email_matcher;
    private Pattern name_pattern;
    private Matcher name_matcher;
    private SharedPreferences sharedPref;

    @InjectView(R.id.email_edit_text)
    EditText email_edit;

    @InjectView(R.id.name_edit_text)
    EditText name_edit;

    @InjectView(R.id.account_email_edit_text)
    EditText account_email_edit;

    @InjectView(R.id.account_password_edit_text)
    EditText account_password_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_device);
        ButterKnife.inject(this);
        email_pattern = Pattern.compile(EMAIL_PATTERN);
        name_pattern = Pattern.compile(NAME_PATTERN);
        sharedPref = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        email_edit.setText(sharedPref.getString("email", ""));
        name_edit.setText(sharedPref.getString("dev_name", ""));
        account_email_edit.setText(sharedPref.getString("account_email", ""));
        account_password_edit.setText(sharedPref.getString("account_password", ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.gameHelper.restartDiscovery();
    }

    @OnTextChanged(R.id.email_edit_text)
    void EmailChanged(CharSequence s, int start, int before, int count) {
        email_matcher = email_pattern.matcher(s.toString());
        checkEmail(s.toString());
    }

    private void checkEmail(String s) {
        email_matcher = email_pattern.matcher(s.trim());
        if(email_matcher.matches()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("email", s.trim());
            editor.commit();
        }
        else {
        }
    }

    @OnTextChanged(R.id.name_edit_text)
    void NameChanged(CharSequence s, int start, int before, int count) {
        checkName(s.toString());
    }

    private void checkName(String s) {
        name_matcher = name_pattern.matcher(s);
        if(name_matcher.matches()) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("dev_name", s);
            editor.commit();
        }
        else {
        }
    }

    @OnTextChanged(R.id.account_password_edit_text) void passwordChanged(CharSequence s, int start, int before, int count) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("account_password", s.toString());
        editor.commit();
    }

    @OnTextChanged(R.id.account_email_edit_text) void accountChanged(CharSequence s, int start, int before, int count) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("account_email", s.toString());
        editor.commit();
    }
}
