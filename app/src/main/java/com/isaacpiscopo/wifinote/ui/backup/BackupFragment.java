package com.isaacpiscopo.wifinote.ui.backup;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.isaacpiscopo.wifinote.R;
import com.isaacpiscopo.wifinote.data.DbHelper;
import com.isaacpiscopo.wifinote.databinding.FragmentBackupBinding;
import com.isaacpiscopo.wifinote.model.Network;
import com.isaacpiscopo.wifinote.ui.networks.SelectableNetworksAdapter;
import com.isaacpiscopo.wifinote.util.QrUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Fragment for selecting and backing up WiFi networks as QR code images to the gallery.
 * Each selected network is encoded as a 512x512 PNG saved to Pictures/WifiNote.
 */
public class BackupFragment extends Fragment {

    private FragmentBackupBinding binding;
    private SelectableNetworksAdapter adapter;
    private DbHelper dbHelper;

    /** Inflates the fragment layout and wires the RecyclerView and backup button. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBackupBinding.inflate(inflater, container, false);
        dbHelper = new DbHelper(requireContext());
        adapter = new SelectableNetworksAdapter();

        adapter.setOnSelectionChangedListener(new SelectableNetworksAdapter.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(int count) {
                updateBackupButton(count);
            }
        });

        binding.recyclerBackup.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBackup.setAdapter(adapter);

        binding.btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupSelected();
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
        binding.textBackupEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerBackup.setVisibility(empty ? View.GONE : View.VISIBLE);
        updateBackupButton(0);
    }

    /**
     * Updates the backup button label and enabled state to reflect the current selection count.
     *
     * @param count the number of selected items.
     */
    private void updateBackupButton(int count) {
        binding.btnBackup.setEnabled(count > 0);
        if (count == 0) {
            binding.btnBackup.setText(getString(R.string.backup_button_label));
        } else {
            binding.btnBackup.setText(getString(R.string.backup_button_label_n, count));
        }
    }

    /**
     * Saves a QR PNG for each selected network to the device gallery via MediaStore.
     * Shows a toast with the saved count, or an error toast if all saves fail.
     */
    private void backupSelected() {
        List<Network> selected = adapter.getSelectedNetworks();
        if (selected.isEmpty()) return;

        int savedCount = 0;
        for (Network network : selected) {
            Bitmap bitmap = QrUtils.encodeWifiQr(network);
            if (bitmap == null) continue;

            String fileName = "wifinote_"
                    + network.getSsid().replaceAll("[^a-zA-Z0-9]", "_") + ".png";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WifiNote");

            Uri uri = requireContext().getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) continue;

            try (OutputStream out = requireContext().getContentResolver().openOutputStream(uri)) {
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    savedCount++;
                }
            } catch (IOException e) {
                // proceed to next network
            }
        }

        if (savedCount > 0) {
            Toast.makeText(requireContext(),
                    getString(R.string.backup_saved_n, savedCount),
                    Toast.LENGTH_SHORT).show();
            adapter.clearSelection();
        } else {
            Toast.makeText(requireContext(),
                    getString(R.string.backup_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
