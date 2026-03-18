package com.isaacpiscopo.wifinote.ui.networks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.isaacpiscopo.wifinote.data.DbHelper;
import com.isaacpiscopo.wifinote.model.Network;

import java.util.List;

/**
 * ViewModel for the Networks list screen.
 * Holds the list of saved networks and reloads it from the database on request.
 */
public class NetworksViewModel extends AndroidViewModel {

    /** Live data list of all saved networks, observed by {@link NetworksFragment}. */
    private final MutableLiveData<List<Network>> networks = new MutableLiveData<>();

    private final DbHelper dbHelper;

    /** Constructs the ViewModel and performs the initial data load. */
    public NetworksViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DbHelper(application);
        refresh();
    }

    /** Returns the LiveData list of networks. */
    public MutableLiveData<List<Network>> getNetworks() {
        return networks;
    }

    /** Reloads all network records from the database and posts to LiveData. */
    public void refresh() {
        networks.setValue(dbHelper.getAllNetworks());
    }
}
