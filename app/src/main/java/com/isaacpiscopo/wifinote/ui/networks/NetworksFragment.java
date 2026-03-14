package com.isaacpiscopo.wifinote.ui.networks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.isaacpiscopo.wifinote.databinding.FragmentNetworksBinding;

/** Fragment displayed when the user navigates to the Networks tab. */
public class NetworksFragment extends Fragment {

    private FragmentNetworksBinding binding;

    /** Inflates the fragment layout and initialises View Binding. */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        new ViewModelProvider(this).get(NetworksViewModel.class);
        binding = FragmentNetworksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /** Releases the View Binding reference to avoid memory leaks. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
