package bzu.android.Mohamad.Jaradat1193265.assignment1;

import android.app.AlertDialog;
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
    private Button setDoneDueButton;
    private Button viewHistoryButton;
    private Button viewDueTasksButton;
    private ImageButton editTasksButton;
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

    // this method will retrieve the due & done tasks lists from shared preferences,
    // in case none existed, a new & empty lists will be created.
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

        updateMaxTaskId();

    }

    // this method will find the max id in the retrieved due & done tasks lists
    // and assign the value to the incremental id variable in Task class.
    private void updateMaxTaskId(){
        int maxID = Task.incrementalId;
        for (Task currTask:tasksList)
            if (maxID < currTask.getIdentifier())
                maxID =currTask.getIdentifier();

        for (Task currTask:doneTasksList)
            if (maxID < currTask.getIdentifier())
                maxID =currTask.getIdentifier();
        Task.incrementalId =maxID;
    }

    // this method hooks (references/ links) the views in activity_main.xml
    // with the declared variables in the java class.
    public void hookLayouts(){
        tasksListView = findViewById(R.id.tasksListView);
        addTaskViewButton= findViewById(R.id.addTaskViewButton);
        cleanCheckedButton=findViewById(R.id.cleanCheckedTasksButton);
        editTasksButton=findViewById(R.id.editTaskButton);
        setDoneDueButton = findViewById(R.id.setDoneButton);
        viewHistoryButton =findViewById(R.id.viewHistoryButton);
        viewDueTasksButton =findViewById(R.id.viewDueTasksButton);
        titleTextView = findViewById(R.id.titleTextView);
    }

    // this method will decide which tasks list (due/done) will be displayed for the user
    // in the main activity help due tasks view or done tasks view.
    private void setOnCreateListAdapter() {
        Intent intent = getIntent();
        // once the app is launched it will be a due view by default, but if the activity was called
        // by another activity using intents, it will go back to the view it was before calling the
        // latter intent, which will be passed in from the latter intent.
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

    // this method will set the array adapter and it's listeners based on a given
    // list and the preferred layout (choice mode). which is responsible for providing the
    // multiple functionalities of single & long click listeners on items in the given list based on
    // the current mode (edit mode on/off).
    private void setListViewAdapter(List<Task> list ,int choiceMode) {
        taskListAdapter = new ArrayAdapter<>(
                this, choiceMode, list);
        AdapterView.OnItemClickListener itemClickListener =
                (parent, view, position, id) -> oneClickAdapterListener(list, (CheckedTextView) view, position);

        AdapterView.OnItemLongClickListener itemLongClickListener =
                (parent, view, position, id)-> longClickAdapterListener(position);

        tasksListView.setOnItemClickListener(itemClickListener);
        tasksListView.setOnItemLongClickListener(itemLongClickListener);
        tasksListView.setAdapter(taskListAdapter);
    }

    // this method will be set to a onClickListener function for the list adapter view
    // when edit mode is one, the user can select multiple items simply by clicking on them.
    // when it's off, a simple dialog will be displayed to show the full details of the selected task
    private void oneClickAdapterListener(List<Task> list, CheckedTextView view, int position) {
        if (editModeOn){
           Task task = list.get(position);
            if (view.isChecked()){
                checkedTasks.add(task);
            }else {
                checkedTasks.remove(task);
            }
        }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                Task task = list.get(position);
                builder.setMessage(task.speacialToString()).setTitle("Task Information");
                AlertDialog dialog = builder.create();
                dialog.show();
         }
    }

    // this method will be set to a longClickListener function for the list adapter view
    // it will start the ViewTaskDetailsActivity after a long click on an item in the list.
    private boolean longClickAdapterListener(int position) {
        if (!editModeOn){
            Intent intent = new Intent(this, ViewTaskDetailsActivity.class);

            Gson GSON = new Gson();

            String JSON_STRING = GSON.toJson(tasksList);
            intent.putExtra(TASK_LIST_KEY, JSON_STRING);

            JSON_STRING = GSON.toJson(doneTasksList);
            intent.putExtra(DONE_TASK_LIST_KEY, JSON_STRING);

            intent.putExtra(IS_DUE_VIEW, isDueView + "");
            intent.putExtra(POSITION, position + "");

            startActivity(intent);
        }
        return true;
    }

    // this method contains all the click listeners for the views in activity_main.xml file
    private void setLayoutsListeners() {
        // this will call the CreateNewTaskActivity.
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

        setDoneDueButton.setOnClickListener(this::onClickDoneDueListener);

        cleanCheckedButton.setOnClickListener(this::onClickCleanButtonListener);

        // this will show the list of done tasks in the main activity.
        viewHistoryButton.setOnClickListener(action->{
            isDueView =false;
            switchListsAndVisibility(View.INVISIBLE,View.GONE,View.VISIBLE, doneTasksList);
            changeDoneDueButtonText();
            changeTitleListName();
        });

        // this will show the list of due tasks in the main activity.
        viewDueTasksButton.setOnClickListener(action-> {
            isDueView=true;
            switchListsAndVisibility(View.VISIBLE,View.VISIBLE,View.GONE, tasksList);
            changeDoneDueButtonText();
            changeTitleListName();

        });
    }

    // this will delete the the selected tasks permanently from the current list.
    private void onClickCleanButtonListener(View action) {
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
    }

    // this method will change the selected tasks state to (done/due),
    // and respectively add it to (done/due) list, based on the current view (due/done view)
    private void onClickDoneDueListener(View action) {
        for (Task checkedTask : checkedTasks) {
            if (isDueView) {
                doneTasksList.add(checkedTask);
                checkedTask.setDone(true);
            }else {
                tasksList.add(checkedTask);
                checkedTask.setDone(false);
            }
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
    }

    //this method will switch the adapter's current list and change necessary visibility states
    private void switchListsAndVisibility(int state1, int state2, int state3, List<Task> list ) {
        addTaskViewButton.setVisibility(state1);
        viewHistoryButton.setVisibility(state2);
        viewDueTasksButton.setVisibility(state3);
        setListViewAdapter(list, android.R.layout.simple_list_item_1);
    }

    // this method will toggle the edit mode on/off and as a result
    // it changes the listview layout mode to multiple or simple.
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

    // this method will set the visibility for buttons in edit
    // mode based on a given state (GONE/VISIBLE)
    private void switchVisibility(int state) {
        cleanCheckedButton.setVisibility(state);
        setDoneDueButton.setVisibility(state);
    }

    // this method will change the text displayed in titleTextView
    // based on the current view (done/due)
    private void changeTitleListName(){
        String msg = "Your " + ((isDueView)? DUE:DONE) +" List";
        titleTextView.setText(msg);
    }

    // this method will change the text displayed on the doneDueButton
    // based on the current view  (done/due)
    private void changeDoneDueButtonText(){
        String msg =  "Set As "+((isDueView)? DONE:DUE);
        setDoneDueButton.setText(msg);
    }


}