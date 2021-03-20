/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.nodefactories;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVideo;
import de.oscvev.virtualchoir.nodeprovider.VirtualChoirVideoNodeProvider;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
public class VirtualChoirVideosNodeFactory extends ChildFactory<VirtualChoirVideo> implements PropertyChangeListener, LookupListener {

    private final VirtualChoir virtualChoir;
    private final Lookup.Result<VirtualChoirVideoNodeProvider> result;
    private final HashMap<String, VirtualChoirVideoNodeProvider> nodeProviderMap = new HashMap<>();

    public VirtualChoirVideosNodeFactory(VirtualChoir virtualChoir) {
        this.virtualChoir = virtualChoir;
        this.virtualChoir.addPropertyChangeListener(this);
        result = Lookup.getDefault().lookupResult(VirtualChoirVideoNodeProvider.class);
        refreshNodeProviderMap();
        result.addLookupListener(this);
    }

    @Override
    protected boolean createKeys(List<VirtualChoirVideo> toPopulate) {
        ArrayList<VirtualChoirVideo> features = virtualChoir.getVideos();

        if (features != null) {
            toPopulate.addAll(features);

            // Sortieren der Liste nach den Name der Materialien
            Collections.sort(toPopulate, new Comparator<VirtualChoirVideo>() {
                @Override
                public int compare(VirtualChoirVideo o1, VirtualChoirVideo o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(VirtualChoirVideo key) {
        return nodeProviderMap.get(key.getClass().getName()).getNode(virtualChoir, key);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        refreshNodeProviderMap();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VirtualChoir.PROP_VIDEO)) {
            this.refresh(true);
        }
    }

    private void refreshNodeProviderMap() {
        nodeProviderMap.clear();
        for (VirtualChoirVideoNodeProvider spnp : result.allInstances()) {
            nodeProviderMap.put(spnp.getClassName(), spnp);
        }
    }
}