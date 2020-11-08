/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.core.actions;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirLookup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Sandwiches",
id = "de.oscvev.virtualchoir.core.actions.AddVirtualChoirAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/core/resources/addvirtualchoir.png",
displayName = "#CTL_AddVirtualChoirAction")
@ActionReferences({
    //@ActionReference(path = "Menu/VirtualChoirs", position = 0),
    @ActionReference(path = "Toolbars/VirtualChoirs", position = 100),
    //@ActionReference(path = "virtualChoirActions/VirtualChoirs", position = 0)
})
public final class AddVirtualChoirAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        VirtualChoir vc = new VirtualChoir(UUID.randomUUID().toString(), NbBundle.getMessage(AddVirtualChoirAction.class, "AddVirtualChoirAction.NewVirtualChoir"));
        VirtualChoirLookup.getDefault().add(vc);
    }
}
