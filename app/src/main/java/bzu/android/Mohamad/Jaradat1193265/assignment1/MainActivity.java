package bzu.android.Mohamad.Jaradat1193265.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbstractTaskManage {
    private final static String DUE = "Due";
    private final static String DONE = "Done";

    private ListView tasksListView;
    private Button addTaskViewButton;
    private Button cleanCheckedButton;
    private Button setDoneButton;
    private Button viewHistoryButton;
    private Button viewDueTasksButton;
    private ImageButton editTasksButton;
    private List<Task> tasksList;
    private ArrayAdapter<Task> taskListAdapter;
    private ArrayList<Task> checkedTasks;
    private boolean editModeOn;
    private boolean isDueView;
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSharedPreferences();
        loadTasksListFromPreferences();
        hookLayouts();
        checkedTasks = new ArrayList<>();
        editModeOn = false;

        setOnCreateListAdapter();
        setLayoutsListeners();
    }



    private void loadTasksListFromPreferences() {
        Gson GSON = new Gson();
        String JSON_STRING = sharedPreferences.getString(TASK_LIST_KEY,null);
        Type objectType = new TypeToken<ArrayList<Task>>(){}.getType();
        tasksList = GSON.fromJson(JSON_STRING,objectType);

        if (tasksList==null)
            tasksList=new ArrayList<>();

        JSON_STRING = sharedPreferences.getString(DONE_TASK_LIST_KEY,null);
        doneTasksList = GSON.fromJson(JSON_STRING,objectType);

        if (doneTasksList==null)
            doneTasksList=new ArrayList<>();
    }
    private void hookLayouts(){
        tasksListView = findViewById(R.id.tasksListView);
        addTaskViewButton= findViewById(R.id.addTaskViewButton);
        cleanCheckedButton=findViewById(R.id.cleanCheckedTasksButton);
        editTasksButton=findViewById(R.id.editTaskButton);
        setDoneButton = findViewById(R.id.setDoneButton);
        viewHistoryButton =findViewById(R.id.viewHistoryButton);
        viewDueTasksButton =findViewById(R.id.viewDueTasksButton);
        titleTextView = findViewById(R.id.titleTextView);
    }
    private void setOnCreateListAdapter() {
        Intent intent = getIntent();
        isDueView =
                intent.getStringExtra(IS_DUE_VIEW) == null || Boolean.parseBoolean(intent.getStringExtra(IS_DUE_VIEW));

        if (isDueView){
            setListViewAdapter(tasksList, android.R.layout.simple_list_item_1);
            viewHistoryButton.setVisibility(View.VISIBLE);
            viewDueTasksButton.setVisibility(View.GONE);
            String msg = "Your "+DUE+" List";
            titleTextView.setText(msg);
        }
        else{
            setListViewAdapter(doneTasksList,android.R.layout.simple_list_item_1);
            addTaskViewButton.setVisibility(View.INVISIBLE);
            viewHistoryButton.setVisibility(View.GONE);
            viewDueTasksButton.setVisibility(View.VISIBLE);
            String msg = "Your "+DONE+" List";
            titleTextView.setText(msg);
        }
    }
    private void setListViewAdapter(List<Task> list ,int choiceMode) {
        taskListAdapter = new ArrayAdapter<>(
                this, choiceMode, list);
        AdapterView.OnItemClickListener itemClickListener = (parent, view, position, id) -> {
           if (editModeOn){
              Task task = list.get(position);
              CheckedTextView checkedTextView = (CheckedTextView) view;
               if (checkedTextView.isChecked()){
                   checkedTasks.add(task);
               }else {
                   checkedTasks.remove(task);
               }
           }else {
               Intent intent = new Intent(this, ViewTaskDetailsActivity.class);

               Gson GSON = new Gson();

               String JSON_STRING  = GSON.toJson(tasksList);
               intent.putExtra(TASK_LIST_KEY,JSON_STRING);

               JSON_STRING = GSON.toJson(doneTasksList);
               intent.putExtra(DONE_TASK_LIST_KEY,JSON_STRING);


               intent.putExtra(IS_DUE_VIEW,isDueView+"");
               intent.putExtra(POSITION,position+"");

               startActivity(intent);
            }
        };

        tasksListView.setOnItemClickListener(itemClickListener);
        tasksListView.setAdapter(taskListAdapter);
    }

    private void setLayoutsListeners() {

        addTaskViewButton.setOnClickListener(action->{
           Gson GSON = new Gson();
           String JSON_STRING  = GSON.toJson(tasksList);
           Intent intent = new Intent(this, CreateNewTaskActivity.class);
           intent.putExtra(TASK_LIST_KEY,JSON_STRING);
           startActivity(intent);
        });

        editTasksButton.setOnClickListener(action->{
            if (isDueView)
                editModeClickListener(tasksList);
            else
                editModeClickListener(doneTasksList);

        });

        setDoneButton.setOnClickListener(action->{
            for (Task checkedTask : checkedTasks) {
                checkedTask.setDone(true);
                if (isDueView)
                    doneTasksList.add(checkedTask);
                else
                    tasksList.add(checkedTask);
                taskListAdapter.remove(checkedTask);
            }
            String message;
            if (checkedTasks.size()==0)
                message="No Items Selected";
            else{
                message = checkedTasks.size()+" Items Were Set To "+ ((!isDueView)?"Due":"Done");
                taskListAdapter.notifyDataSetChanged();
            }
            Toast.makeText(this,message,Toast.LENGTH_LONG).show();
            checkedTasks.clear();
            tasksListView.clearChoices();
            saveTasksListToPreferences();
        });

        cleanCheckedButton.setOnClickListener(action->{
            for (Task checkedTask : checkedTasks) {
                taskListAdapter.remove(checkedTask);
            }
            String message;
            if (checkedTasks.size()==0)
                message="No Items Selected";
            else{
                message = checkedTasks.size()+" Items Removed";
                taskListAdapter.notifyDataSetChanged();
            }
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
            checkedTasks.clear();
            tasksListView.clearChoices();
            saveTasksListToPreferences();
        });

        viewHistoryButton.setOnClickListener(action->{
            isDueView =false;
            switchListsAndVisibility(View.INVISIBLE,View.GONE,View.VISIBLE, doneTasksList);
            String msg = "Set As "+DUE;
            setDoneButton.setText(msg);
        });

        viewDueTasksButton.setOnClickListener(action-> {
            isDueView=true;
            switchListsAndVisibility(View.VISIBLE,View.VISIBLE,View.GONE, tasksList);
            String msg = "Set As "+DONE;
            setDoneButton.setText(msg);
        });
    }

    private void switchListsAndVisibility(int state1, int state2, int state3, List<Task> list ) {
        addTaskViewButton.setVisibility(state1);
        viewHistoryButton.setVisibility(state2);
        viewDueTasksButton.setVisibility(state3);
        setListViewAdapter(list, android.R.layout.simple_list_item_1);
    }

    private void editModeClickListener(List<Task> list) {
        editModeOn=!editModeOn;
        if (editModeOn){
            tasksListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            setListViewAdapter(list, android.R.layout.simple_list_item_multiple_choice);

            switchVisibility(View.VISIBLE);
            viewHistoryButton.setVisibility(View.GONE);
            viewDueTasksButton.setVisibility(View.GONE);

        }else {
            tasksListView.clearChoices();
            tasksListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            setListViewAdapter(list, android.R.layout.simple_list_item_1);

            switchVisibility(View.GONE);
            if (isDueView)
                viewHistoryButton.setVisibility(View.VISIBLE);
            else
                viewDueTasksButton.setVisibility(View.VISIBLE);

            checkedTasks.clear();
        }
    }

    private void switchVisibility(int state) {
        cleanCheckedButton.setVisibility(state);
        setDoneButton.setVisibility(state);
    }


}