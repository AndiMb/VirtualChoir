/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.splitscreenvideo;

import de.oscvev.virtualchoir.core.VideoPathProvider;
import de.oscvev.virtualchoir.core.VirtualChoir;
import de.oscvev.virtualchoir.core.VirtualChoirVideo;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andreas
 */
public class DefaultSplitScreenVideo extends VirtualChoirVideo implements VideoPathProvider{

    public static final String PROP_CLIPS = "clips";
    public static final String PROP_VIDEOPATH = "videoPath";
    public static final String PROP_AUDIOPATH = "audioPath";
    public static final String PROP_AUDIOVIDEOPATH = "audioVideoPath";
    public static final String PROP_WORKINGDIRECTORY = "workingDirectory";

    private final ArrayList<SplitScreenClip> clips = new ArrayList<>();
    private Path videoPath;
    private Path audioPath;
    private Path audioVideoPath;
    private String workingDirectory;
   
    public DefaultSplitScreenVideo(String uuid, String name, VirtualChoir vChoir, boolean addToLookup) {
        super(uuid, name, vChoir, addToLookup);
    }

    public List<SplitScreenClip> getClips() {
        return clips;
    }

    public void addClip(SplitScreenClip clip) {
        clips.add(clip);
        firePropertyChange(PROP_CLIPS, null, clips);
    }

    public void addClips(ArrayList<SplitScreenClip> clips) {
        this.clips.addAll(clips);
        firePropertyChange(PROP_CLIPS, null, this.clips);
    }

    public Path getAudioVideoPath() {
        return audioVideoPath;
    }

    public void setAudioVideoPath(Path audioVideoPath) {
        Path oldAudioVideoPath = this.audioVideoPath;
        this.audioVideoPath = audioVideoPath;
        firePropertyChange(PROP_AUDIOVIDEOPATH, oldAudioVideoPath, audioVideoPath);
    }

    public Path getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(Path audioPath) {
        Path oldAudioPath = this.audioPath;
        this.audioPath = audioPath;
        firePropertyChange(PROP_AUDIOPATH, oldAudioPath, audioPath);
    }

    public Path getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(Path videoPath) {
        Path oldVideoPath = this.videoPath;
        this.videoPath = videoPath;
        firePropertyChange(PROP_VIDEOPATH, oldVideoPath, videoPath);
    }

    @Override
    public int getUpdatePriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Path getVideoClipPath() {
        return audioVideoPath;
    }   
    
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        String oldWorkingDirectory = this.workingDirectory;
        this.workingDirectory = workingDirectory;
        firePropertyChange(PROP_WORKINGDIRECTORY, oldWorkingDirectory, workingDirectory);
    }
}
