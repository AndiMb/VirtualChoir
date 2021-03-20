/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.nodefactories;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.nodes.VirtualChoirVideosNode;
import de.oscvev.virtualchoir.nodes.VirtualChoirVoicesNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Andreas Hauffe
 */
public class VirtualChoirNodeFactory extends ChildFactory<String> {

    private final VirtualChoir virtualChoir;

    public VirtualChoirNodeFactory(VirtualChoir virtualChoir) {
        this.virtualChoir = virtualChoir;
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        toPopulate.add("VoicesNode");
        toPopulate.add("VideoNode");
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        if (key.equals("VoicesNode")){
            return new VirtualChoirVoicesNode(virtualChoir);
        }else if (key.equals("VideoNode")){
            return new VirtualChoirVideosNode(virtualChoir);
        }
        return null;
    }

}
