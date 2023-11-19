package bzu.android.Mohamad.Jaradat1193265.assignment1;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
    public EditText titleEditText;
    public EditText detailEditText;
    public DatePicker datePicker;
    public RadioGroup priorityRadioGroup;
    public List<Task> tasksList;
    public List<Task> doneTasksList;

    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor sharedPreferencesEditor;


    // this method instantiates the objects responsible for reading and writing data on shared preferences
    public void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    // this method is responsible for saving (committing/applying)
    // the changes on lists (due and done ones)
    public void saveTasksListToPreferences(){

        putInSharedReferences(TASK_LIST_KEY,tasksList);

        putInSharedReferences(DONE_TASK_LIST_KEY,doneTasksList);

        // i'm not using commit() since apply() is faster and i'm not doing
        // any action with the returned value from commit()
        sharedPreferencesEditor.apply();

    }
    // this method is responsible for writing data on shared preferences in JSON
    // the data in this context is the a key string and a list of tasks.
    public void putInSharedReferences(String KEY,List<Task> list){
        Gson GSON = new Gson();

        String JSON_STRING = GSON.toJson(list);
        sharedPreferencesEditor.putString(KEY, JSON_STRING);
    }

    // this method hooks (references/ links) in general, the views in a certain xml file based
    // on the subclass calling it, with the declared variables in the java class.
    // Subclasses of this class may implement it differently.
    public void hookLayouts(){
        backButton = findViewById(R.id.abortTaskButton);
        titleEditText = findViewById(R.id.titleEditText);
        detailEditText =findViewById(R.id.detailEditText);
        datePicker = findViewById(R.id.datePicker);
        priorityRadioGroup = findViewById(R.id.radioGroup);
        datePicker.setMinDate(System.currentTimeMillis());
    }

    // this method retrieves the title text input in the titleEditText
    // the return value is either the title entered or an empty string
    // in case the user entered an empty or blank string
    public String getTitleFromView(){
        if (titleEditText==null)
            return null;
        String currText = titleEditText.getText().toString().trim();
        return !currText.isEmpty()? currText:"";
    }

    // this method retrieves the date picked by the user in the date picker field
    // the return value is a string in the format of YYYY-MM-DD
    public String getDateFromView(){
        if (datePicker==null)
            return null;
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        return LocalDate.of(year, month + 1, day)+"";
    }

    //this method retrieves the selected radio button which contains the priority
    // (importance) of the task, the return value is an int with values of 1, 2, or 3
    // which represents low, medium high choices respectively.
    public int getPriorityRadiosFromView(){
        int checkedRadioId = priorityRadioGroup.getCheckedRadioButtonId();
        RadioButton priorityRadioButton = findViewById(checkedRadioId);
        return Integer.parseInt(priorityRadioButton.getTag().toString());
    }

    // this method will clear all inputs from the user in the views
    // and sets them to default
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
