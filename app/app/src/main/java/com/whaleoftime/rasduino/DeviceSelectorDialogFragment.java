package com.whaleoftime.rasduino;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;

public class DeviceSelectorDialogFragment extends DialogFragment {
	MainActivity mainActivity;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		CharSequence devices[] = new CharSequence[]{"Living room"};
		builder.setTitle("Please pick a device to use")
				.setItems(devices, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mainActivity.wasSelected();
						// The 'which' argument contains the index position
						// of the selected item
					}
				});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mainActivity = (MainActivity) context;
	}
}
