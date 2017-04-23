package com.vixir.finalproject.perfectday.customdialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.thebluealliance.spectrum.SpectrumPalette;
import com.vixir.finalproject.perfectday.R;
import com.vixir.finalproject.perfectday.utils.DialogUtils;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ItemPickerDialogFragment extends DialogFragment {

    private static final String ITEM_COLOR = "item_color";

    @BindView(R.id.task_description_input)
    MaterialEditText mDescriptionInput;
    @BindView(R.id.title_dialog)
    protected TextView mdialogTitle;
    public static final Integer EDIT_ITEM = 200;
    public static final Integer NEW_ITEM = 300;
    public static final String ITEM_TASK = "itemName";
    @BindView(R.id.color_picker)
    SpectrumPalette mSpectrumPalette;

    private static String task = "";
    private static int selectedColor = Color.WHITE;
    private static int taskId;

    public static ItemPickerDialogFragment newInstance(int id, String hint, int color) {
        ItemPickerDialogFragment pickerDialogFragment = new ItemPickerDialogFragment();
        Bundle args = new Bundle();
        taskId = id;
        task = hint;
        selectedColor = color;
        pickerDialogFragment.setArguments(args);
        return pickerDialogFragment;
    }

    public interface EditTaskDialogListener {
        void onFinishedCreateDialog(String inputText, int color);

        void onFinishedEditDialog(int id, String inputText, int color);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = getActivity().getLayoutInflater().inflate(R.layout.task_item_picker, container);
        ButterKnife.bind(this, mView);
        int[] rainbow = getActivity().getResources().getIntArray(R.array.picker_color);
        mSpectrumPalette.setColors(rainbow);
        mSpectrumPalette.setHorizontalScrollBarEnabled(true);
        mSpectrumPalette.setFixedColumnCount(17);
        mDescriptionInput.setSelected(true);
        if (getTargetRequestCode() == EDIT_ITEM) {
            mdialogTitle.setText(R.string.edit_dialog_title);
            mdialogTitle.setHintTextColor(selectedColor);
        }
        if (!task.equals("")) {
            mDescriptionInput.setText(task);
        }
        if (getTargetRequestCode() != EDIT_ITEM) {
            selectedColor = rainbow[new Random().nextInt(rainbow.length)];
            mDescriptionInput.setHint(DialogUtils.getHabitHint());
        }

        mSpectrumPalette.setSelectedColor(selectedColor);
        mDescriptionInput.setTextColor(selectedColor);
        mSpectrumPalette.setOnColorSelectedListener(new SpectrumPalette.OnColorSelectedListener() {
            @Override
            public void onColorSelected(@ColorInt int color) {
                selectedColor = color;
                mDescriptionInput.setTextColor(color);
            }
        });
        return mView;
    }


    @OnClick(R.id.create_ok_button)
    protected void onOkPressed() {
        if (!mDescriptionInput.isCharactersCountValid()) {
            View layout = getActivity().getLayoutInflater().inflate(R.layout.custom_toast_view,
                    (ViewGroup) getActivity().findViewById(R.id.custom_toast_container));
            TextView text = (TextView) layout.findViewById(R.id.text);
            if (mDescriptionInput.getText().toString().trim().equals("")) {
                text.setText(R.string.empty_task);
            } else {
                text.setText(R.string.invalid_task);
            }
            Toast toast = new Toast(getActivity());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            return;
        }
        if (getTargetRequestCode() == NEW_ITEM) {
            sendBackCreateResult();
        }
        if (getTargetRequestCode() == EDIT_ITEM) {
            sendBackEditResult();
        }
        dismiss();
    }

    private void sendBackEditResult() {
        EditTaskDialogListener editTaskDialogListener = (EditTaskDialogListener) getTargetFragment();
        editTaskDialogListener.onFinishedEditDialog(taskId, mDescriptionInput.getText().toString(), selectedColor);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDescriptionInput.requestFocus();
        if (null != getDialog().getWindow()) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }


    public void sendBackCreateResult() {
        EditTaskDialogListener editTaskDialogListener = (EditTaskDialogListener) getTargetFragment();
        editTaskDialogListener.onFinishedCreateDialog(mDescriptionInput.getText().toString(), selectedColor);
    }

}
