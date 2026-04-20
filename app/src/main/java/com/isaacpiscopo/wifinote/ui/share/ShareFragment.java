package com.isaacpiscopo.wifinote.ui.share;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.isaacpiscopo.wifinote.R;
import com.isaacpiscopo.wifinote.api.PasteRepository;
import com.isaacpiscopo.wifinote.data.DbHelper;
import com.isaacpiscopo.wifinote.databinding.FragmentShareBinding;
import com.isaacpiscopo.wifinote.model.Network;
import com.isaacpiscopo.wifinote.ui.networks.SelectableNetworksAdapter;

import java.util.List;

/**
 * Fragment for selecting WiFi networks and sharing their credentials either via
 * the system share sheet ({@link Intent#ACTION_SEND}) or as a web paste link.
 */
public class ShareFragment extends Fragment {

    private FragmentShareBinding binding;
    private SelectableNetworksAdapter adapter;
    private DbHelper dbHelper;

    /** Handler for posting PasteRepository callbacks back to the main thread. */
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /** Inflates the fragment layout and wires the RecyclerView and action buttons. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding  = FragmentShareBinding.inflate(inflater, container, false);
        dbHelper = new DbHelper(requireContext());
        adapter  = new SelectableNetworksAdapter();

        adapter.setOnSelectionChangedListener(new SelectableNetworksAdapter.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(int count) {
                updateButtons(count);
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

        binding.btnShareWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareViaWeb();
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
        updateButtons(0);
    }

    /**
     * Updates both action buttons' labels and enabled state to reflect the selection count.
     *
     * @param count the number of selected items.
     */
    private void updateButtons(int count) {
        boolean enabled = count > 0;
        binding.btnShare.setEnabled(enabled);
        binding.btnShareWeb.setEnabled(enabled);

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
        String payload = buildPayload();
        if (payload == null) return;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, payload);

        startActivity(Intent.createChooser(shareIntent, null));
        adapter.clearSelection();
    }

    /**
     * Uploads the selected networks' credentials to paste.rs and shows the
     * resulting URL in a {@link MaterialAlertDialogBuilder} with a Copy action.
     * Falls back to a retry {@link Snackbar} on failure.
     */
    private void shareViaWeb() {
        String payload = buildPayload();
        if (payload == null) return;

        setWebShareInProgress(true);

        PasteRepository.getInstance().share(payload, new PasteRepository.ShareCallback() {
            @Override
            public void onSuccess(String url) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setWebShareInProgress(false);
                        if (getContext() == null) return;
                        adapter.clearSelection();
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(getString(R.string.share_web_dialog_title))
                                .setMessage(url)
                                .setPositiveButton(getString(R.string.share_web_copy),
                                        (dialog, which) -> copyToClipboard(url))
                                .setNegativeButton(android.R.string.cancel, null)
                                .show();
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setWebShareInProgress(false);
                        if (getView() == null) return;
                        Snackbar.make(getView(),
                                getString(R.string.share_web_failed),
                                Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.share_web_failed),
                                        v -> shareViaWeb())
                                .show();
                    }
                });
            }
        });
    }

    /**
     * Builds a human-readable plain-text payload from the current selection.
     *
     * @return the payload string, or {@code null} if nothing is selected.
     */
    @Nullable
    private String buildPayload() {
        List<Network> selected = adapter.getSelectedNetworks();
        if (selected.isEmpty()) return null;

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
        return sb.toString().trim();
    }

    /**
     * Shows or hides the progress indicator and disables the action buttons while a
     * web share is in flight.
     *
     * @param inProgress {@code true} to show the spinner and disable buttons.
     */
    private void setWebShareInProgress(boolean inProgress) {
        binding.progressShare.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        binding.btnShare.setEnabled(!inProgress && adapter.getSelectedCount() > 0);
        binding.btnShareWeb.setEnabled(!inProgress && adapter.getSelectedCount() > 0);
    }

    /**
     * Copies the given text to the system clipboard.
     *
     * @param text the text to copy.
     */
    private void copyToClipboard(String text) {
        ClipboardManager clipboard =
                (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("url", text));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
