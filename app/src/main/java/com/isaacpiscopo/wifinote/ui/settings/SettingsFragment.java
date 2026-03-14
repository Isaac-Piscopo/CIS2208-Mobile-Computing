package com.isaacpiscopo.wifinote.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.isaacpiscopo.wifinote.databinding.FragmentSettingsBinding;

/** Fragment displayed when the user navigates to the Settings tab. */
public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    /** Inflates the fragment layout and initialises View Binding. */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /** Releases the View Binding reference to avoid memory leaks. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
