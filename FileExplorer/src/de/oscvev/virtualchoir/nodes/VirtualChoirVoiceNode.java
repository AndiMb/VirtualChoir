/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.nodes;

import de.oscvev.virtualchoir.core.VirtualChoirVoice;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 *
 * @author Andreas
 */
public class VirtualChoirVoiceNode extends AbstractNode implements PropertyChangeListener {

    private final VirtualChoirVoice voice;

    public VirtualChoirVoiceNode(Lookup lookup) {
        this(Children.LEAF, lookup);
    }

    public VirtualChoirVoiceNode(Children children, Lookup lookup) {
        super(children, lookup);
        voice = lookup.lookup(VirtualChoirVoice.class);
        voice.addPropertyChangeListener(WeakListeners.propertyChange(this, voice));
    }
    
    public Lookup getMyLookup(){
        return getLookup();
    }

    @Override
    public void setName(String s) {
        getLookup().lookup(VirtualChoirVoice.class).setName(s);
    }

    @Override
    public String getName() {
        return getLookup().lookup(VirtualChoirVoice.class).getName();
    }

    @Override
    public Action[] getActions(boolean popup) {
        List<? extends Action> myActions = Utilities.actionsForPath("SandMeshActions/SandMeshFeatures");
        return myActions.toArray(new Action[myActions.size()]);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        this.fireDisplayNameChange(null, this.getHtmlDisplayName());
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName("GeneralProperties");
        generalProp.setDisplayName(NbBundle.getMessage(VirtualChoirVoiceNode.class, "VirtualChoirVoiceNode.GeneralProperties"));

        try {
            PropertySupport.Reflection<String> nameProp = new PropertySupport.Reflection<>(voice, String.class, VirtualChoirVoiceNode.PROP_NAME);
            nameProp.setDisplayName(NbBundle.getMessage(VirtualChoirVoiceNode.class, "VirtualChoirVoiceNode.Name"));
            generalProp.put(nameProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        sheet.put(generalProp);

        return sheet;
    }
}
