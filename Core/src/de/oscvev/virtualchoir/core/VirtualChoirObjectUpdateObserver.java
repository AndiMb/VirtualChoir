/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Andreas Hauffe
 */
public class VirtualChoirObjectUpdateObserver implements Comparator<VirtualChoirObject>{
    
    private static VirtualChoirObjectUpdateObserver instance = null;
    
    private VirtualChoirObject source = null;
    
    private ArrayList<VirtualChoirObject> objects = new ArrayList<>();
    
    public VirtualChoirObjectUpdateObserver(VirtualChoirObject source){
        this.source = source;
    }

    public VirtualChoirObject getSource() {
        return source;
    }
    
    public static void setActual(VirtualChoirObjectUpdateObserver inst){
        instance = inst;
    }
    
    public static VirtualChoirObjectUpdateObserver getActual(){
        return instance;
    }
    
    public void addVirtualChoirObject(VirtualChoirObject eObject){
        objects.add(eObject);
    }
    
    public void informAllVirtualChoirObjects(){
        Collections.sort(objects, this);
        for (VirtualChoirObject vChObject : objects) {
            vChObject.update();
        }
        objects.clear();
        source = null;
        instance = null;
    }

    @Override
    public int compare(VirtualChoirObject o1, VirtualChoirObject o2) {
        // ab Java 7 sollte Integer.compare(a,b) verwendet werden!!!
        return Integer.compare(o1.getUpdatePriority(), o2.getUpdatePriority());
    }    
}
