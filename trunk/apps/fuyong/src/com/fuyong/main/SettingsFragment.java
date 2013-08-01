package com.fuyong.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-7-11
 * Time: 下午10:25
 * To change this template use File | Settings | File Templates.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Preference mSelectedPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String setting = getArguments().getString(getString(R.string.header_name));
        if (setting.equals(getString(R.string.wifi))) {
            addPreferencesFromResource(R.xml.settings_wifi);
        } else if (setting.equals(getString(R.string.data_upload))) {
            addPreferencesFromResource(R.xml.settings_data_upload);
        } else if (setting.equals(getString(R.string.app_name))) {
            addPreferencesFromResource(R.xml.preferences);
        }
        updateEditTextPreferenceSummery();
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //To change body of implemented methods use File | Settings | File Templates.

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void afterTextChanged(Editable s) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    private void updateEditTextPreferenceSummery() {
        int count = getPreferenceScreen().getPreferenceCount();
        for (int index = 0; index < count; ++index) {
            Preference preference = getPreferenceScreen().getPreference(index);
            if (preference instanceof EditTextPreference) {
                preference.setSummary(((EditTextPreference) preference).getText());
                ((EditTextPreference) preference).getEditText().addTextChangedListener(mTextWatcher);
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        mSelectedPreference = preference;
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference instanceof EditTextPreference) {
            preference.setSummary(((EditTextPreference) preference).getText());
        }
    }
}
