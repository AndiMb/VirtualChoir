/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.voices.nodes;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVoice;
import de.oscvev.virtualchoir.nodeprovider.VirtualChoirVoiceNodeProvider;
import de.oscvev.virtualchoir.voices.DefaultVoice;
import org.openide.nodes.Node;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = VirtualChoirVoiceNodeProvider.class)
public class DefaultVoiceNodeProvider implements VirtualChoirVoiceNodeProvider{

    @Override
    public String getClassName() {
        return DefaultVoice.class.getName();
    }

    @Override
    public Node getNode(VirtualChoir virtualChoir, VirtualChoirVoice voice) {
        return new DefaultVoiceNode(virtualChoir, (DefaultVoice)voice);
    }
    
}
