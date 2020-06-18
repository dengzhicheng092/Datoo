package com.angopapo.datoo.auth;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.angopapo.datoo.R;
import com.angopapo.datoo.app.Application;
import com.angopapo.datoo.app.Config;
import com.angopapo.datoo.authUtils.AngopapoLoginConfig;
import com.angopapo.datoo.authUtils.AngopapoSignupActivity;
import com.angopapo.datoo.helpers.QuickHelp;
import com.angopapo.datoo.home.settings.WebUrlsActivity;
import com.angopapo.datoo.models.datoo.User;
import com.angopapo.datoo.utils.SharedPrefUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ui.login.ParseLoginFragmentBase;
import com.parse.ui.login.ParseOnLoadingListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Fragment for the user signup screen.
 */
public class SignupFragment extends ParseLoginFragmentBase {

    private EditText passwordField;
    private EditText emailField;
    private EditText nameField;
    private EditText mBirthday;
    private TextInputLayout mBirthdayWrapper;
    private Button createAccountButton;
    private Toolbar mToolbar;
    private TextView mTermsText;


    Date date;
    Date birthday;
    long Birthdate;
    int AgeString;
    private int year;
    private int month;
    private int day;
    String dateCheck = null;
    Integer ageInt;

    private AngopapoLoginConfig config;
    private int minPasswordLength;

    private static final String LOG_TAG = "SignupFragment";
    private static final int DEFAULT_MIN_PASSWORD_LENGTH = 6;
    private static final String USER_OBJECT_NAME_FIELD = "name";

    public static SignupFragment newInstance(Bundle configOptions) {
        SignupFragment signupFragment = new SignupFragment();
        Bundle args = new Bundle(configOptions);
        signupFragment.setArguments(args);
        return signupFragment;
    }

    protected String gender = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        config = AngopapoLoginConfig.fromBundle(args, getActivity());

        minPasswordLength = DEFAULT_MIN_PASSWORD_LENGTH;
        if (config.getParseSignupMinPasswordLength() != null) {
            minPasswordLength = config.getParseSignupMinPasswordLength();
        }

        View v = inflater.inflate(R.layout.include_email, parent, false);

        passwordField = v.findViewById(R.id.password);
        emailField = v.findViewById(R.id.Registration_emailOrPhone);
        nameField = v.findViewById(R.id.Registration_name);
        mBirthday = v.findViewById(R.id.Registration_birthday);
        mBirthdayWrapper = v.findViewById(R.id.Registration_birthdayWrapper);
        mToolbar = v.findViewById(R.id.toolbar);
        mTermsText = v.findViewById(R.id.Registration_termsAndConditions);

        setHasOptionsMenu(true);
        mToolbar.setOnMenuItemClickListener(menuItem -> false);

        mBirthday.setOnClickListener(v12 -> {

            DateSelect();
        });

        mBirthdayWrapper.setOnClickListener(v1 -> {


        });

        mBirthday.setOnFocusChangeListener((v13, hasFocus) -> {

            if (hasFocus){
                hideKeyboard(v13);
                DateSelect();
            }
        });


        setmTermsText(mTermsText);

        ((AngopapoSignupActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AngopapoSignupActivity)getActivity()).getSupportActionBar().setTitle("");
        ((AngopapoSignupActivity)getActivity()).getSupportActionBar().setElevation(3.0f);
        ((AngopapoSignupActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setBackgroundResource(R.color.transparent);

        gender = new SharedPrefUtil(getActivity()).getString(User.COL_GENDER, "");

        createAccountButton = v.findViewById(R.id.Registration_buttonCreateAccount);

        createAccountButton.setOnClickListener(v14 -> registerNow());

        return v;
    }

    public void DateSelect(){

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.DatePickerTheme, mDateSetListener,year,month,day);

        final Calendar calendar = Calendar.getInstance();
        final Calendar calendar2 = Calendar.getInstance();

        calendar2.add(Calendar.YEAR,-Config.MinimumAgeToRegister);

        dpd.getDatePicker().setMaxDate(calendar2.getTimeInMillis());

        calendar.add(Calendar.YEAR,-Config.MaximumAgeToRegister);

        // Set the Calendar new date as minimum date of date picker
        dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());

        dpd.show();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int mYear, int monthOfYear, int dayOfMonth) {

            year = mYear;
            month = monthOfYear;
            day = dayOfMonth;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,day);

            Date BirDate = calendar.getTime();

            Calendar dob = Calendar.getInstance();
            Calendar today = Calendar.getInstance();


            dob.set(year,month,day);

            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            ageInt = age;
            String ageS = ageInt.toString();

            date = BirDate;

            dateCheck = "true";


            Birthdate = BirDate.getTime();

            AgeString = ageInt;

            birthday = new Date(BirDate.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy "); // the format of your date

            String birthday = sdf.format(date);

            mBirthday.setText(String.format("%s (%s %s)", birthday, ageS, getString(R.string.year_of_age)));


        }

    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        final Activity activity = getActivity();

        if (activity instanceof ParseOnLoadingListener) {
            onLoadingListener = (ParseOnLoadingListener) activity;
        } else {
            throw new IllegalArgumentException(
                    "Activity must implemement ParseOnLoadingListener");
        }
    }

    private void setmTermsText(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                getString(R.string.signup_by_clinging) + " ");
        spanTxt.append(getString(R.string.signup_terms)).append(" ");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                QuickHelp.goToActivityWithNoClean(getActivity(), WebUrlsActivity.class, WebUrlsActivity.WEB_URL_TYPE, Config.TERMS_OF_USE);
            }
        }, spanTxt.length() - getString(R.string.signup_terms).length(), spanTxt.length(), 0);
        spanTxt.setSpan(new StyleSpan(Typeface.BOLD),spanTxt.length() - getString(R.string.signup_terms).length(), spanTxt.length(),0);

        spanTxt.append(getString(R.string.signup_we_will));

        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    public void registerNow(){

        if (QuickHelp.isInternetAvailable(Application.getInstance().getApplicationContext())){

            String password = passwordField.getText().toString().trim();

            String email = emailField.getText().toString().replaceAll(" ", "").trim();

            String name = nameField.getText().toString().trim();

            if (email.length() == 0 && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                emailField.setError(getString(R.string.com_parse_ui_no_email_toast));
            } else if (name.length() == 0 && config.isParseSignupNameFieldEnabled()) {

                nameField.setError(getString(R.string.com_parse_ui_no_name_toast));

            } else if (dateCheck == null) {

                mBirthday.setError(getString(R.string.signup_user_date));

            } else if (password.length() == 0) {

                passwordField.setError(getString(R.string.com_parse_ui_no_password_toast));
            } else if (password.length() < minPasswordLength) {

                passwordField.setError(getResources().getQuantityString(R.plurals.com_parse_ui_password_too_short_toast, minPasswordLength, minPasswordLength));

            } else if (gender == null){

                showToast(getString(R.string.gender_missing));
            } else {


                String[] parts = name.split(" ");

                String firstName = parts[0];

                String username = (name+firstName).toLowerCase().trim().replaceAll(" ", "");

                User user = ParseObject.create(User.class);

                user.setUid(QuickHelp.generateUId());
                user.setColBirthdate(birthday);
                user.setAge(ageInt);
                user.setColFirstName(firstName);
                user.setColFullName(name);
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setColGender(gender);
                user.setPopularity(0);
                user.setPrefMinAge(Config.MinimumAgeToRegister);
                user.setPrefMaxAge(Config.MaximumAgeToRegister);
                user.setPrefLocationIsNearBy(true);
                user.addCredit(Config.WelcomeCredit);
                user.setBio(Config.bio);
                user.setPasswordEnabled(true);

                loadingStart();
                user.signUpInBackground(e -> {
                    if (isActivityDestroyed()) {
                        return;
                    }

                    if (e == null) {
                        loadingFinish();
                        signupSuccessFbLink();

                    } else {
                        loadingFinish();
                        debugLog(getString(R.string.com_parse_ui_login_warning_parse_signup_failed) +
                                e.toString());
                        switch (e.getCode()) {
                            case ParseException.INVALID_EMAIL_ADDRESS:
                                showToast(R.string.com_parse_ui_invalid_email_toast);
                                emailField.setError(getString(R.string.com_parse_ui_invalid_email_toast));
                                break;
                            case ParseException.USERNAME_TAKEN:
                                showToast(R.string.com_parse_ui_username_taken_toast);
                                nameField.setError(getString(R.string.com_parse_ui_username_taken_toast));
                                break;
                            case ParseException.EMAIL_TAKEN:
                                showToast(R.string.com_parse_ui_email_taken_toast);
                                emailField.setError(getString(R.string.com_parse_ui_invalid_email_toast));
                                break;
                            default:
                                showToast(R.string.com_parse_ui_signup_failed_unknown_toast);
                        }
                    }
                });
            }

        } else {

            QuickHelp.noInternetConnect(getActivity());
        }

    }

    private void signupSuccessFbLink() {

        User parseUser = User.getUser();
        
        QuickHelp.saveInstallation(parseUser);

        Intent mainIntent = new Intent(getActivity(), FacebookLink.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Objects.requireNonNull(getActivity()).onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}

