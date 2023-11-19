package bzu.android.Mohamad.Jaradat1193265.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;

public class ViewTaskDetailsActivity extends AbstractTaskManage {
    private Task task;
    private Button updateTaskButton;
    private  boolean isDueView;
    private RadioGroup radioGroupStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task_details);

        loadTasksListFromIntent();
        setupSharedPreferences();
        hookLayouts();
        initializeLayoutsWithInfo();
        setLayoutsListeners();
    }

    // this method will retrieve the date sent from the previous activity such as
    // (due & done task lists, the previous due/done view and the position of the selected task)
    private void loadTasksListFromIntent() {
        Intent intent = getIntent();
        Gson GSON = new Gson();
        Type objectType = new TypeToken<ArrayList<Task>>(){}.getType();

        String JSON_STRING = intent.getStringExtra(TASK_LIST_KEY);
        tasksList = GSON.fromJson(JSON_STRING,objectType);
        if (tasksList==null)
            tasksList=new ArrayList<>();

        JSON_STRING = intent.getStringExtra(DONE_TASK_LIST_KEY);
        doneTasksList = GSON.fromJson(JSON_STRING,objectType);
        if (doneTasksList==null)
            doneTasksList=new ArrayList<>();


        String string = intent.getStringExtra(IS_DUE_VIEW);
        if (string == null){
            Toast.makeText(this,"error occurred",Toast.LENGTH_SHORT).show();
        }else {
            isDueView = Boolean.parseBoolean(string);
        }

        string = intent.getStringExtra(POSITION);
        if (string == null){
            Toast.makeText(this,"error occurred",Toast.LENGTH_SHORT).show();
        }else {
            int position = Integer.parseInt(string);
            if (isDueView)
                task = tasksList.get(position);
            else
                task = doneTasksList.get(position);
        }
    }

    // this method hooks (references) the views in activity_view_task_details.xml
    public void hookLayouts(){
        super.hookLayouts();
        updateTaskButton = findViewById(R.id.updateTaskButton);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
    }

    //this method will initiate the views with the data of the current task
    private void initializeLayoutsWithInfo() {
        updateTaskButton = findViewById(R.id.updateTaskButton);

        titleEditText.setText(task.getTitle());
        detailEditText.setText(
                (detailEditText.getText().toString().equals(Task.EMPTY))? "":detailEditText.getText()
        );
        priorityRadioGroup.check(
                priorityRadioGroup.getChildAt( task.getPriority()-1).getId()
        );

        LocalDate localDate = LocalDate.parse(task.getDateDue());
        datePicker.updateDate(localDate.getYear(),localDate.getMonthValue()-1,localDate.getDayOfMonth());


        int index = (task.isDone())? 1:0;
        radioGroupStatus.check(
                radioGroupStatus.getChildAt(index).getId()
        );
    }

    // this method sets the click listeners for the views whenever an action is made on them.
    private void setLayoutsListeners() {

        updateTaskButton.setOnClickListener(this::updateTaskAction);

        backButton.setOnClickListener(action -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(IS_DUE_VIEW,isDueView+"");
            startActivity(intent);
        });
    }

    // this method will apply the changes on the current task's details the were made (if any were done)
    private void updateTaskAction(View view){
        titleEditText.setHintTextColor(getColor( R.color.alertHintColor));
        String title = getTitleFromView();
        if (title ==null || title.isEmpty()){
            titleEditText.setHintTextColor(getColor( R.color.alertHintColor));
            titleEditText.setText("");
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

        int checkedRadioId = radioGroupStatus.getCheckedRadioButtonId();
        boolean status = (checkedRadioId == findViewById(R.id.radioButtonStatusDone).getId());

        boolean statusBefore = task.isDone();

        if (title.equals(task.getTitle()) && detail.equals(task.getDetail())
                && date.equals(task.getDateDue())  && priority==task.getPriority()
                && status==statusBefore)
            Toast.makeText(this,"No Changes Were Made",Toast.LENGTH_LONG).show();
        else {
            task.setTitle(title);
            task.setDateDue(date);
            task.setPriority(priority);
            task.setDetail(detail);
            task.setDone(status);


            if (statusBefore!= status) {
                if (status){
                tasksList.remove(task);
                doneTasksList.add(task);
                } else{
                    tasksList.add(task);
                    doneTasksList.remove(task);
                }
            }
            saveTasksListToPreferences();
            Toast.makeText(this,"Task Updated",Toast.LENGTH_LONG).show();
        }

    }



}