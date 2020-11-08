/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.voices.actions;

import de.oscvev.virtualchoir.core.VirtualChoirVideoClip;
import de.oscvev.virtualchoir.ffmpeg.FFMPEGUtilities;
import de.oscvev.virtualchoir.voices.DefaultVoice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import javax.swing.filechooser.FileFilter;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle;

@ActionID(category = "VirtualChoirVoice",
id = "de.oscvev.virtualchoir.voices.actions.SelectMasterVideoAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/voices/resources/addvoices.png",
displayName = "#CTL_SelectMasterVideoAction")
@ActionReferences({
    //@ActionReference(path = "Menu/VirtualChoirVoice", position = 100),
    //@ActionReference(path = "Toolbars/VirtualChoirVoice", position = 100),
    @ActionReference(path = "VirtualChoirActions/Voice", position = 100)
})
public final class SelectMasterVideoAction implements ActionListener {

    private final DefaultVoice voice;

    public SelectMasterVideoAction(DefaultVoice voice) {
        this.voice = voice;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File basePath = new File(System.getProperty("user.home"));
        File file = new FileChooserBuilder("mastervideodir").setTitle(NbBundle.getMessage(SelectMasterVideoAction.class, "OpenMasterFileDialog.Title")).setDefaultWorkingDirectory(basePath).setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                boolean result = false;
                for (String s : FFMPEGUtilities.getSupportedVideoFormats()){
                    result = result || name.endsWith(s);
                }
                for (String s : FFMPEGUtilities.getSupportedAudioFormats()){
                    result = result || name.endsWith(s);
                }
                return result || (f.isDirectory() && !f.isHidden());
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(SelectMasterVideoAction.class, "OpenMasterFileDialog.FileTypeDescription");
            }
        }).setSelectionApprover(new FileChooserBuilder.SelectionApprover() {
            @Override
            public boolean approve(File[] selection) {
                if (selection.length > 1) {
                    return false;
                }
                String name = selection[0].getName().toLowerCase();
                boolean result = false;
                for (String s : FFMPEGUtilities.getSupportedVideoFormats()){
                    result = result || name.endsWith(s);
                }
                for (String s : FFMPEGUtilities.getSupportedAudioFormats()){
                    result = result || name.endsWith(s);
                }
                return result;
            }
        }).setApproveText(NbBundle.getMessage(SelectMasterVideoAction.class, "OpenMasterFileDialog.ApproveText")).setFileHiding(true).showOpenDialog();
        if (file != null && file.exists()) {
            Path path = file.toPath();
            VirtualChoirVideoClip video = new VirtualChoirVideoClip(UUID.randomUUID().toString(), path.toString(), path, voice.getVirtualChoir(), true);
            voice.setMasterFile(video);
        }
    }
}
