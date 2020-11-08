/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.nodeprovider;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVoice;
import org.openide.nodes.Node;

/**
 *
 * @author Andreas
 */
public interface VirtualChoirVoiceNodeProvider {
    
    public String getClassName();
    
    public Node getNode(VirtualChoir virtualChoir, VirtualChoirVoice voice);
    
}
