/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.videocreator.actions;

import de.oscvev.virtualchoir.splitscreenvideo.DefaultSplitScreenVideo;
import de.oscvev.virtualchoir.splitscreenvideo.SplitScreenClip;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

@ActionID(category = "VirtualChoirVoice",
        id = "de.oscvev.virtualchoir.videocreator.actions.CheckClipsAction")
@ActionRegistration(iconBase = "de/oscvev/virtualchoir/videocreator/resources/createvideo.png",
        displayName = "#CTL_CheckClipsAction")
@ActionReferences({
    //@ActionReference(path = "Menu/VirtualChoirVoice", position = 100),
    @ActionReference(path = "VirtualChoirActions/Video", position = 100)
})
public class CheckClipsAction implements ActionListener {

    private final DefaultSplitScreenVideo video;

    public CheckClipsAction(DefaultSplitScreenVideo video) {
        this.video = video;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (SplitScreenClip clip : video.getClips()){
            if (clip.getVideoFile().toString().toLowerCase().contains("sopran") && clip.getCorrelationCoefficient() < 200000.0){
                clip.setUseAudio(false);
                clip.setUseVideo(false);
            }
            if (clip.getVideoFile().toString().toLowerCase().contains("alt") && clip.getCorrelationCoefficient() < 270000.0){
                clip.setUseAudio(false);
                clip.setUseVideo(false);
            }
            if (clip.getVideoFile().toString().toLowerCase().contains("tenor") && clip.getCorrelationCoefficient() < 150000.0){
                clip.setUseAudio(false);
                clip.setUseVideo(false);
            }
            if (clip.getVideoFile().toString().toLowerCase().contains("bass") && clip.getCorrelationCoefficient() < 40000.0){
                clip.setUseAudio(false);
                clip.setUseVideo(false);
            }
        }
    }
}