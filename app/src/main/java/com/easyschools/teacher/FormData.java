package com.easyschools.teacher;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by vestment17 on 21/02/18.
 */

public class FormData extends ExpandableGroup {

    String title , id ;

    public FormData(String title, List items) {
        super(title, items);
    }

}
