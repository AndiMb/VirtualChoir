/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.splitscreenvideo.node;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVideo;
import de.oscvev.virtualchoir.nodeprovider.VirtualChoirVideoNodeProvider;
import de.oscvev.virtualchoir.splitscreenvideo.DefaultSplitScreenVideo;
import org.openide.nodes.Node;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = VirtualChoirVideoNodeProvider.class)
public class DefaultSplitScreenVideoNodeProvider implements VirtualChoirVideoNodeProvider{

    @Override
    public String getClassName() {
        return DefaultSplitScreenVideo.class.getName();
    }

    @Override
    public Node getNode(VirtualChoir virtualChoir, VirtualChoirVideo video) {
        return new DefaultSplitScreenVideoNode(virtualChoir, (DefaultSplitScreenVideo)video);
    }
    
}