package com.isaacpiscopo.wifinote;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.isaacpiscopo.wifinote.data.DbHelper;
import com.isaacpiscopo.wifinote.databinding.ActivityEditNetworkBinding;
import com.isaacpiscopo.wifinote.model.Network;

/**
 * Full-screen Activity for adding a new network or editing an existing one.
 * Launch with no extras for add mode; pass a {@link Network} via
 * {@link #EXTRA_NETWORK} for edit mode.
 */
public class EditNetworkActivity extends AppCompatActivity {

    /** Intent extra key for passing a {@link Network} to edit. */
    public static final String EXTRA_NETWORK = "extra_network";

    private ActivityEditNetworkBinding binding;
    private DbHelper dbHelper;
    private Network networkToEdit;

    /** Inflates the layout, resolves add/edit mode, and populates fields. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNetworkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DbHelper(this);

        // Security type dropdown
        String[] securityOptions = {
                getString(R.string.security_wpa2),
                getString(R.string.security_wpa3),
                getString(R.string.security_open)
        };
        ArrayAdapter<String> securityAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, securityOptions);
        binding.spinnerSecurity.setAdapter(securityAdapter);
        binding.spinnerSecurity.setText(securityOptions[0], false);

        // Determine add vs edit mode
        networkToEdit = (Network) getIntent().getSerializableExtra(EXTRA_NETWORK);
        if (networkToEdit != null) {
            binding.toolbarEdit.setTitle(getString(R.string.edit_network_title_edit));
            binding.editSsid.setText(networkToEdit.getSsid());
            binding.editPassword.setText(networkToEdit.getPassword());
            binding.spinnerSecurity.setText(networkToEdit.getSecurity(), false);
        }

        // Toolbar back navigation cancels without saving
        binding.toolbarEdit.setNavigationOnClickListener(v -> finish());

        binding.btnCancel.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> attemptSave());
    }

    /**
     * Validates input fields and persists the network if valid.
     * Shows inline errors via TextInputLayout on validation failure.
     */
    private void attemptSave() {
        String ssid = binding.editSsid.getText() != null
                          ? binding.editSsid.getText().toString().trim() : "";
        String password = binding.editPassword.getText() != null
                          ? binding.editPassword.getText().toString() : "";
        String security = binding.spinnerSecurity.getText().toString();

        boolean valid = true;

        // Validate SSID
        binding.layoutSsid.setError(null);
        if (ssid.isEmpty()) {
            binding.layoutSsid.setError(getString(R.string.error_ssid_empty));
            valid = false;
        } else if (ssid.length() > 32) {
            binding.layoutSsid.setError(getString(R.string.error_ssid_too_long));
            valid = false;
        }

        // Validate password -- required for WPA2 / WPA3
        binding.layoutPassword.setError(null);
        boolean requiresPassword = !security.equals(getString(R.string.security_open));
        if (requiresPassword && password.length() < 8) {
            binding.layoutPassword.setError(getString(R.string.error_password_too_short));
            valid = false;
        }

        if (!valid) {
            return;
        }

        if (networkToEdit == null) {
            // Add mode
            dbHelper.insertNetwork(new Network(ssid, password, security));
        } else {
            // Edit mode
            networkToEdit.setSsid(ssid);
            networkToEdit.setPassword(password);
            networkToEdit.setSecurity(security);
            dbHelper.updateNetwork(networkToEdit);
        }

        setResult(RESULT_OK);
        finish();
    }
}
