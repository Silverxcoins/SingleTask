package com.example.sasha.singletask.choice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;
import com.example.sasha.singletask.helpers.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentTaskFragment extends Fragment implements DB.MarkTaskDeletedCallback, DB.GetTaskByIdCallback {

    private static final Logger logger = LoggerFactory.getLogger(CurrentTaskFragment.class);

    public static final String ACTION_TIME_LEFT_CHANGED = "action.TIME_LEFT_CHANGED";
    private static final String NAME_KEY = "name";
    private static final String COMMENT_KEY = "comment";
    private static final String TIME_KEY = "time";
    private static final String CURRENT_TASK_KEY = "currentTask";
    private static final String FROM_SERVICE_KEY = "isIntentFromService";

    private TextView textViewCurrentTaskFragment;
    private TextView taskNameTextView;
    private TextView taskCommentTextView;
    private TextView taskTimeTextView;
    private ImageView taskTimeIcon;

    private String taskName;

    BroadcastReceiver broadcastReceiver;

    private int postponeTime;
    private boolean isIntentFromService;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        logger.debug("onCreateView()");

        view = inflater.inflate(R.layout.fragment_current_task, null);

        textViewCurrentTaskFragment =
                ((TextView) view.findViewById(R.id.textViewCurrentTaskFragment));
        taskNameTextView = (TextView) view.findViewById(R.id.textViewCurrentTask);
        taskCommentTextView = (TextView) view.findViewById(R.id.textViewCurrentTaskComment);
        taskTimeTextView = (TextView) view.findViewById(R.id.textViewCurrentTaskTime);
        taskTimeIcon = (ImageView) view.findViewById(R.id.timeIcon);

        Animation animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        view.startAnimation(animationFadeIn);

        DB.getInstance(getActivity()).setMarkTaskDeletedCallback(this);
        DB.getInstance(getActivity()).setGetTaskByIdCallback(this);

        SharedPreferences settings =
                getActivity().getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        DB.getInstance(getActivity()).getTaskById(settings.getLong(CURRENT_TASK_KEY, 0));

        if (getActivity().getIntent().hasExtra(FROM_SERVICE_KEY)) {
            isIntentFromService = true;
        } else {
            isIntentFromService = false;
        }
        if (isIntentFromService) {
            getActivity().getIntent().removeExtra(FROM_SERVICE_KEY);
            textViewCurrentTaskFragment.setText(R.string.time_expired);
            taskTimeTextView.setVisibility(View.INVISIBLE);
            setButtons(true);
        } else {
            textViewCurrentTaskFragment.setText(R.string.task_executing);
            setButtons(false);
            registerBroadcastReceiver();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        logger.debug("onSaveInstanceState()");

        if (isAdded()) {
            outState.putString(NAME_KEY, taskNameTextView.getText().toString());
            if (!taskCommentTextView.getText().toString().isEmpty()) {
                outState.putString(COMMENT_KEY, taskCommentTextView.getText().toString());
            }
            outState.putString(TIME_KEY, taskTimeTextView.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void setButtons(boolean isIntentFromService) {
        Button firstButton = (Button) view.findViewById(R.id.firstButton);
        Button rejectButton = (Button) view.findViewById(R.id.rejectButton);
        Button doneButton = (Button) view.findViewById(R.id.doneButton);

        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(new Intent(getActivity(), TimeLeftService.class));
                updateCurrentTask(true);
            }
        });
        if (isIntentFromService) {
            taskTimeTextView.setVisibility(View.INVISIBLE);
            taskTimeIcon.setVisibility(View.INVISIBLE);
            rejectButton.setText(R.string.postpone);
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayTimeChoiceDialog();
                }
            });
        } else {
            taskTimeTextView.setVisibility(View.VISIBLE);
            taskTimeIcon.setVisibility(View.VISIBLE);
            rejectButton.setText(R.string.reject);
            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCurrentTask(false);
                    getActivity().stopService(new Intent(getActivity(), TimeLeftService.class));
                }
            });
        }
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentTask(true);
                getActivity().stopService(new Intent(getActivity(), TimeLeftService.class));
            }
        });
    }

    private void registerBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int time = intent.getIntExtra(TIME_KEY, 0);
                if (time != 0) {
                    taskTimeTextView.setText(Utils.getTimeAsString(time));
                } else {
                    textViewCurrentTaskFragment.setText(R.string.time_expired);
                    setButtons(true);
                    taskTimeTextView.setVisibility(View.INVISIBLE);
                    taskTimeIcon.setVisibility(View.INVISIBLE);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(ACTION_TIME_LEFT_CHANGED);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void updateCurrentTask(boolean isDeleted) {

        logger.debug("updateCurrentTask(" + isDeleted + ")");

        SharedPreferences settings =
                getActivity().getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        if (isDeleted) {
            DB.getInstance(getActivity()).markTaskDeleted(settings.getLong("currentTask", 0));
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("currentTask", 0);
        editor.putString("taskStart", null);
        editor.putString("lastUpdate", Utils.getCurrentTimeAsString());
        editor.apply();
        if (!isDeleted) {
            ((ChoiceActivity) getActivity()).onTaskUpdated();
        }
    }

    @Override
    public void onReceiveTaskById(TaskDataSet task) {

        logger.debug("onReceiveTaskById()");

        if (task != null) {
            taskName = task.getName();
            taskNameTextView.setText(taskName);
            taskCommentTextView.setText(task.getComment());
            int taskTime = task.getTime();
            int timeBetweenStartAndNow =
                    Utils.getMinutesBetweenStartTimeAndCurrentTime(getActivity());
            if (taskTime < timeBetweenStartAndNow) {
                textViewCurrentTaskFragment.setText(R.string.time_expired);
                setButtons(true);
            } else {
                taskTimeTextView.setText(Utils.getTimeAsString(taskTime - timeBetweenStartAndNow));
                textViewCurrentTaskFragment.setText(R.string.task_executing);
                setButtons(false);
            }
        }
    }

    @Override
    public void onMarkTaskDeleted(long taskId) {

        logger.debug("onMarkTaskDeleted()");

        ((ChoiceActivity) getActivity()).onTaskUpdated();
    }

    private void displayTimeChoiceDialog() {

        logger.debug("displayTimeChoiceDialog()");

        postponeTime = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_postpone_reject);
        builder.setPositiveButton(R.string.ok_btn_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                okButtonClicked();
            }
        });
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final NumberPicker hourNumberPicker =
                ((NumberPicker) dialog.findViewById(R.id.postponeNumberPickerHour));
        hourNumberPicker.setMinValue(0);
        hourNumberPicker.setMaxValue(23);

        final NumberPicker minuteNumberPicker =
                ((NumberPicker) dialog.findViewById(R.id.postponeNumberPickerMinute));
        minuteNumberPicker.setMinValue(0);
        minuteNumberPicker.setMaxValue(59);

        hourNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                onNumberPickersChanged(hourNumberPicker, minuteNumberPicker);
            }
        });
        minuteNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                onNumberPickersChanged(hourNumberPicker, minuteNumberPicker);
            }
        });

        CheckBox rejectCheckBox = (CheckBox) dialog.findViewById(R.id.checkBox);
        rejectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hourNumberPicker.setEnabled(false);
                    minuteNumberPicker.setEnabled(false);
                    postponeTime = 0;
                } else {
                    hourNumberPicker.setEnabled(true);
                    minuteNumberPicker.setEnabled(true);
                    onNumberPickersChanged(hourNumberPicker, minuteNumberPicker);
                }
            }
        });

    }

    void onNumberPickersChanged(NumberPicker hourNumberPicker, NumberPicker minuteNumberPicker) {

        logger.debug("onNumberPickerChanged()");

        int hours = hourNumberPicker.getValue();
        int minutes = minuteNumberPicker.getValue();
        postponeTime = hours * 60 + minutes;
    }

    void okButtonClicked() {

        logger.debug("okButtonClicked");

        if (postponeTime == 0) {
            updateCurrentTask(false);
            ((ChoiceActivity) getActivity()).onTaskUpdated();
        } else {
            updateCurrentTaskLastUpdate();
            SharedPreferences settings =
                    getActivity().getSharedPreferences(getString(R.string.PREFS_NAME), 0);
            DB.getInstance(getActivity()).updateTaskTime(settings.getLong(CURRENT_TASK_KEY, 0),
                    postponeTime);

            textViewCurrentTaskFragment.setText(R.string.task_executing);
            registerBroadcastReceiver();
            setButtons(false);

            taskTimeTextView.setText(Utils.getTimeAsString(postponeTime));

            Intent intent = new Intent(getActivity(), TimeLeftService.class);
            intent.putExtra(NAME_KEY, taskName);
            intent.putExtra(TIME_KEY, postponeTime);
            getActivity().startService(intent);
        }
    }

    void updateCurrentTaskLastUpdate() {

        logger.debug("updateCurrentTaskLastUpdate");

        SharedPreferences settings =
                getActivity().getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastUpdate", Utils.getCurrentTimeAsString());
        editor.apply();
    }

    @Override
    public void onStop() {
        logger.debug("onDestroy()");

        try {
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException | NullPointerException e) {
        }

        super.onStop();
    }

    @Override
    public void onStart() {
        registerBroadcastReceiver();
        super.onStart();
    }
}
