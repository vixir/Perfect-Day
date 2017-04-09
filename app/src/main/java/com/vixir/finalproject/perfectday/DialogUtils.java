package com.vixir.finalproject.perfectday;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import static com.vixir.finalproject.perfectday.ItemPickerDialogFragment.EDIT_ITEM;

public class DialogUtils {


    public static void callEditDialog(FragmentActivity activity, Fragment fragment, int id, String description, int color) {
        FragmentManager fm = activity.getSupportFragmentManager();
        ItemPickerDialogFragment pickTaskName = ItemPickerDialogFragment.newInstance(id, description, color);
        pickTaskName.setTargetFragment(fragment, EDIT_ITEM);
        pickTaskName.show(fm, "edit_item");
    }


}
