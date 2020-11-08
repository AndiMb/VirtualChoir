/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Andreas
 */
public abstract class VirtualChoirObject {
    
    private final String uuid;                  // Universal Unique ID, um das Object auch nach dem Speicher in Dateien eindeutig zuordnen zu können
    
    private       String name;                  // Name des Objekts
    public static final String PROP_NAME = "name";
    
    private transient PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    private final DynamicLookup lookup = new DynamicLookup();
    
    private boolean changed = false;
    private VirtualChoir vChoir;

    public VirtualChoirObject(String uuid, String name, VirtualChoir vChoir, boolean addToLookup) {
        this.uuid = uuid;
        this.name = name;
        this.vChoir = vChoir;
        if (addToLookup){
            VirtualChoirLookup.getDefault().add(this);
        }
    }
    
    public VirtualChoir getVirtualChoir(){
        return vChoir;
    }
    
    public abstract int getUpdatePriority();
    
    public void setChanged(boolean changed){
        boolean oldChanged = this.changed;
        this.changed = changed;
        if (this.changed && !oldChanged) {
            VirtualChoirObjectUpdateObserver.getActual().addVirtualChoirObject(this);
        }
    }
    
    public boolean hasChanged(){
        return changed;
    }

    public DynamicLookup getLookup() {
        return lookup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        firePropertyChange(PROP_NAME, oldName, this.name);
    }

    public String getUUID() {
        return uuid;
    }
    
    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue){
        if (VirtualChoirObjectUpdateObserver.getActual() == null){
            VirtualChoirObjectUpdateObserver.setActual(new VirtualChoirObjectUpdateObserver(this));
        }
        setChanged(true);
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        if (VirtualChoirObjectUpdateObserver.getActual().getSource() == this){
            VirtualChoirObjectUpdateObserver.getActual().informAllVirtualChoirObjects();
        }
    }
            
    public void firePropertyChange(String propertyName, int oldValue, int newValue){
        if (VirtualChoirObjectUpdateObserver.getActual() == null){
            VirtualChoirObjectUpdateObserver.setActual(new VirtualChoirObjectUpdateObserver(this));
        }
        setChanged(true);
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        if (VirtualChoirObjectUpdateObserver.getActual().getSource() == this){
            VirtualChoirObjectUpdateObserver.getActual().informAllVirtualChoirObjects();
        }
    }
    
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue){
        if (VirtualChoirObjectUpdateObserver.getActual() == null){
            VirtualChoirObjectUpdateObserver.setActual(new VirtualChoirObjectUpdateObserver(this));
        }
        setChanged(true);
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        if (VirtualChoirObjectUpdateObserver.getActual().getSource() == this){
            VirtualChoirObjectUpdateObserver.getActual().informAllVirtualChoirObjects();
            changed = false;
        }
    }
    
    /*
     * Diese Methode informiert alle im Lookup des eLamX-Objekts vorhandenen
     * DependingObjects, also Objekte, deren Daten von denen des eLmaX-Objekts
     * abhängen.
     */
    public void informDependingObjects(){
        for (DependingObject d : lookup.lookupAll(DependingObject.class)){
            d.update();
        }
    }
    
    public void update(){
        informDependingObjects();
        changed = false;
    }
    
    public void delete(){
        lookup.clear();
    }
}
