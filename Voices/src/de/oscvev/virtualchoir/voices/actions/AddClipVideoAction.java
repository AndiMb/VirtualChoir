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
        id = "de.oscvev.virtualchoir.voices.actions.AddClipVideoAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/voices/resources/addvoices.png",
        displayName = "#CTL_AddClipVideoAction")
@ActionReferences({
    //@ActionReference(path = "Menu/VirtualChoirVoice", position = 200),
    //@ActionReference(path = "Toolbars/VirtualChoirVoice", position = 200),
    @ActionReference(path = "VirtualChoirActions/Voice", position = 200, separatorAfter = 250)
})
public final class AddClipVideoAction implements ActionListener {

    private final DefaultVoice voice;

    public AddClipVideoAction(DefaultVoice voice) {
        this.voice = voice;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File basePath = new File(System.getProperty("user.home"));
        File[] files = new FileChooserBuilder("videoclipdir").setTitle(NbBundle.getMessage(AddClipVideoAction.class, "AddClipFileDialog.Title")).setDefaultWorkingDirectory(basePath).setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                boolean result = false;
                for (String s : FFMPEGUtilities.getSupportedVideoFormats()) {
                    result = result || name.endsWith(s);
                }
                return result || (f.isDirectory() && !f.isHidden());
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(AddClipVideoAction.class, "AddClipFileDialog.FileTypeDescription");
            }

        }).setSelectionApprover(new FileChooserBuilder.SelectionApprover() {
            @Override
            public boolean approve(File[] selection) {
                boolean result = true;
                if (selection.length > 0) {
                    for (File f : selection) {
                        String name = f.getName().toLowerCase();
                        boolean b = false;
                        for (String s : FFMPEGUtilities.getSupportedVideoFormats()) {
                            b = b || name.endsWith(s);
                        }
                        result = result && b;
                    }
                } else {
                    result = false;
                }
                return result;
            }
        }).setApproveText(NbBundle.getMessage(AddClipVideoAction.class, "AddClipFileDialog.ApproveText")).setFileHiding(true).showMultiOpenDialog();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file != null && file.exists()) {
                    Path path = file.toPath();
                    VirtualChoirVideoClip video = new VirtualChoirVideoClip(UUID.randomUUID().toString(), path.toString(), path, voice.getVirtualChoir(), true);
                    voice.addVideoFile(video);
                }
            }
        }
    }
}
