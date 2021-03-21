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
import java.util.HashMap;
import java.util.UUID;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author Andreas Hauffe
 */
public class VideoCreator extends Thread {

    private static final int SplitScreenVideoWidth = 1920;
    private static final int SplitScreenVideoHeight = 1080;

    private final VirtualChoir virtualChoir;
    private String workingDirectory;

    private int clipHeight = -1;
    private int clipWidth = -1;

    private DefaultSplitScreenVideo splitScreenVideo = null;
    
    private boolean useNVidia = false;

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

    public DefaultSplitScreenVideo getSplitScreenVideo() {
        return splitScreenVideo;
    }

    public void setSplitScreenVideo(DefaultSplitScreenVideo splitScreenVideo) {
        this.splitScreenVideo = splitScreenVideo;
    }
    
    public void setUseNvidia(boolean useNVidia){
        this.useNVidia = useNVidia;
    }
    
    public boolean isUseNVidia(){
        return useNVidia;
    }

    @Override
    public void run() {
        VideoUtilities vUtils = new VideoUtilities(workingDirectory);
        if (splitScreenVideo == null) {
            splitScreenVideo = new DefaultSplitScreenVideo(UUID.randomUUID().toString(), "SplitScreenVideo", virtualChoir, true);
            splitScreenVideo.setWorkingDirectory(workingDirectory);
            virtualChoir.addVideo(splitScreenVideo);
        }
        try {
            createVideo(splitScreenVideo, vUtils);
            //createSplitScreenVideo(splitScreenVideo, vUtils);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private DefaultSplitScreenVideo createVideo(DefaultSplitScreenVideo splitScreenVideo, VideoUtilities vUtils) {
        final ProgressHandle progr = ProgressHandle.createHandle("Create Video");

        HashMap<String, SplitScreenClip> alreadyDoneClips = new HashMap<>(splitScreenVideo.getClips().size());

        for (SplitScreenClip ssClip : splitScreenVideo.getClips()) {
            alreadyDoneClips.put(ssClip.getClipHash(), ssClip);
        }

        ArrayList<VirtualChoirVoice> voices = virtualChoir.getVoices();

        int totalClipCount = 0;
        for (VirtualChoirVoice vv : voices) {
            totalClipCount += ((DefaultVoice) vv).getVideoFiles().size();
        }
        progr.start(totalClipCount);

        int videosPerSide = (int) (Math.sqrt(totalClipCount) - 0.01) + 1;

        int subClipWidth = (int) (SplitScreenVideoWidth / (double) videosPerSide);
        int subClipHeight = (int) (SplitScreenVideoHeight / (double) videosPerSide);

        int singleClipWidth = clipWidth < 1 ? subClipWidth : clipWidth;
        int singleClipHeight = clipHeight < 1 ? subClipHeight : clipHeight;

        int numberOfProcessedClips = 0;
        for (VirtualChoirVoice vv : voices) {
            DefaultVoice voice = (DefaultVoice) vv;
            VirtualChoirVideoClip master = voice.getMasterFile();
            if (master == null) {
                return null;
            }
            Path masterPath = voice.getMasterFile().getPath();

            Path masterWave = vUtils.extactOneAudioChannel(masterPath);
            double startOffset = master.getStartTime();
            double duration = master.getEndTime() - startOffset;

            for (VirtualChoirVideoClip clip : voice.getVideoFiles()) {

                String clipHash = ""
                        + clip.getUUID() + ":"
                        + clip.getOffset() + ":"
                        + master.getStartTime() + ":"
                        + master.getEndTime() + ":"
                        + subClipWidth + ":"
                        + subClipHeight;

                if (!alreadyDoneClips.containsKey(clipHash) && !Double.isNaN(clip.getEndTime())) {
                    Path clipWav = vUtils.extactOneAudioChannel(clip.getPath());
                    double offset = vUtils.correlateWaves(masterWave, clipWav, master.getStartTime(), master.getEndTime(), VideoUtilities.PRAAT_RETURN_OFFSETMAX);
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

                    double corrCoeff = vUtils.correlateWaves(masterWave, clipWav, master.getStartTime(), master.getEndTime(), VideoUtilities.PRAAT_RETURN_VALUEABSMAX);

                    Path clipVidTrimmed = vUtils.trimVideo(clip.getPath(), startOffset + clip.getOffset(), duration, singleClipWidth, singleClipHeight, clip.getRotation(), useNVidia);

                    clipHash = ""
                            + clip.getUUID() + ":"
                            + clip.getOffset() + ":"
                            + master.getStartTime() + ":"
                            + master.getEndTime() + ":"
                            + subClipWidth + ":"
                            + subClipHeight;

                    SplitScreenClip ssClip = new SplitScreenClip(UUID.randomUUID().toString(), clipVidTrimmed.toString(), virtualChoir, true);
                    ssClip.setAudioFile(clipWav);
                    ssClip.setVideoFile(clipVidTrimmed);
                    ssClip.setUseAudio(true);
                    ssClip.setUseVideo(true);
                    ssClip.setMasterfile(masterPath);
                    ssClip.setCorrelationCoefficient(corrCoeff);
                    ssClip.setClipHash(clipHash);
                    splitScreenVideo.addClip(ssClip);
                }

                progr.progress(numberOfProcessedClips++);
            }
        }

        createSplitScreenVideo(splitScreenVideo, vUtils);

        progr.finish();

        return splitScreenVideo;
    }

    private void createSplitScreenVideo(DefaultSplitScreenVideo splitScreenVideo, VideoUtilities vUtils) {

        int totalVideoClipCount = 0;
        int totalAudioFileCount = 0;
        for (SplitScreenClip ssc : splitScreenVideo.getClips()) {
            if (ssc.isUseAudio() && Files.exists(ssc.getAudioFile())) {
                totalAudioFileCount++;
            }
            if (ssc.isUseVideo() && Files.exists(ssc.getVideoFile())) {
                totalVideoClipCount++;
            }
        }

        ArrayList<Path> videoClips = new ArrayList<>(totalVideoClipCount);
        ArrayList<Path> audioFiles = new ArrayList<>(totalAudioFileCount);

        for (SplitScreenClip ssc : splitScreenVideo.getClips()) {
            if (ssc.isUseAudio()) {
                audioFiles.add(ssc.getAudioFile());
            }
            if (ssc.isUseVideo()) {
                videoClips.add(ssc.getVideoFile());
            }
        }

        int videosPerSide = (int) (Math.sqrt(totalVideoClipCount) - 0.01) + 1;

        int subClipHeight = (int) (SplitScreenVideoHeight / (double) videosPerSide);

        if (subClipHeight % 2 != 0) {
            subClipHeight--;
        }

        Path mixedAudioFile = vUtils.mix_audios(audioFiles);
        splitScreenVideo.setAudioPath(mixedAudioFile);

        Path videoFile = vUtils.createSplitScreen(videoClips, subClipHeight, videosPerSide);
        splitScreenVideo.setVideoPath(videoFile);

        Path videoAudioFile = vUtils.mergeAudioVideo(videoFile, mixedAudioFile);
        splitScreenVideo.setAudioVideoPath(videoAudioFile);

    }
}
