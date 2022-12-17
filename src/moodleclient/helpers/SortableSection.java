/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import moodleclient.entity.Sections;

/**
 *
 * @author pepi
 */
public class SortableSection implements Comparable<SortableSection>{
    
    public Sections section;
    
    public SortableSection(Sections _section){
        this.section = _section;
    }

    @Override
    public int compareTo(SortableSection o) {
        return(this.section.getRemoteId() < o.section.getRemoteId() ? -1:
                (this.section.getRemoteId() == o.section.getRemoteId() ? 0 : 1));
    }
    
}
