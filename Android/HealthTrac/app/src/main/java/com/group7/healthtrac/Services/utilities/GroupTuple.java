package com.group7.healthtrac.services.utilities;

import com.group7.healthtrac.models.FeedEvent;

import java.util.List;

public class GroupTuple {

    private List<FeedEvent> m_Item1;
    private int m_Item2;

    public GroupTuple(List<FeedEvent> m_Item1, int m_Item2) {
        this.m_Item1 = m_Item1;
        this.m_Item2 = m_Item2;
    }

    public List<FeedEvent> getM_Item1() {
        return m_Item1;
    }

    public void setM_Item1(List<FeedEvent> m_Item1) {
        this.m_Item1 = m_Item1;
    }

    public int getM_Item2() {
        return m_Item2;
    }

    public void setM_Item2(int m_Item2) {
        this.m_Item2 = m_Item2;
    }
}
