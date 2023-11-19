package bzu.android.Mohamad.Jaradat1193265.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CreateNewTaskActivity extends AbstractTaskManage {
    private Button addTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_task);

        loadTasksListFromIntent();
        setupSharedPreferences();
        hookLayouts();
        setLayoutsListeners();
    }

    // this method will retrieve the due tasks list from the previous activity
    // in case none were provided, a new & empty list will be created.
    private void loadTasksListFromIntent() {
        Intent intent = getIntent();
        String JSON_LIST = intent.getStringExtra(TASK_LIST_KEY);

        Gson GSON = new Gson();
        Type objectType = new TypeToken<ArrayList<Task>>(){}.getType();
        tasksList = GSON.fromJson(JSON_LIST,objectType);

        if (tasksList==null)
            tasksList=new ArrayList<>();

    }

    // this method hooks (references/ links) the views in activity_create_new_task.xml
    // with the declared variables in the java class.
    public void hookLayouts(){
        super.hookLayouts();
        addTaskButton = findViewById(R.id.addTaskButton);
    }

    // this method sets the click listeners for the views
    // whenever an action is made on them.
    private void setLayoutsListeners() {
        addTaskButton.setOnClickListener(this::addTaskAction);

        // simply go back to the main activity.
        backButton.setOnClickListener(action -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }


    // this method will add the new task to the due tasks list
    // of-course checks and validations for that, are done as well.
    private void addTaskAction(View view){
        titleEditText.setHintTextColor(getColor( R.color.alertHintColor));
        String title = getTitleFromView();
        if (title ==null || title.isEmpty()){
            titleEditText.setHintTextColor(getColor( R.color.alertHintColor));
            Toast.makeText(this,"Empty title",Toast.LENGTH_LONG).show();
            return;
        }

        String detail = detailEditText.getText().toString().trim();
        if (detail.isEmpty())
            detail=Task.EMPTY;

        String date = getDateFromView();
        if (date==null)
            return;

        int priority = getPriorityRadiosFromView();

        Task task = new Task(title, detail, date, priority);

        tasksList.add(task);

        this.saveTasksListToPreferences();

        Toast.makeText(this,"Task Added",Toast.LENGTH_LONG).show();
        clearViews();

    }

    // this method is responsible for saving (committing/applying)
    // the changes on due tasks list only.
    public void saveTasksListToPreferences(){
        putInSharedReferences(TASK_LIST_KEY,tasksList);

        sharedPreferencesEditor.apply();
    }

}

