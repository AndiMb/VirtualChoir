/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.nodefactories;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVoice;
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
import de.oscvev.virtualchoir.nodeprovider.VirtualChoirVoiceNodeProvider;

/**
 *
 * @author Andreas
 */
public class VirtualChoirVoicesNodeFactory extends ChildFactory<VirtualChoirVoice> implements PropertyChangeListener, LookupListener {

    private final VirtualChoir virtualChoir;
    private final Lookup.Result<VirtualChoirVoiceNodeProvider> result;
    private final HashMap<String, VirtualChoirVoiceNodeProvider> nodeProviderMap = new HashMap<>();

    public VirtualChoirVoicesNodeFactory(VirtualChoir virtualChoir) {
        this.virtualChoir = virtualChoir;
        this.virtualChoir.addPropertyChangeListener(this);
        result = Lookup.getDefault().lookupResult(VirtualChoirVoiceNodeProvider.class);
        refreshNodeProviderMap();
        result.addLookupListener(this);
    }

    @Override
    protected boolean createKeys(List<VirtualChoirVoice> toPopulate) {
        ArrayList<VirtualChoirVoice> features = virtualChoir.getVoices();

        if (features != null) {
            toPopulate.addAll(features);

            // Sortieren der Liste nach den Name der Materialien
            Collections.sort(toPopulate, new Comparator<VirtualChoirVoice>() {
                @Override
                public int compare(VirtualChoirVoice o1, VirtualChoirVoice o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(VirtualChoirVoice key) {
        return nodeProviderMap.get(key.getClass().getName()).getNode(virtualChoir, key);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        refreshNodeProviderMap();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(VirtualChoir.PROP_VOICE)) {
            this.refresh(true);
        }
    }

    private void refreshNodeProviderMap() {
        nodeProviderMap.clear();
        for (VirtualChoirVoiceNodeProvider spnp : result.allInstances()) {
            nodeProviderMap.put(spnp.getClassName(), spnp);
        }
    }
}