/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.voices.nodes;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.nodes.VirtualChoirVoiceNode;
import de.oscvev.virtualchoir.core.VirtualChoirVideoClip;
import de.oscvev.virtualchoir.voices.DefaultVoice;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.*;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas Hauffe
 */
public class DefaultVoiceNode extends VirtualChoirVoiceNode {

    public DefaultVoiceNode(VirtualChoir virtualChoir, DefaultVoice voice) {
        super(Children.create(new DefaultVideosNodeFactory(voice), false), Lookups.fixed(virtualChoir, voice));
        voice.addPropertyChangeListener(WeakListeners.propertyChange(this, voice));
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        
        Sheet.Set videoProps = Sheet.createPropertiesSet();
        videoProps.setName("VideoProperties");
        videoProps.setDisplayName(NbBundle.getMessage(DefaultVoiceNode.class, "VirtualChoirVideoClip.VideoProperties"));

        DefaultVoice voice = this.getLookup().lookup(DefaultVoice.class);
        VirtualChoirVideoClip video = voice.getMasterFile();
        
        if (video != null){
            try {
                PropertySupport.Reflection<Double> lengthProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_CLIPLENGTH);
                lengthProp.setDisplayName(NbBundle.getMessage(DefaultVoiceNode.class, "VirtualChoirVideoClip.cliplength"));
                videoProps.put(lengthProp);

                PropertySupport.Reflection<Double> startProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_STARTTIME);
                startProp.setDisplayName(NbBundle.getMessage(DefaultVoiceNode.class, "VirtualChoirVideoClip.start"));
                videoProps.put(startProp);

                PropertySupport.Reflection<Double> endProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_ENDTIME);
                endProp.setDisplayName(NbBundle.getMessage(DefaultVoiceNode.class, "VirtualChoirVideoClip.end"));
                videoProps.put(endProp);

                PropertySupport.Reflection<Double> offsetProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_OFFSET);
                offsetProp.setDisplayName(NbBundle.getMessage(DefaultVoiceNode.class, "VirtualChoirVideoClip.offset"));
                videoProps.put(offsetProp);

                PropertySupport.Reflection<Double> rotationProp = new PropertySupport.Reflection<>(video, double.class, VirtualChoirVideoClip.PROP_ROTATION);
                rotationProp.setDisplayName(NbBundle.getMessage(DefaultVoiceNode.class, "VirtualChoirVideoClip.rotation"));
                videoProps.put(rotationProp);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        sheet.put(videoProps);
        
        return sheet;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/oscvev/virtualchoir/voices/resources/voice.png");
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
        VirtualChoirVideoClip video = this.getLookup().lookup(DefaultVoice.class).getMasterFile();
        String filename = NbBundle.getMessage(DefaultVoiceNode.class, "DefaultVoiceNode.nomasterfile");
        if (video != null){
            filename = video.getName();
        }
        return super.getDisplayName() + " (" + filename + ")";
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        if (evt.getPropertyName().equals(DefaultVoice.PROP_MASTERFILE)){
            this.setSheet(createSheet());
        }
    }
}
