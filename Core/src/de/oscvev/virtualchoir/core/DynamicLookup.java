/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.core;

import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Andreas
 */
public class DynamicLookup extends AbstractLookup {

    private final InstanceContent content;

    protected DynamicLookup() {
        this(new InstanceContent());
    }

    private DynamicLookup(InstanceContent ic) {
        super(ic);
        content = ic;
    }

    public void add(Object instance) {
        content.add(instance);
    }

    public void remove(Object instance) {
        content.remove(instance);
    }
    
    public void clear(){
        for (Object o : this.lookupAll(Object.class)){
            remove(o);
        }
    }
}
