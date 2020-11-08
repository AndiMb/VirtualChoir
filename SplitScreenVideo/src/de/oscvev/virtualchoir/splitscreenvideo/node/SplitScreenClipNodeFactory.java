/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.splitscreenvideo.node;

import de.oscvev.virtualchoir.splitscreenvideo.DefaultSplitScreenVideo;
import de.oscvev.virtualchoir.splitscreenvideo.SplitScreenClip;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author Andreas
 */
public class SplitScreenClipNodeFactory extends ChildFactory<SplitScreenClip> implements PropertyChangeListener {

    private final DefaultSplitScreenVideo video;

    public SplitScreenClipNodeFactory(DefaultSplitScreenVideo video) {
        this.video = video;
        video.addPropertyChangeListener(WeakListeners.propertyChange(this, video));
    }

    @Override
    protected boolean createKeys(List<SplitScreenClip> toPopulate) {
        List<SplitScreenClip> videos = video.getClips();
        if (videos != null && !videos.isEmpty()) {
            toPopulate.addAll(videos);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(SplitScreenClip video) {
        return new SplitScreenClipNode(video);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DefaultSplitScreenVideo.PROP_CLIPS)) {
            this.refresh(true);
        }
    }
}