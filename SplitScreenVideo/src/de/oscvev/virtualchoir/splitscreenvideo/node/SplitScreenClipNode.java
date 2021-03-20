/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.splitscreenvideo.node;

import de.oscvev.virtualchoir.splitscreenvideo.SplitScreenClip;
import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas Hauffe
 */
public class SplitScreenClipNode extends AbstractNode {

    public SplitScreenClipNode(SplitScreenClip clip) {
        super(Children.LEAF, Lookups.fixed(clip));
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        
        SplitScreenClip clip = this.getLookup().lookup(SplitScreenClip.class);
        
        Sheet.Set generalProp = Sheet.createPropertiesSet();
        generalProp.setName("GeneralProperties");
        generalProp.setDisplayName(NbBundle.getMessage(SplitScreenClipNode.class, "SplitScreenClipNode.GeneralProperties"));

        try {
            PropertySupport.Reflection<String> nameProp = new PropertySupport.Reflection<>(clip, String.class, SplitScreenClip.PROP_NAME);
            nameProp.setDisplayName(NbBundle.getMessage(SplitScreenClipNode.class, "SplitScreenClipNode.Name"));
            generalProp.put(nameProp);
            
            PropertySupport.Reflection<Boolean> useVideoProp = new PropertySupport.Reflection<>(clip, boolean.class, SplitScreenClip.PROP_USEVIDEO);
            useVideoProp.setDisplayName(NbBundle.getMessage(SplitScreenClipNode.class, "SplitScreenClipNode.UseVideo"));
            generalProp.put(useVideoProp);
            
            PropertySupport.Reflection<Boolean> useAudioProp = new PropertySupport.Reflection<>(clip, boolean.class, SplitScreenClip.PROP_USEAUDIO);
            useAudioProp.setDisplayName(NbBundle.getMessage(SplitScreenClipNode.class, "SplitScreenClipNode.UseAudio"));
            generalProp.put(useAudioProp);
            
            PropertySupport.Reflection<Double> corrCoeffProp = new PropertySupport.Reflection<>(clip, double.class, SplitScreenClip.PROP_CORRELATIONCOEFFICIENT);
            corrCoeffProp.setDisplayName(NbBundle.getMessage(SplitScreenClipNode.class, "SplitScreenClipNode.corrcoeff"));
            generalProp.put(corrCoeffProp);
            
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        sheet.put(generalProp);
        
        return sheet;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/oscvev/virtualchoir/splitscreenvideo/resources/clip.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public String getDisplayName() {
        SplitScreenClip clip = this.getLookup().lookup(SplitScreenClip.class);
        return clip.getName();
    }
}