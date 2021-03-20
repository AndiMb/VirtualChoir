/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.nodes;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.nodefactories.VirtualChoirVoicesNodeFactory;
import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas Hauffe
 */
public class VirtualChoirVoicesNode extends AbstractNode {

    public VirtualChoirVoicesNode(VirtualChoir virtualChoir) {
        super(Children.create(new VirtualChoirVoicesNodeFactory(virtualChoir), false), Lookups.fixed(virtualChoir));
    }

    protected Class[] cookieClasses() {
        return new Class[]{Index.class};
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/oscvev/virtualchoir/resources/voices.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> myActions = Utilities.actionsForPath("VirtualChoirActions/Voices");
        return myActions.toArray(new Action[myActions.size()]);
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(VirtualChoirVoicesNode.class, "VirtualChoirVoicesNodeName");
    }    
}