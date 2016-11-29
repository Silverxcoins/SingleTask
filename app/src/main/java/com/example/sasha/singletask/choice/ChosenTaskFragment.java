package com.example.sasha.singletask.choice;

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

    TextView taskNameTextView;
    TextView taskCommentTextView;
    TextView taskTimeTextView;
    TextView taskDateTextView;

    Animation animationFadeIn;
    Animation animationFadeOut;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chosen_task, null);

        taskNameTextView = (TextView) view.findViewById(R.id.textViewChosenTask);
        taskCommentTextView = (TextView) view.findViewById(R.id.textViewChosenTaskComment);
        taskTimeTextView = (TextView) view.findViewById(R.id.textViewChosenTaskTime);
        taskDateTextView = (TextView) view.findViewById(R.id.textViewChosenTaskDate);

        animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);

        setButtonsClickListeners(view);

        DB.getInstance(getActivity()).setSelectTasksCallback(this);

        return view;
    }

    private void setButtonsClickListeners(final View view) {
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
    }

    @Override
    public void onTasksSelectionFinished() {

        logger.debug("onTasksSelectionFinished()");

        TaskDataSet task = selectOneTask();
        if (task != null) {
            taskNameTextView.setText(task.getName());
            taskCommentTextView.setText(task.getComment());
            taskTimeTextView.setText(Utils.getTimeAsString(task.getTime()));
            taskDateTextView.setText(task.getDate());
            if (task.getDate() != null) {
                view.findViewById(R.id.dateIcon).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.dateIcon).setVisibility(View.INVISIBLE);
            }
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
