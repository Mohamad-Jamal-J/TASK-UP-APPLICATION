package bzu.android.Mohamad.Jaradat1193265.assignment1;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.time.LocalDate;
import java.util.List;

public abstract class AbstractTaskManage extends AppCompatActivity {
    public static final String TASK_LIST_KEY = "tasksList";
    public static final String DONE_TASK_LIST_KEY = "doneTasksList";
    public static final String IS_DUE_VIEW= "isDueView";
    public static final String POSITION = "position";
    public ImageButton backButton;
    public Button addTaskButton;
    public EditText titleEditText;
    public EditText detailEditText;
    public DatePicker datePicker;
    public RadioGroup priorityRadioGroup;
    public List<Task> tasksList;
    public List<Task> doneTasksList;

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor sharedPreferencesEditor;


    public void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }
    public void saveTasksListToPreferences(){
        Gson GSON = new Gson();

        String JSON_STRING = GSON.toJson(tasksList);
        sharedPreferencesEditor.putString(TASK_LIST_KEY, JSON_STRING);

        JSON_STRING = GSON.toJson(doneTasksList);
        sharedPreferencesEditor.putString(DONE_TASK_LIST_KEY, JSON_STRING);

        sharedPreferencesEditor.apply();

    }
    public String getTitleFromView(){
        if (titleEditText==null)
            return null;
        String currText = titleEditText.getText().toString().trim();
        return !currText.isEmpty()? currText:"";
    }
    public String getDateFromView(){
        if (datePicker==null)
            return null;
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        return LocalDate.of(year, month + 1, day)+"";
    }
    public int getPriorityRadiosFromView(){
        int checkedRadioId = priorityRadioGroup.getCheckedRadioButtonId();
        RadioButton priorityRadioButton = findViewById(checkedRadioId);
        return Integer.parseInt(priorityRadioButton.getTag().toString());
    }
    public void clearViews(){
        ColorStateList defaultHintTextColor = detailEditText.getHintTextColors();
        titleEditText.setHintTextColor(defaultHintTextColor);
        titleEditText.setText("");
        detailEditText.setText("");
        datePicker.setMinDate(System.currentTimeMillis());
        int firstOptionId = priorityRadioGroup.getChildAt(0).getId();
        priorityRadioGroup.check(firstOptionId);
    }
}
