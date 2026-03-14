package com.isaacpiscopo.wifinote.ui.backup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.isaacpiscopo.wifinote.databinding.FragmentBackupBinding;

/** Fragment displayed when the user navigates to the Backup tab. */
public class BackupFragment extends Fragment {

    private FragmentBackupBinding binding;

    /** Inflates the fragment layout and initialises View Binding. */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        new ViewModelProvider(this).get(BackupViewModel.class);
        binding = FragmentBackupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /** Releases the View Binding reference to avoid memory leaks. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
