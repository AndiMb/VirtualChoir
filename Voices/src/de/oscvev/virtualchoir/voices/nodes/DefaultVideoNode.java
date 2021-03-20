/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.voices.nodes;

import de.oscvev.virtualchoir.core.VirtualChoirVideoClip;
import java.awt.Image;
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
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas Hauffe
 */
public class DefaultVideoNode extends AbstractNode {

    public DefaultVideoNode(VirtualChoirVideoClip video) {
        super(Children.LEAF, Lookups.fixed(video));
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        
        Sheet.Set videoProps = Sheet.createPropertiesSet();
        videoProps.setName("VideoProperties");
        videoProps.setDisplayName(NbBundle.getMessage(DefaultVideoNode.class, "VirtualChoirVideoClip.VideoProperties"));

        VirtualChoirVideoClip video = this.getLookup().lookup(VirtualChoirVideoClip.class);
        
        try {
            PropertySupport.Reflection<Double> lengthProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_CLIPLENGTH);
            lengthProp.setDisplayName(NbBundle.getMessage(DefaultVideoNode.class, "VirtualChoirVideoClip.cliplength"));
            videoProps.put(lengthProp);
            
            PropertySupport.Reflection<Double> startProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_STARTTIME);
            startProp.setDisplayName(NbBundle.getMessage(DefaultVideoNode.class, "VirtualChoirVideoClip.start"));
            videoProps.put(startProp);
            
            PropertySupport.Reflection<Double> endProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_ENDTIME);
            endProp.setDisplayName(NbBundle.getMessage(DefaultVideoNode.class, "VirtualChoirVideoClip.end"));
            videoProps.put(endProp);
            
            PropertySupport.Reflection<Double> offsetProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_OFFSET);
            offsetProp.setDisplayName(NbBundle.getMessage(DefaultVideoNode.class, "VirtualChoirVideoClip.offset"));
            videoProps.put(offsetProp);
            
            PropertySupport.Reflection<Double> rotationProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_ROTATION);
            rotationProp.setDisplayName(NbBundle.getMessage(DefaultVideoNode.class, "VirtualChoirVideoClip.rotation"));
            videoProps.put(rotationProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        sheet.put(videoProps);
        
        return sheet;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/oscvev/virtualchoir/voices/resources/video.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        List<? extends Action> myActions = Utilities.actionsForPath("VirtualChoirActions/Voice");
        return myActions.toArray(new Action[myActions.size()]);
    }

    @Override
    public String getDisplayName() {
        VirtualChoirVideoClip video = this.getLookup().lookup(VirtualChoirVideoClip.class);
        return video.getName();
    }
}