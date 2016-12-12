package com.example.sasha.singletask.choice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;
import com.example.sasha.singletask.helpers.Ui;
import com.example.sasha.singletask.helpers.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class ChosenTaskFragment extends Fragment implements DB.SelectTasksCallback {

    private static final Logger logger = LoggerFactory.getLogger(ChosenTaskFragment.class);

    private static final String NAME_KEY = "name";
    private static final String COMMENT_KEY = "comment";
    private static final String DATE_KEY = "date";
    private static final String TIME_KEY = "time";
    private static final String IS_TASK_FOUND_KEY = "isTaskFound";

    private TextView taskNameTextView;
    private TextView taskCommentTextView;
    private TextView taskTimeTextView;
    private TextView taskDateTextView;

    private long taskId;
    private String taskName;
    private int taskTime;

    private View dateIcon;
    private View taskCardContent;
    private View noApproachTasksLabel;

    private Animation animationFadeIn;
    private Animation animationFadeOut;

    private Bundle state;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        logger.debug("onCreateView()");

        view = inflater.inflate(R.layout.fragment_chosen_task, null);

        taskNameTextView = (TextView) view.findViewById(R.id.textViewChosenTask);
        taskCommentTextView = (TextView) view.findViewById(R.id.textViewChosenTaskComment);
        taskTimeTextView = (TextView) view.findViewById(R.id.textViewChosenTaskTime);
        taskDateTextView = (TextView) view.findViewById(R.id.textViewChosenTaskDate);
        dateIcon = view.findViewById(R.id.dateIcon);
        taskCardContent = view.findViewById(R.id.taskCardContent);
        noApproachTasksLabel = view.findViewById(R.id.noApproachTasks);

        animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);

        setButtonsClickListeners();

        DB.getInstance(getActivity()).setSelectTasksCallback(this);

        if (state != null) {
            if (state.containsKey(IS_TASK_FOUND_KEY)){
                taskCardContent.setVisibility(View.VISIBLE);
                noApproachTasksLabel.setVisibility(View.INVISIBLE);
                taskNameTextView.setText(state.getString(NAME_KEY));
                if (state.containsKey(COMMENT_KEY)) {
                    taskCommentTextView.setText(state.getString(COMMENT_KEY));
                }
                if (state.containsKey(DATE_KEY)) {
                    taskDateTextView.setText(state.getString(DATE_KEY));
                } else {
                    dateIcon.setVisibility(View.INVISIBLE);
                }
                taskTimeTextView.setText(state.getString(TIME_KEY));
            }
        } else {
            DB.getInstance(getActivity()
            ).selectTasks();
        }

        if (savedInstanceState != null) {
            state = new Bundle(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        logger.debug("onSaveInstanceState()");

        if (isAdded() && taskCardContent.getVisibility() == View.VISIBLE) {
            outState.putBoolean(IS_TASK_FOUND_KEY, true);
            outState.putString(NAME_KEY, taskNameTextView.getText().toString());
            if (!taskCommentTextView.getText().toString().isEmpty()) {
                outState.putString(COMMENT_KEY, taskCommentTextView.getText().toString());
            }
            if (!taskDateTextView.getText().toString().isEmpty()) {
                outState.putString(DATE_KEY, taskDateTextView.getText().toString());
            }
            outState.putString(TIME_KEY, taskTimeTextView.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void setButtonsClickListeners() {
        view.findViewById(R.id.buttonOther).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.startAnimation(animationFadeOut);
                Ui.run(new Runnable() {
                    @Override
                    public void run() {
                        onTasksSelectionFinished();
                    }
                });
                view.startAnimation(animationFadeIn);
            }
        });

        view.findViewById(R.id.buttonAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings =
                        getActivity().getSharedPreferences(getString(R.string.PREFS_NAME), 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong("currentTask", taskId);
                editor.putString("taskStart", Utils.getCurrentTimeAsString());
                editor.putString("lastUpdate", Utils.getCurrentTimeAsString());
                editor.apply();

                Intent intent = new Intent(getActivity(), TimeLeftService.class);
                intent.putExtra(NAME_KEY, taskName);
                intent.putExtra(TIME_KEY, taskTime);
                logger.debug("starting service");
                getActivity().startService(intent);

                view.startAnimation(animationFadeOut);
                Ui.run(new Runnable() {
                    @Override
                    public void run() {
                        ((ChoiceActivity)getActivity()).onTaskAccepted();
                    }
                });
            }
        });
    }

    @Override
    public void onTasksSelectionFinished() {

        logger.debug("onTasksSelectionFinished()");

        TaskDataSet task = selectOneTask();
        if (task != null) {
            taskId = task.getId();
            taskName = task.getName();
            taskNameTextView.setText(taskName);
            taskCommentTextView.setText(task.getComment());
            taskTime = task.getTime();
            taskTimeTextView.setText(Utils.getTimeAsString(task.getTime()));
            taskDateTextView.setText(task.getDate());
            if (task.getDate() != null) {
                dateIcon.setVisibility(View.VISIBLE);
            } else {
                dateIcon.setVisibility(View.INVISIBLE);
            }
            noApproachTasksLabel.setVisibility(View.INVISIBLE);
            taskCardContent.setVisibility(View.VISIBLE);
        } else {
            taskCardContent.setVisibility(View.INVISIBLE);
            noApproachTasksLabel.setVisibility(View.VISIBLE);
        }
    }

    private TaskDataSet selectOneTask() {

        logger.debug("selectOneTask()");

        TaskDataSet task;

        List<TaskDataSet> urgentTasks = CurrentChoice.getInstance().getUrgentTasks();
        List<TaskDataSet> simpleTasks = CurrentChoice.getInstance().getSimpleTasks();
        if (!urgentTasks.isEmpty()) {
            Date minDate = Utils.parseDateString(urgentTasks.get(0).getDate());
            task = urgentTasks.get(0);
            for (int i = 0; i < urgentTasks.size() - 1; i++) {
                Date date = Utils.parseDateString(urgentTasks.get(i + 1).getDate());
                if (date.before(minDate)) {
                    minDate = date;
                    task = urgentTasks.get(i);
                }
            }
            urgentTasks.remove(task);
            CurrentChoice.getInstance().setUrgentTasks(urgentTasks);
        } else if (!simpleTasks.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(simpleTasks.size());
            task = simpleTasks.get(index);
            simpleTasks.remove(task);
            CurrentChoice.getInstance().setSimpleTasks(simpleTasks);
        } else {
            return null;
        }

        return task;
    }
}
