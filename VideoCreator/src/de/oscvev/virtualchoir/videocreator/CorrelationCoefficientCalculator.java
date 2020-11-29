/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoir.videocreator;

import de.oscvev.virtualchoid.videoutilities.VideoUtilities;
import de.oscvev.virtualchoir.splitscreenvideo.DefaultSplitScreenVideo;
import de.oscvev.virtualchoir.splitscreenvideo.SplitScreenClip;
import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author Andreas
 */
public class CorrelationCoefficientCalculator extends Thread {

    private final DefaultSplitScreenVideo splitScreenVideo;

    public CorrelationCoefficientCalculator(DefaultSplitScreenVideo splitScreenVideo) {
        super();
        this.splitScreenVideo = splitScreenVideo;
    }

    @Override
    public void run() {
        final ProgressHandle progr = ProgressHandle.createHandle("Calculation Correlation Coefficients");
        VideoUtilities vUtils = new VideoUtilities(splitScreenVideo.getWorkingDirectory());
        progr.start(splitScreenVideo.getClips().size());
        int numberOfProcessedClips = 0;
        for (SplitScreenClip ssClip : splitScreenVideo.getClips()) {
            double offset = vUtils.correlateWaves(ssClip.getMasterfile(), ssClip.getAudioFile(), 0.0, 141.0, VideoUtilities.PRAAT_RETURN_VALUEABSMAX);
            ssClip.setCorrelationCoefficient(offset);
            progr.progress(numberOfProcessedClips++);
        }
        progr.finish();
    }

}
