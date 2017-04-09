package com.vixir.finalproject.perfectday;

import android.app.Dialog;
import android.content.DialogInterface;
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

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ItemPickerDialogFragment extends DialogFragment {

    private static final String ITEM_COLOR = "item_color";
    @BindColor(R.color.picker_red)
    int pickerRed;
    @BindColor(R.color.picker_amber)
    int pickerAmber;
    @BindColor(R.color.picker_teal)
    int pickerTeal;
    @BindColor(R.color.picker_blue)
    int pickerBlue;
    @BindColor(R.color.picker_orange)
    int pickerOrange;
    @BindColor(R.color.picker_yellow)
    int pickerYellow;

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
        Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Montserrat-Bold.ttf");
        mdialogTitle.setTypeface(custom_font);
        mSpectrumPalette.setColors(new int[]{pickerAmber, pickerBlue, pickerOrange, pickerRed, pickerTeal, pickerYellow});
        mDescriptionInput.setSelected(true);
        mSpectrumPalette.setSelectedColor(pickerAmber);
        mSpectrumPalette.setHorizontalScrollBarEnabled(true);
        mSpectrumPalette.setFixedColumnCount(20);
        if (getTargetRequestCode() == EDIT_ITEM) {
            mdialogTitle.setText(R.string.edit_dialog_title);
        }
        if (!task.equals("")) {
            mDescriptionInput.setText(task);
        }
        if (selectedColor == 0) {
            //Math.random() here
            selectedColor = pickerOrange;
        }
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