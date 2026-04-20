package com.isaacpiscopo.wifinote.ui.networks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isaacpiscopo.wifinote.R;
import com.isaacpiscopo.wifinote.model.Network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RecyclerView adapter supporting multi-selection for Backup and Share fragments.
 * Selection state is tracked by network id.
 */
public class SelectableNetworksAdapter
        extends RecyclerView.Adapter<SelectableNetworksAdapter.ViewHolder> {

    /** Callback fired whenever the selection count changes. */
    public interface OnSelectionChangedListener {
        /**
         * Called when the selection changes.
         *
         * @param count the number of currently selected items.
         */
        void onSelectionChanged(int count);
    }

    private List<Network> networks = new ArrayList<>();
    private final Set<Long> selectedIds = new HashSet<>();
    private OnSelectionChangedListener selectionListener;

    /**
     * Replaces the dataset and clears any existing selection.
     *
     * @param networks the new list of networks to display.
     */
    public void setNetworks(List<Network> networks) {
        this.networks = networks;
        selectedIds.clear();
        notifyDataSetChanged();
    }

    /**
     * Sets the listener notified when the selection count changes.
     *
     * @param listener the callback to register.
     */
    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    /**
     * Returns a list of networks whose ids are in the current selection.
     *
     * @return selected networks; may be empty.
     */
    public List<Network> getSelectedNetworks() {
        List<Network> selected = new ArrayList<>();
        for (Network n : networks) {
            if (selectedIds.contains(n.getId())) {
                selected.add(n);
            }
        }
        return selected;
    }

    /** Returns the number of currently selected items. */
    public int getSelectedCount() {
        return selectedIds.size();
    }

    /** Clears all selections and notifies the listener with count 0. */
    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(0);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selectable_network, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Network network = networks.get(position);
        holder.textSsid.setText(network.getSsid());
        holder.textSecurity.setText(network.getSecurity());
        holder.checkBox.setChecked(selectedIds.contains(network.getId()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_ID) return;
                Network n = networks.get(pos);
                if (selectedIds.contains(n.getId())) {
                    selectedIds.remove(n.getId());
                } else {
                    selectedIds.add(n.getId());
                }
                notifyItemChanged(pos);
                if (selectionListener != null) {
                    selectionListener.onSelectionChanged(selectedIds.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return networks.size();
    }

    /** ViewHolder for a selectable network item. */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /** Checkbox indicating selection state. */
        final CheckBox checkBox;
        /** Primary label showing the network SSID. */
        final TextView textSsid;
        /** Secondary label showing the security type. */
        final TextView textSecurity;

        /**
         * Constructs a ViewHolder by binding views from the given item view.
         *
         * @param itemView the inflated item layout.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_select);
            textSsid = itemView.findViewById(R.id.text_ssid);
            textSecurity = itemView.findViewById(R.id.text_security);
        }
    }
}
