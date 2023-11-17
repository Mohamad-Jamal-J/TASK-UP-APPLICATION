package bzu.android.Mohamad.Jaradat1193265.assignment1;

import androidx.annotation.NonNull;

import java.time.LocalDate;

public class Task {

    private int identifier;
    private String title;
    private String detail;
    private String dateDue;
    private int priority;
    private String dateCreated;
    private static int incrementalId=1;

    private boolean isDone;

    public static final String EMPTY="No Details Provided";


    public Task(String title, String detail, String dateDue, int priority) {
        setIdentifier();
        setTitle(title);
        setDetail(detail);
        setDateDue(dateDue);
        setDateCreated();
        setPriority(priority);
        setDateCreated();
        setDone(false);
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier() {
        this.identifier = incrementalId++;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDateDue() {
        return dateDue;
    }

    public void setDateDue(String dateDue) {
        this.dateDue = dateDue;

    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated() {
        this.dateCreated = LocalDate.now()+"";
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @NonNull
    @Override
    public String toString() {
        String prioString;
        if (priority==1)
            prioString="Low";
        else if (priority==2)
            prioString="Mid";
        else
            prioString="High";
        return "\nTask: " + title +
                        "\n\nDue: " + dateDue+"\t\t\t\t\t\t\t\tPriority: " + prioString ;
    }

}
