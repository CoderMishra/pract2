package com.nashlincoln.blink.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.content.DeviceLoader;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.nfc.NfcUtils;

import java.util.List;



/**
 * Created by nash on 10/19/14.
 */
public class DeviceListFragment extends BlinkListFragment {
    private static final String TAG = "DeviceListFragment";
    private DeviceAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new DeviceAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        getLoaderManager().restartLoader(0, null, mLoaderCallbacks);
    }

    @Override
    protected void onFabClick(View view) {
        Intent intent = new Intent(getActivity(), EditListActivity.class);
        intent.putExtra(BlinkApp.EXTRA_TYPE, BlinkApp.TYPE_DEVICE_TYPE);
        startActivity(intent);
    }

    private LoaderManager.LoaderCallbacks<List<Device>> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Device>>() {
        @Override
        public Loader<List<Device>> onCreateLoader(int i, Bundle bundle) {
            Log.d(TAG, "onCreateLoader");
            return new DeviceLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<List<Device>> listLoader, List<Device> devices) {
            Log.d(TAG, "onLoadFinished");
            mAdapter.clear();
            if (devices != null) {
                mAdapter.addAll(devices);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Device>> listLoader) {
            Log.d(TAG, "onLoaderReset");
        }
    };

    class DeviceAdapter extends ArrayAdapter<Device> {

        public DeviceAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.device_item, null);
                holder = new Holder(convertView);
            } else {
                holder = (Holder) convertView.getTag();
            }

            bindView(position, holder);

            return convertView;
        }

        private void bindView(int position, Holder holder) {
            Device device = getItem(position);
            holder.position = position;
            holder.textView.setText(device.getName());
            holder.selfChange = true;
            holder.toggleView.setChecked(device.isOn());
            holder.seekBar.setProgress(device.getLevel());
            holder.selfChange = false;
        }

        class Holder implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {
            ImageButton settingsButton;
            boolean selfChange;
            int position;
            TextView textView;
            SwitchCompat toggleView;
            SeekBar seekBar;

            Holder(View view) {
                view.setTag(this);
                textView = (TextView) view.findViewById(R.id.text);
                toggleView = (SwitchCompat) view.findViewById(R.id.toggle);
                seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
                settingsButton = (ImageButton) view.findViewById(R.id.button_settings);
                settingsButton.setOnClickListener(this);
                seekBar.setMax(255);
                toggleView.setOnCheckedChangeListener(this);
                seekBar.setOnSeekBarChangeListener(this);
            }

            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                if (selfChange) {
                    return;
                }
                Device device = getItem(position);
                device.setOn(b);
                Syncro.getInstance().syncDevices();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (selfChange) {
                    return;
                }
                Device device = getItem(position);
                device.setLevel(seekBar.getProgress());
                Syncro.getInstance().syncDevices();
            }

            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater().inflate(R.menu.device_item, popup.getMenu());
                popup.setOnMenuItemClickListener(this);
                popup.show();
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Device device;
                switch (item.getItemId()) {

                    case R.id.action_edit:
                        device = getItem(position);
                        DialogFragment fragment = EditNameDialogFragment.newInstance(device.getId());
                        fragment.show(getFragmentManager(), "edit");
                        break;

                    case R.id.action_remove:
                        device = getItem(position);
                        device.setState(BlinkApp.STATE_REMOVED);
                        Syncro.getInstance().syncDevices();
                        break;

                    case R.id.action_write_nfc:
                        device = getItem(position);
                        NfcUtils.stageWrite(getActivity(), device.toNfc());
                        break;
                }
                return false;
            }
        }
    }
}
