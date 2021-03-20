/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.*;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Andreas Hauffe
 */
public class VirtualChoirLookup extends AbstractLookup implements PropertyChangeListener {
    public final static String PROP_FILEOBJECT = "fileObject";
    private static VirtualChoirLookup lookup = new VirtualChoirLookup();
    private final InstanceContent content;
    private boolean notifyDataObject = false;
    private boolean changable = true;

    private VirtualChoirLookup() {
        this(new InstanceContent());
    }

    private VirtualChoirLookup(InstanceContent ic) {
        super(ic);
        this.content = ic;
    }

    public boolean isChangable() {
        return changable;
    }

    public void setChangable(boolean changable) {
        this.changable = changable;
    }

    public void clear() {
        if (!changable){
            return;
        }
        for (VirtualChoirObject o : lookupAll(VirtualChoirObject.class)) {
            remove(o);
        }
    }

    public void add(VirtualChoirObject instance) {
        if (!changable || fileObject == null) {
            return;
        }
        instance.addPropertyChangeListener(this);
        content.add(instance);
        if (notifyDataObject && !dataOb.isModified()) {
            dataOb.setModified(true);
        }
    }

    public void remove(VirtualChoirObject instance) {
        if (!changable || fileObject == null) {
            return;
        }
        instance.delete();
        instance.removePropertyChangeListener(this);
        content.remove(instance);
        if (notifyDataObject && !dataOb.isModified()) {
            dataOb.setModified(true);
        }
    }

    public static VirtualChoirLookup getDefault() {
        return lookup;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChanged(evt);
        if (notifyDataObject && !dataOb.isModified()) {
            dataOb.setModified(true);
        }
    }
    private final ArrayList<PropertyChangeListener> listeners = new ArrayList<>();

    public void addPropertyChangeListener(PropertyChangeListener l) {
        listeners.add(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        listeners.remove(l);
    }

    protected final void firePropertyChanged(PropertyChangeEvent e) {
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(e);
        }
    }

    private FileObject fileObject;
    private DataObject dataOb;

    public void setFileObject(FileObject fo) {
        if (fo != null && fo != fileObject) {
            try {
                notifyDataObject = false;
                if (dataOb != null) {
                    dataOb.setValid(false);
                }
                dataOb = null;
                //fileObject = null;
                clear();
                fileObject = fo;
                dataOb = DataObject.find(fo);
                firePropertyChanged(new PropertyChangeEvent(this, PROP_FILEOBJECT, null, fileObject));
                notifyDataObject = true;
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    public DataObject getDataObject() {
        return dataOb;
    }
    
    public void setDataObject(DataObject dataOb){
        notifyDataObject = false;
        this.dataOb = dataOb;
        this.fileObject = this.dataOb.getPrimaryFile();
        firePropertyChanged(new PropertyChangeEvent(this, PROP_FILEOBJECT, null, fileObject));
        notifyDataObject = true;
    }
    
    public void setModified(boolean value){
        if (dataOb != null){
            dataOb.setModified(value);
        }
    }
}
