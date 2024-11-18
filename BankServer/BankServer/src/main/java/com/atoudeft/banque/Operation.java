package com.atoudeft.banque;

import java.io.Serializable;
import java.util.Date;

public abstract class Operation implements Serializable {

    private TypeOperation type;
    private Date date;

    public TypeOperation getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public void setType(TypeOperation type) {
        this.type = type;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
