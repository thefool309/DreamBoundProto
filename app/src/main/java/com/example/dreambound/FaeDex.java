package com.example.dreambound;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FaeDex implements Serializable {
    private List<Fae> faeList;

    public FaeDex() {
        this.faeList = new ArrayList<>();
    }

    public void addFae(Fae fae) {
        faeList.add(fae);
    }

    public List<Fae> getFaeList() {
        return faeList;
    }


}
