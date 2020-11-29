/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.videocreator.actions;

import de.oscvev.virtualchoir.splitscreenvideo.DefaultSplitScreenVideo;
import de.oscvev.virtualchoir.videocreator.VideoCreator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.RequestProcessor;

@ActionID(category = "VirtualChoirVoice",
        id = "de.oscvev.virtualchoir.videocreator.actions.RecreateVideoAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/videocreator/resources/createvideo.png",
        displayName = "#CTL_RecreateVideoAction")
@ActionReferences({
    //@ActionReference(path = "Menu/VirtualChoirVoice", position = 100),
    @ActionReference(path = "VirtualChoirActions/Video", position = 100)
})
public class RecreateVideoAction implements ActionListener {

    private final DefaultSplitScreenVideo video;

    public RecreateVideoAction(DefaultSplitScreenVideo video) {
        this.video = video;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        VideoCreator export = new VideoCreator(video.getVirtualChoir(), video.getWorkingDirectory());
        export.setSplitScreenVideo(video);
        RequestProcessor.getDefault().post(export);
    }
}