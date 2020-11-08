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
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Andreas
 */
public class SplitScreenClipNode extends AbstractNode {

    public SplitScreenClipNode(SplitScreenClip clip) {
        super(Children.LEAF, Lookups.fixed(clip));
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        
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