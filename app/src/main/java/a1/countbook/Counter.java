package a1.countbook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andoryu on 2017-09-30.
 */

public class Counter {
    private String name;
    private int value;
    private int newValue;
    private Date date;
    private Date lastDate;
    private String comment;

    public Counter(String name, int value, String comment) {
        this.name = name;
        this.value = value;
        this.comment = comment;
        this.newValue = this.value;
        this.date = new Date();
        this.lastDate = this.date;
    }

    public void setValue(int value) {
            this.newValue = value;
            this.lastDate = new Date();
    }

    public void setName(String name) {
        this.name = name;
        this.lastDate = new Date();
    }

    public void setComment(String comment) {
        this.comment = comment;
        this.lastDate = new Date();
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return newValue;
    }

    public int getInitial() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd, h:mm:ss").format(date);
    }

    public String getLastDate() {
        return new SimpleDateFormat("yyyy-MM-dd, h:mm:ss").format(lastDate);
    }

    public String toString() {
        return Integer.toString(value);
    }
}