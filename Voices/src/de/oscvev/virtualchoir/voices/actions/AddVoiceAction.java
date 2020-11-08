/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.voices.actions;

import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.voices.DefaultVoice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "VirtualChoirVoices",
id = "de.oscvev.virtualchoir.voices.actions.AddVoiceAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/voices/resources/addvoices.png",
displayName = "#CTL_AddVoiceAction")
@ActionReferences({
    //@ActionReference(path = "Menu/VirtualChoirVoices", position = 100),
    //@ActionReference(path = "Toolbars/VirtualChoirVoices", position = 100),
    @ActionReference(path = "VirtualChoirActions/Voices", position = 100)
})
public final class AddVoiceAction implements ActionListener {

    private final VirtualChoir virtualChoir;

    public AddVoiceAction(VirtualChoir virtualChoir) {
        this.virtualChoir = virtualChoir;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultVoice voice = new DefaultVoice(UUID.randomUUID().toString(), NbBundle.getMessage(AddVoiceAction.class, "AddVoiceAction.NewVoice"), virtualChoir);
        virtualChoir.addVoice(voice);
    }
}
