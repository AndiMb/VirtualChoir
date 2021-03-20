/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.nodefactories;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirLookup;
import de.oscvev.virtualchoir.nodes.VirtualChoirNode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Andreas Hauffe
 */
public class VirtualChoirsNodeFactory extends ChildFactory<VirtualChoir> implements LookupListener{

    private Lookup.Result<VirtualChoir> result = null;
    
    public VirtualChoirsNodeFactory() {
        result = VirtualChoirLookup.getDefault().lookupResult(VirtualChoir.class);
        result.addLookupListener(this);
    }
    
    @Override
    protected boolean createKeys(List<VirtualChoir> list) {
        list.addAll(result.allInstances());
        // Sortieren der Liste nach den Name der Materialien
        Collections.sort(list, new Comparator<VirtualChoir>(){
            @Override
            public int compare(VirtualChoir o1, VirtualChoir o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return true;
    }

    @Override
    protected Node createNodeForKey(VirtualChoir virtualChoir) {
        return new VirtualChoirNode(virtualChoir);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        this.refresh(true);
    }
    
}
