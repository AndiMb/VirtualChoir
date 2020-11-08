/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.voices.nodes;

import de.oscvev.virtualchoir.core.VirtualChoirVideoClip;
import de.oscvev.virtualchoir.voices.DefaultVoice;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

public class DefaultVideosNodeFactory extends ChildFactory<VirtualChoirVideoClip> implements PropertyChangeListener {

    private final DefaultVoice voice;

    public DefaultVideosNodeFactory(DefaultVoice voice) {
        this.voice = voice;
        voice.addPropertyChangeListener(WeakListeners.propertyChange(this, voice));
    }

    @Override
    protected boolean createKeys(List<VirtualChoirVideoClip> toPopulate) {
        List<VirtualChoirVideoClip> videos = voice.getVideoFiles();
        if (videos != null && !videos.isEmpty()) {
            toPopulate.addAll(videos);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(VirtualChoirVideoClip video) {
        return new DefaultVideoNode(video);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DefaultVoice.PROP_VIDEOFILES)) {
            this.refresh(true);
        }
    }
}