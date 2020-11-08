/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.splitscreenvideo.node;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.nodes.VirtualChoirVideoNode;
import de.oscvev.virtualchoir.splitscreenvideo.DefaultSplitScreenVideo;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.*;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas
 */
public class DefaultSplitScreenVideoNode extends VirtualChoirVideoNode {

    public DefaultSplitScreenVideoNode(VirtualChoir virtualChoir, DefaultSplitScreenVideo video) {
        super(Children.create(new SplitScreenClipNodeFactory(video), false), Lookups.fixed(virtualChoir, video));
        video.addPropertyChangeListener(WeakListeners.propertyChange(this, video));
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();        
        return sheet;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/oscvev/virtualchoir/splitscreenvideo/resources/SplitScreenVideo.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        List<? extends Action> myActions = Utilities.actionsForPath("VirtualChoirActions/Video");
        return myActions.toArray(new Action[myActions.size()]);
    }

    @Override
    public String getDisplayName() {
        return "Split Screen Video";
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        /*if (evt.getPropertyName().equals(DefaultVoice.PROP_MASTERFILE)){
            this.setSheet(createSheet());
        }*/
    }
}