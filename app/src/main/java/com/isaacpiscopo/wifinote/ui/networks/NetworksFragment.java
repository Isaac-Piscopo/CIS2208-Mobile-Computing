package com.isaacpiscopo.wifinote.ui.networks;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.isaacpiscopo.wifinote.EditNetworkActivity;
import com.isaacpiscopo.wifinote.QrDetailActivity;
import com.isaacpiscopo.wifinote.databinding.FragmentNetworksBinding;
import com.isaacpiscopo.wifinote.model.Network;

/**
 * Fragment displaying the list of saved WiFi networks.
 * Hosts a RecyclerView with a FAB for adding networks.
 * Short-press on a card navigates to the QR detail screen (placeholder).
 * Long-press on a card navigates to the edit screen (placeholder).
 */
public class NetworksFragment extends Fragment {

    private FragmentNetworksBinding binding;
    private NetworksViewModel viewModel;
    private NetworksAdapter adapter;

    /** Inflates the fragment layout and initialises View Binding. */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNetworksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /** Wires the Toolbar, RecyclerView, Adapter, FAB, and observes the ViewModel. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar menu
        binding.toolbarNetworks.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == com.isaacpiscopo.wifinote.R.id.action_scan_qr) {
                    Toast.makeText(requireContext(), "Scan QR -- coming in M6", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        // Adapter
        adapter = new NetworksAdapter(
                new NetworksAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Network network) {
                        // Short-press: QR detail -- wired in M6
                        Intent intent = new Intent(requireActivity(), QrDetailActivity.class);
                        intent.putExtra(QrDetailActivity.EXTRA_NETWORK, network);
                        startActivity(intent);
                    }
                },
                new NetworksAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(Network network) {
                        // Long-press: edit -- wired in M5
                        Intent intent = new Intent(requireActivity(), EditNetworkActivity.class);
                        intent.putExtra(EditNetworkActivity.EXTRA_NETWORK, network);
                        startActivity(intent);
                    }
                }
        );

        // RecyclerView
        binding.recyclerNetworks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerNetworks.setAdapter(adapter);

        // FAB
        binding.fabAddNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add network -- wired in M5
                Intent intent = new Intent(requireActivity(), EditNetworkActivity.class);
                startActivity(intent);
            }
        });

        // ViewModel
        viewModel = new ViewModelProvider(this).get(NetworksViewModel.class);
        viewModel.getNetworks().observe(getViewLifecycleOwner(), networks -> {
            adapter.setNetworks(networks);
            binding.textEmpty.setVisibility(networks.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    /** Refreshes the list when the fragment resumes (e.g. after returning from edit). */
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refresh();
        }
    }

    /** Releases the View Binding reference to avoid memory leaks. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
