/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.nodes;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.nodefactories.VirtualChoirVideosNodeFactory;
import java.awt.Image;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas Hauffe
 */
public class VirtualChoirVideosNode extends AbstractNode {

    public VirtualChoirVideosNode(VirtualChoir virtualChoir) {
        super(Children.create(new VirtualChoirVideosNodeFactory(virtualChoir), false), Lookups.fixed(virtualChoir));
    }

    protected Class[] cookieClasses() {
        return new Class[]{Index.class};
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/oscvev/virtualchoir/resources/SplitScreenVideo.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public Action[] getActions(boolean context) {
        return null;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(VirtualChoirVideosNode.class, "VirtualChoirVideosNodeName");
    }    
}