/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.nodes;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.nodefactories.VirtualChoirNodeFactory;
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
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas
 */
public class VirtualChoirNode extends AbstractNode implements PropertyChangeListener {

    private final VirtualChoir virtualChoir;

    public VirtualChoirNode(VirtualChoir virtualChoir) {
        super(Children.create(new VirtualChoirNodeFactory(virtualChoir), false), Lookups.fixed(virtualChoir));
        this.virtualChoir = virtualChoir;
        virtualChoir.addPropertyChangeListener(WeakListeners.propertyChange(this, virtualChoir));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/oscvev/virtualchoir/fileexplorer/resources/virtualchoir.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> sandwichActions = Utilities.actionsForPath("VirtualChoirActions/VirtualChoir");
        return sandwichActions.toArray(new Action[sandwichActions.size()]);
    }

    @Override
    public String getDisplayName() {
        return virtualChoir.getName();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VirtualChoir.PROP_NAME)) {
            this.fireDisplayNameChange(null, this.getHtmlDisplayName());
        }
    }
    
    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();

        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName("GeneralProperties");
        generalProp.setDisplayName(NbBundle.getMessage(VirtualChoirNode.class, "VirtualChoirNode.GeneralProperties"));

        try {
            PropertySupport.Reflection<String> nameProp = new PropertySupport.Reflection<>(virtualChoir, String.class, VirtualChoir.PROP_NAME);
            nameProp.setDisplayName(NbBundle.getMessage(VirtualChoirNode.class, "VirtualChoirNode.Name"));
            generalProp.put(nameProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        sheet.put(generalProp);

        return sheet;
    }
}