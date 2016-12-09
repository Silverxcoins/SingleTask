package com.example.sasha.singletask.choice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.helpers.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurrentTaskFragment extends Fragment implements DB.MarkTaskDeletedCallback {

    private static final Logger logger = LoggerFactory.getLogger(CurrentTaskFragment.class);

    private static final String NAME_KEY = "name";
    private static final String COMMENT_KEY = "comment";
    private static final String DATE_KEY = "date";
    private static final String TIME_KEY = "time";
    private static final String IS_TASK_FOUND_KEY = "isTaskFound";

    private TextView taskNameTextView;
    private TextView taskCommentTextView;
    private TextView taskTimeTextView;

    private Bundle state;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        logger.debug("onCreateView()");

        view = inflater.inflate(R.layout.fragment_current_task, null);

        taskNameTextView = (TextView) view.findViewById(R.id.textViewCurrentTask);
        taskCommentTextView = (TextView) view.findViewById(R.id.textViewCurrentTaskComment);
        taskTimeTextView = (TextView) view.findViewById(R.id.textViewCurrentTaskTime);

        Animation animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        view.startAnimation(animationFadeIn);

        setButtonsClickListeners();
        DB.getInstance(getActivity()).setMarkTaskDeletedCallback(this);

        return view;
    }

    private void setButtonsClickListeners() {
        Button firstButton = (Button) view.findViewById(R.id.firstButton);
        Button rejectButton = (Button) view.findViewById(R.id.rejectButton);
        Button doneButton = (Button) view.findViewById(R.id.doneButton);

        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentTask(true);
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentTask(false);
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentTask(true);
            }
        });
    }

    private void updateCurrentTask(boolean isDeleted) {
        SharedPreferences settings =
                getActivity().getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        if (isDeleted) {
            DB.getInstance(getActivity()).markTaskDeleted(settings.getLong("currentTask", 0));
        }

        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("currentTask", 0);
        editor.putString("taskStart", null);
        editor.putString("lastUpdate", Utils.getCurrentTimeAsString());
        if (!isDeleted) {
            ((ChoiceActivity) getActivity()).onTaskUpdated();
        }
    }

    @Override
    public void onMarkTaskDeleted(long taskId) {
        ((ChoiceActivity) getActivity()).onTaskUpdated();
    }
}
