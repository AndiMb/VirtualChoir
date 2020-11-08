/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.videocreator;

import de.oscvev.virtualchoid.videoutilities.VideoUtilities;
import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVideoClip;
import de.oscvev.virtualchoir.core.VirtualChoirVoice;
import de.oscvev.virtualchoir.splitscreenvideo.DefaultSplitScreenVideo;
import de.oscvev.virtualchoir.splitscreenvideo.SplitScreenClip;
import de.oscvev.virtualchoir.voices.DefaultVoice;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Andreas
 */
public class VideoCreator extends Thread {
    
    
    private final VirtualChoir virtualChoir;
    private String workingDirectory;
    
    private int clipHeight = -1;
    private int clipWidth = -1;

    public VideoCreator(VirtualChoir virtualChoir, String workingDirectory) {
        this.virtualChoir = virtualChoir;
        this.workingDirectory = workingDirectory;
    }

    public int getClipHeight() {
        return clipHeight;
    }

    public void setClipHeight(int clipHeight) {
        this.clipHeight = clipHeight;
    }

    public int getClipWidth() {
        return clipWidth;
    }

    public void setClipWidth(int clipWidth) {
        this.clipWidth = clipWidth;
    }
 
    @Override
    public void run() {
        try {
            createVideo();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    private void createVideo(){        
        final ProgressHandle progr = ProgressHandle.createHandle("Create Video");
        VideoUtilities vUtils = new VideoUtilities(workingDirectory);
        
        
        DefaultSplitScreenVideo splitScreenVideo = new DefaultSplitScreenVideo(UUID.randomUUID().toString(), "SplitScreenVideo", virtualChoir, true);
        virtualChoir.addVideo(splitScreenVideo);
        
        ArrayList<VirtualChoirVoice> voices = virtualChoir.getVoices();
        
        int totalClipCount = 0;
        for (VirtualChoirVoice vv : voices) {
            totalClipCount += ((DefaultVoice)vv).getVideoFiles().size();
        }
        progr.start(totalClipCount);
        ArrayList<Path> audioFiles = new ArrayList<>(totalClipCount);
        
        int videosPerSide = (int)(Math.sqrt(totalClipCount) - 0.01) + 1;
        
        int subClipWidth = (int)(1920.0 / (double)videosPerSide);
        int subClipHeight = (int)(1080.0 / (double)videosPerSide);
        
        int singleClipWidth = clipWidth < 1 ? subClipWidth : clipWidth;
        int singleClipHeight = clipHeight < 1 ? subClipHeight : clipHeight;
        
        int numberOfProcessedClips = 0;
        ArrayList<Path> videoClips = new ArrayList<>();
        for (VirtualChoirVoice vv : voices) {
            DefaultVoice voice = (DefaultVoice)vv;
            VirtualChoirVideoClip master = voice.getMasterFile();
            if (master == null) {
                return;
            }
            Path masterPath = voice.getMasterFile().getPath();

            Path masterWave = vUtils.extactOneAudioChannel(masterPath);
            double startOffset = master.getStartTime();
            double duration = master.getEndTime()-startOffset;

            for (VirtualChoirVideoClip clip : voice.getVideoFiles()) {
                Path clipWav = vUtils.extactOneAudioChannel(clip.getPath());
                double offset = vUtils.getOffsetFromWave(masterWave, clipWav, master.getStartTime(), master.getEndTime());
                clip.setOffset(offset);
                
                Path clipWavWithoutOffset = vUtils.removeOffsetFromWave(clipWav, clip.getOffset());
                try {
                    Files.delete(clipWav);
                    Files.move(clipWavWithoutOffset, clipWav);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                
                Path clipWavTrimmed = vUtils.trimAudio(clipWav, startOffset, duration);
                try {
                    Files.delete(clipWav);
                    Files.move(clipWavTrimmed, clipWav);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                
                vUtils.normalizeAudio(clipWav);
                audioFiles.add(clipWav);
                
                Path clipVidTrimmed = vUtils.trimVideo(clip.getPath(), startOffset+clip.getOffset(), duration, singleClipWidth, singleClipHeight);
                videoClips.add(clipVidTrimmed);
                
                SplitScreenClip ssClip = new SplitScreenClip(UUID.randomUUID().toString(), clipVidTrimmed.toString(), virtualChoir, true);
                ssClip.setAudioFile(clipWavTrimmed);
                ssClip.setVideoFile(clipVidTrimmed);
                ssClip.setUseAudio(true);
                ssClip.setUseVideo(true);
                String clipHash = "" + 
                        clip.getUUID() + ":" +
                        clip.getOffset() + ":" +
                        master.getStartTime() + ":" +
                        master.getEndTime() + ":" +
                        subClipWidth +  ":" +
                        subClipHeight;
                ssClip.setClipHash(clipHash);
                splitScreenVideo.addClip(ssClip);
                
                progr.progress(numberOfProcessedClips++);
            }
        }
        Path mixedAudioFile = vUtils.mix_audios(audioFiles);
        Path videoFile = vUtils.createSplitScreen(videoClips, subClipHeight, videosPerSide);
        Path videoAudioFile = vUtils.mergeAudioVideo(videoFile, mixedAudioFile);
        
        splitScreenVideo.setAudioPath(mixedAudioFile);
        splitScreenVideo.setVideoPath(videoFile);
        splitScreenVideo.setAudioVideoPath(videoAudioFile);
        
        progr.finish();
    }
}
