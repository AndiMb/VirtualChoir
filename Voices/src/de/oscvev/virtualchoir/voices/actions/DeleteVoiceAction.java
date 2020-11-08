/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.voices.actions;

import de.oscvev.virtualchoir.voices.DefaultVoice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

@ActionID(category = "VirtualChoirVoice",
id = "de.oscvev.virtualchoir.voices.actions.DeleteVoiceAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/voices/resources/deletevoices.png",
displayName = "#CTL_DeleteVoiceAction")
@ActionReferences({
    //@ActionReference(path = "Menu/VirtualChoirVoices", position = 100),
    //@ActionReference(path = "Toolbars/VirtualChoirVoices", position = 100),
    @ActionReference(path = "VirtualChoirActions/Voice", position = 300)
})
public class DeleteVoiceAction implements ActionListener {

    private final DefaultVoice voice;

    public DeleteVoiceAction(DefaultVoice voice) {
        this.voice = voice;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        voice.getVirtualChoir().removeVoice(voice);
    }
}