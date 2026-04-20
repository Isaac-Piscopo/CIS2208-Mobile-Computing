package com.isaacpiscopo.wifinote.ui.networks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isaacpiscopo.wifinote.R;
import com.isaacpiscopo.wifinote.model.Network;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter that binds a list of {@link Network} records to card views.
 * Short-press navigates to the QR detail screen; long-press opens the edit screen.
 */
public class NetworksAdapter extends RecyclerView.Adapter<NetworksAdapter.ViewHolder> {

    /** Callback for short-press (QR detail) events. */
    public interface OnItemClickListener {
        /** Called when the user short-presses a network card. */
        void onItemClick(Network network);
    }

    /** Callback for long-press (edit) events. */
    public interface OnItemLongClickListener {
        /** Called when the user long-presses a network card. */
        void onItemLongClick(Network network);
    }

    private List<Network> networks = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    /** Constructs the adapter with click and long-click callbacks. */
    public NetworksAdapter(OnItemClickListener clickListener,
                           OnItemLongClickListener longClickListener) {
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    /** Replaces the current list and notifies the RecyclerView of the change. */
    public void setNetworks(List<Network> networks) {
        this.networks = networks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_network, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Network network = networks.get(position);
        holder.textSsid.setText(network.getSsid());
        holder.textSecurity.setText(network.getSecurity());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(network);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.onItemLongClick(network);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return networks.size();
    }

    /**
     * ViewHolder that caches the views for a single network card item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /** Displays the WiFi network name (SSID). */
        final TextView textSsid;

        /** Displays the security type. */
        final TextView textSecurity;

        /** Constructs the ViewHolder and binds view references. */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSsid = itemView.findViewById(R.id.text_ssid);
            textSecurity = itemView.findViewById(R.id.text_security);
        }
    }
}
