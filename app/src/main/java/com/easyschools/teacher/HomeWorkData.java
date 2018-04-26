package com.easyschools.teacher;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by vestment17 on 08/02/18.
 */

public class HomeWorkData extends ExpandableGroup
{
     HomeWorkData(String title ,List items) {
        super(title,  items);
    }

}
