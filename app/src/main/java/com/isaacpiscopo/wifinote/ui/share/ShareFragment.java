package com.isaacpiscopo.wifinote.ui.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.isaacpiscopo.wifinote.R;
import com.isaacpiscopo.wifinote.data.DbHelper;
import com.isaacpiscopo.wifinote.databinding.FragmentShareBinding;
import com.isaacpiscopo.wifinote.model.Network;
import com.isaacpiscopo.wifinote.ui.networks.SelectableNetworksAdapter;

import java.util.List;

/**
 * Fragment for selecting WiFi networks and sharing their credentials as plain text
 * via the system share sheet ({@link Intent#ACTION_SEND}).
 */
public class ShareFragment extends Fragment {

    private FragmentShareBinding binding;
    private SelectableNetworksAdapter adapter;
    private DbHelper dbHelper;

    /** Inflates the fragment layout and wires the RecyclerView and share button. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShareBinding.inflate(inflater, container, false);
        dbHelper = new DbHelper(requireContext());
        adapter = new SelectableNetworksAdapter();

        adapter.setOnSelectionChangedListener(new SelectableNetworksAdapter.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(int count) {
                updateShareButton(count);
            }
        });

        binding.recyclerShare.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerShare.setAdapter(adapter);

        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareSelected();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Network> networks = dbHelper.getAllNetworks();
        adapter.setNetworks(networks);

        boolean empty = networks.isEmpty();
        binding.textShareEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerShare.setVisibility(empty ? View.GONE : View.VISIBLE);
        updateShareButton(0);
    }

    /**
     * Updates the share button label and enabled state to reflect the current selection count.
     *
     * @param count the number of selected items.
     */
    private void updateShareButton(int count) {
        binding.btnShare.setEnabled(count > 0);
        if (count == 0) {
            binding.btnShare.setText(getString(R.string.share_button_label));
        } else {
            binding.btnShare.setText(getString(R.string.share_button_label_n, count));
        }
    }

    /**
     * Builds a human-readable text payload from the selected networks and launches
     * the system share sheet via {@link Intent#ACTION_SEND}.
     */
    private void shareSelected() {
        List<Network> selected = adapter.getSelectedNetworks();
        if (selected.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.share_text_header)).append("\n\n");

        for (Network network : selected) {
            sb.append("SSID: ").append(network.getSsid()).append("\n");
            sb.append("Security: ").append(network.getSecurity()).append("\n");
            if (!network.getPassword().isEmpty()) {
                sb.append("Password: ").append(network.getPassword()).append("\n");
            }
            sb.append("\n");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString().trim());

        startActivity(Intent.createChooser(shareIntent, null));
        adapter.clearSelection();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
