/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.oscvev.virtualchoid.videoutilities;

import de.oscvev.virtualchoir.ffmpeg.FFMPEGUtilities;
import de.oscvev.virtualchoir.praat.PraatUtilities;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.io.IOProvider;
import org.netbeans.api.io.InputOutput;
import org.openide.util.Exceptions;

/**
 *
 * @author Andreas
 */
public class VideoUtilities {

   // private static final String tempPath = "H:\\Temp";
    //private static final String tempPath = "/tmp/Temp_ahauffe";
    private String praatScriptPath;
    private String praatResultFilePath;
    private String ffmpegInputFilePath;
    
    private String ffmpegPath;
    private String praatPath;
    private String tempPath;
    
    private boolean nativeOutput = true;

    public VideoUtilities(String workingDirectory) {
        setWorkingDirectory(workingDirectory);
        try {
            Path execPath = FFMPEGUtilities.getFFMPEGExecutable(tempPath);
            ffmpegPath = execPath.toString();
            execPath = PraatUtilities.getPraatExecutable(tempPath);
            praatPath = execPath.toString();
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void setWorkingDirectory(String workingDirectory){
        tempPath = workingDirectory;
        praatScriptPath = tempPath + File.separator + "crosscorrelate_pitch.praat";
        praatResultFilePath = tempPath + File.separator + "offset.txt";
        ffmpegInputFilePath = tempPath + File.separator + "input.txt";
    }
    
    private boolean checkPrerequisites(){
        boolean ffmpegExec = ffmpegPath != null;
        boolean praatExec = praatPath != null;
        
        return ffmpegExec && praatExec;
    }

    public Path extactOneAudioChannel(Path videoPath) {
        if (!checkPrerequisites() || videoPath == null) {
            return null;
        }
        InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
        io.getOut().println("Extract Audio of " + videoPath.toString() + "...");
        ProcessBuilder builder = new ProcessBuilder();
        Path wavFilePath = Paths.get(tempPath + File.separator + videoPath.getFileName().toString() + ".wav");
        builder.command(ffmpegPath,
                "-y",
                "-i", videoPath.toString(),
                "-ss", "0.0",
                "-acodec", "pcm_s16le", "-ac", "1", "-ar", "44100",
                wavFilePath.toString());
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
            if (exitCode != 0) {
                wavFilePath = null;
            }
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            wavFilePath = null;
        }
        io.getOut().close();
        return wavFilePath;
    }
    
    public static final int PRAAT_RETURN_OFFSETMAX = 0;
    public static final int PRAAT_RETURN_OFFSETMIN = 1;
    public static final int PRAAT_RETURN_VALUEMAX = 2;
    public static final int PRAAT_RETURN_VALUEMIN = 3;
    public static final int PRAAT_RETURN_VALUEABSMAX = 4;

    public double correlateWaves(Path wavFile1, Path wavFile2, double startTime, double duration, int returnValue) {
        if (!checkPrerequisites() || wavFile1 == null || wavFile2 == null) {
            return Double.NaN;
        }
        double offset = 0.0;

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(praatScriptPath))) {
            writer.write("form Cross Correlate two Sounds"); writer.newLine();
            writer.write("  sentence Input_sound_1"); writer.newLine();
            writer.write("  sentence Input_sound_2"); writer.newLine();
            writer.write("  real start_time 0.0"); writer.newLine();
            writer.write("  real end_time 60.0"); writer.newLine();
            writer.write("endform"); writer.newLine();
            writer.write(""); writer.newLine();
            writer.write("Open long sound file... 'input_sound_1$'"); writer.newLine();
            writer.write("Extract part: start_time, end_time, \"no\""); writer.newLine();
            writer.write("Extract one channel... 1"); writer.newLine();
            writer.write("To Pitch: 0.0, 75.0, 800.0"); writer.newLine();
            writer.write("To Sound (hum)"); writer.newLine();
            writer.write("sound1 = selected(\"Sound\")"); writer.newLine();
            writer.write("Open long sound file... 'input_sound_2$'"); writer.newLine();
            writer.write("Extract part: start_time, end_time, \"no\""); writer.newLine();
            writer.write("Extract one channel... 1"); writer.newLine();
            writer.write("To Pitch: 0.0, 75.0, 800.0"); writer.newLine();
            writer.write("To Sound (hum)"); writer.newLine();
            writer.write("sound2 = selected(\"Sound\")"); writer.newLine();
            writer.write(""); writer.newLine();
            writer.write("select sound1"); writer.newLine();
            writer.write("plus sound2"); writer.newLine();
            writer.write("Cross-correlate: \"sum\", \"zero\""); writer.newLine();
            writer.write("valueMax = Get maximum: 0, 0, \"Sinc70\""); writer.newLine();
            writer.write("valueMin = Get minimum: 0, 0, \"Sinc70\""); writer.newLine();
            writer.write("offsetMax = Get time of maximum: 0, 0, \"Sinc70\""); writer.newLine();
            writer.write("offsetMin = Get time of minimum: 0, 0, \"Sinc70\""); writer.newLine();
            writer.write(""); writer.newLine();            
            writer.write("writeFileLine: \"" + praatResultFilePath.toString() + "\", 'valueMax'"); writer.newLine();
            writer.write("appendFileLine: \"" + praatResultFilePath.toString() + "\", 'valueMin'"); writer.newLine();
            writer.write("appendFileLine: \"" + praatResultFilePath.toString() + "\", 'offsetMax'"); writer.newLine();
            writer.write("appendFileLine: \"" + praatResultFilePath.toString() + "\", 'offsetMin'"); writer.newLine();
            writer.close();
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
        io.getOut().println("Determine offset of " + wavFile2.toString() + "...");
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(tempPath));
        builder.command("cmd.exe",
                "/c",
                praatPath + " "
                + praatScriptPath + " "
                + "\"" + wavFile1.toString() + "\" "
                + "\"" + wavFile2.toString() + "\" "
                + Double.toString(startTime) + " "
                + Double.toString(startTime + duration));
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }

        double valueMax = Double.NaN;
        double valueMin = Double.NaN;
        double offsetMax = Double.NaN;
        double offsetMin = Double.NaN;
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(praatResultFilePath))) {
            valueMax = Double.parseDouble(reader.readLine().trim());
            valueMin = Double.parseDouble(reader.readLine().trim());
            offsetMax = Double.parseDouble(reader.readLine().trim());
            offsetMin = Double.parseDouble(reader.readLine().trim());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        
        switch(returnValue){
            case PRAAT_RETURN_OFFSETMAX:
                offset = offsetMax; break;
            case PRAAT_RETURN_OFFSETMIN:
                offset = offsetMin; break;
            case PRAAT_RETURN_VALUEMAX:
                offset = valueMax; break;
            case PRAAT_RETURN_VALUEMIN:
                offset = valueMin; break;
            case PRAAT_RETURN_VALUEABSMAX:
                offset = Math.max(Math.abs(valueMax), Math.abs(valueMin)); break;
            default:
                offset = offsetMax; break;
        }
        
        io.getOut().close();

        return offset;
    }
    
    public double getVideoLength(Path videoPath) {
        if (!checkPrerequisites() || videoPath == null) {
            return Double.NaN;
        }
        double length = Double.NaN;
        InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
        io.getOut().println("Determine clip length of " + videoPath.toString() + "...");
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(ffmpegPath,
                "-i", videoPath.toString());
        builder.redirectErrorStream(true);
        
        try {
            Process process = builder.start();
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
                if (line.contains("Duration:")){
                    String time = line.substring(line.indexOf(": ")+1, line.indexOf(","));
                    String[] parts = time.split(":");
                    int hours = Integer.parseInt(parts[0].trim());
                    int minutes = Integer.parseInt(parts[1].trim());
                    double seconds = Double.parseDouble(parts[2].trim());
                    seconds += minutes * 60;
                    seconds += hours * 3600;
                    length = seconds;
                }
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        io.getOut().close();
        return length;
    }
    
    public Path removeOffsetFromWave(Path audioPath, double offset){
        if (!checkPrerequisites()){
            return null;
        }
        Path resultFilePath = Paths.get(tempPath + File.separator + audioPath.getFileName().toString() + "_cut.wav");
        if (offset < 0.0){
            if (audioPath == null) {
                return null;
            }
            
            // Stille erstellen
            InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
            io.getOut().println("Remove offset from " + audioPath.toString() + "...");
            ProcessBuilder builder = new ProcessBuilder();
            Path silenceFilePath = Paths.get(tempPath + File.separator + "silence.wav");
            builder.command(ffmpegPath,
                    "-y",
                    "-t", Double.toString(-offset),
                    "-f", "lavfi",
                    "-i", "anullsrc", 
                    "-acodec", "pcm_s16le", 
                    "-ac", "1",
                    "-ar", "44100",
                    silenceFilePath.toString());
            builder.redirectErrorStream(true);
            try {
                Process process = builder.start();
                BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = readerOutput.readLine()) != null) {
                    if (nativeOutput) io.getOut().println("    " + line);
                }

                int exitCode = process.waitFor();
                if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
                if (exitCode != 0) {
                    silenceFilePath = null;
                }
            } catch (IOException | InterruptedException ex) {
                Exceptions.printStackTrace(ex);
                silenceFilePath = null;
            }
            io.getOut().close();
            
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(ffmpegInputFilePath))) {
                writer.write("file '" + silenceFilePath.toString() + "'"); writer.newLine();
                writer.write("file '" + audioPath.toString() + "'");
                writer.close();
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
            }
            
            // Audiodateien verbinden
            io = IOProvider.getDefault().getIO("Videoprocessing", false);
            io.getOut().println("Concatenate audio file with silence " + audioPath.toString() + "...");
            builder = new ProcessBuilder();
            builder.command(ffmpegPath,
                    "-y",
                    "-f", "concat",
                    "-safe", "0",
                    "-i", ffmpegInputFilePath.toString(),
                    "-codec", "copy", 
                    resultFilePath.toString());
            builder.redirectErrorStream(true);
            try {
                Process process = builder.start();
                BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = readerOutput.readLine()) != null) {
                    if (nativeOutput) io.getOut().println("    " + line);
                }

                int exitCode = process.waitFor();
                if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
                if (exitCode != 0) {
                    resultFilePath = null;
                }
            } catch (IOException | InterruptedException ex) {
                Exceptions.printStackTrace(ex);
                resultFilePath = null;
            }
            io.getOut().close();
            try {
                Files.deleteIfExists(Paths.get(ffmpegInputFilePath));
                Files.deleteIfExists(silenceFilePath);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }else{
            
            // Audiodateien verbinden
            InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
            io.getOut().println("Trim audio file " + audioPath.toString() + "...");
            ProcessBuilder builder = new ProcessBuilder();
            
            builder.command(ffmpegPath,
                    "-y",
                    "-ss", Double.toString(offset),
                    "-i", audioPath.toString(),
                    "-c", "copy", 
                    resultFilePath.toString());
            builder.redirectErrorStream(true);
            try {
                Process process = builder.start();
                BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = readerOutput.readLine()) != null) {
                    if (nativeOutput) io.getOut().println("   " + line);
                }

                int exitCode = process.waitFor();
                if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
                if (exitCode != 0) {
                    resultFilePath = null;
                }
            } catch (IOException | InterruptedException ex) {
                Exceptions.printStackTrace(ex);
                resultFilePath = null;
            }
            io.getOut().close();
        }
        return resultFilePath;
    }
    
    public Path trimAudio(Path audioFile, double startTime, double duration){
        if (!checkPrerequisites()){
            return null;
        }
        InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
        io.getOut().println("Trim " + audioFile.toString() + " to " + startTime + " - " + duration);
        ProcessBuilder builder = new ProcessBuilder();

        Path resultFilePath = Paths.get(tempPath + File.separator + audioFile.getFileName().toString() + "_trim.wav");
        //-ss {0} -i {1} -t {2} -c copy {3}
        builder.command(ffmpegPath,
                "-y",
                "-ss", Double.toString(startTime),
                "-i", audioFile.toString(),
                "-t", Double.toString(duration),
                "-c", "copy",
                resultFilePath.toString());
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
            if (exitCode != 0) {
                resultFilePath = null;
            }
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            resultFilePath = null;
        }
        io.getOut().close();
        return resultFilePath;
    }
    
    public Path trimVideo(Path videoFile, double startTime, double duration, int width, int height){
        if (!checkPrerequisites()){
            return null;
        }
        InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
        io.getOut().println("Trim video " + videoFile.toString() + "...");
        ProcessBuilder builder = new ProcessBuilder();

        Path resultFilePath = Paths.get(tempPath + File.separator + videoFile.getFileName().toString() + "_trim.mp4");
        builder.command(ffmpegPath,
                "-y",
                "-ss", Double.toString(startTime),
                "-i", videoFile.toString(),
                "-t", Double.toString(duration),
                "-preset", "slow",
                "-an",
                //"-codec:v", "libx264",
                "-codec:v", "h264_nvenc",
                "-pix_fmt", "yuv420p",
                "-r", "30.00",
                "-vf", "scale=" + Integer.toString(width) + ":" + Integer.toString(height) + ":force_original_aspect_ratio=increase,crop=" + Integer.toString(width) + ":" + Integer.toString(height),
                resultFilePath.toString());
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;            
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
            if (exitCode != 0) {
                resultFilePath = null;
            }
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            resultFilePath = null;
        }
        io.getOut().close();
        return resultFilePath;
    }
    
    public void normalizeAudio(Path audioPath){
        if (!checkPrerequisites()){
            return;
        }
        double dBValue = 0.0;
        InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
        io.getOut().println("Normalize " + audioPath.toString() + "...");
        ProcessBuilder builder = new ProcessBuilder();

        builder.command(ffmpegPath,
                "-y",
                "-i", audioPath.toString(),
                "-af", "volumedetect",
                "-vn", "-sn",
                "-dn", "-f",
                "null", "NUL");
        try {
            Process process = builder.start();
            BufferedReader readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
            }

            while ((line = readerError.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
                if (line.contains("max_volume")){
                    String dBValueStr = line.substring(line.indexOf(": ")+1, line.indexOf("dB"));
                    dBValue = Double.parseDouble(dBValueStr);
                }
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n     Exited with error code : " + exitCode);
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        io.getOut().close();
        

        io = IOProvider.getDefault().getIO("Videoprocessing", false);
        builder = new ProcessBuilder();

        Path resultFilePath = Paths.get(tempPath + File.separator + audioPath.getFileName().toString() + "_tmp.wav");
        io.getOut().println("Normalize from " + dBValue + " to " + Double.toString(-2.0-dBValue) + "...");
        // -i {0} -af \"volume={1}dB\" -ac 1 {2}
        builder.command(ffmpegPath,
                "-y",
                "-i", audioPath.toString(),
                "-af", "volume=" + Double.toString(-2.0-dBValue) + "dB",
                "-ac", "1",
                resultFilePath.toString());
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
            if (exitCode != 0) {
                resultFilePath = null;
            }
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            resultFilePath = null;
        }
        io.getOut().close();
        if (resultFilePath != null){
            try {
                Files.deleteIfExists(audioPath);
                Files.move(resultFilePath, audioPath);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }        
    }
    
    public Path mix_audios(ArrayList<Path> audioFiles){
        if (!checkPrerequisites()){
            return null;
        }
        InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
        io.getOut().println("Mix all audiofiles ...");
        ProcessBuilder builder = new ProcessBuilder();

        Path resultFilePath = Paths.get(tempPath + File.separator + "splitvideo.wav");
        
        List<String> commandList = new ArrayList<>();
        commandList.add(ffmpegPath);
        commandList.add("-y");
        for (Path file : audioFiles){
            commandList.add("-i");
            commandList.add(file.toString());
        }
        commandList.add("-filter_complex");
        commandList.add("amix=inputs=" + audioFiles.size() + ":duration=longest");
        commandList.add(resultFilePath.toString());
        
        builder.command(commandList);
        try {
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n     Exited with error code : " + exitCode);
            if (exitCode != 0) {
                resultFilePath = null;
            }
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            resultFilePath = null;
        }
        io.getOut().close();
        return resultFilePath;
    }
    
    public Path createSplitScreen(List<Path> clips, int clipHeight, int numClipsPerSide){
        if (!checkPrerequisites()){
            return null;
        }
        Path resultFilePath = Paths.get(tempPath + File.separator + "splitscreenvideo.mp4");
        
        String layoutString = "";
        String tmpStrJJ = "";
       
        for (int jj = 0; jj < numClipsPerSide; jj++){
            String tmpStrII = "";
            int numJJ = jj - 1;
            if (numJJ > -1){
                tmpStrJJ += "+h" + numJJ;
            }else{
                tmpStrJJ += "0";
            }
            for (int ii = 0; ii < numClipsPerSide; ii++){
                int numII = ii - 1;
                if (numII > -1){
                    tmpStrII += "+w" + numII;
                }else{
                    tmpStrII += "0";
                }
                layoutString += tmpStrII + "_" + tmpStrJJ + "|";
            }
        }
        
        String part1 = "";
        String part2 = "";
        String part3 = "xstack=inputs=" + clips.size() + ":layout=" + layoutString + "[v]";
        ArrayList<String> arguments = new ArrayList<>();
        arguments.add(ffmpegPath);
        arguments.add("-y");
        
        int counter = 0;
        for (Path clipPath : clips){
            arguments.add("-i");
            arguments.add(clipPath.toString());
            part1 += "[" + counter + "]scale=-1:" + clipHeight + "[v" + counter + "];";
            part2 += "[v" + counter + "]";
            counter++;
        }
        arguments.add("-filter_complex");
        arguments.add(part1 + part2 + part3);
        arguments.add("-map");
        arguments.add("[v]");
        arguments.add(resultFilePath.toString());
        
        InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
        io.getOut().println("Create Split Screen Video ...");
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(arguments);
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
            if (exitCode != 0) {
                resultFilePath = null;
            }
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            resultFilePath = null;
        }
        io.getOut().close();
        return resultFilePath;
    }
    
    public Path mergeAudioVideo(Path videoFile, Path audioFile){
        if (!checkPrerequisites()){
            return null;
        }
        
        Path resultFilePath = Paths.get(tempPath + File.separator + "splitscreenvideoaudio.mp4");
        InputOutput io = IOProvider.getDefault().getIO("Videoprocessing", false);
        io.getOut().println("Merge Audio and Video of Split Screen Video ...");
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(ffmpegPath,
                "-y",
                "-i", videoFile.toString(),
                "-i", audioFile.toString(),
                "-c:v", "copy",
                "-c:a", "aac",
                resultFilePath.toString());
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            BufferedReader readerOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = readerOutput.readLine()) != null) {
                if (nativeOutput) io.getOut().println("    " + line);
            }

            int exitCode = process.waitFor();
            if (nativeOutput) io.getOut().println("\n    Exited with error code : " + exitCode);
            if (exitCode != 0) {
                resultFilePath = null;
            }
        } catch (IOException | InterruptedException ex) {
            Exceptions.printStackTrace(ex);
            resultFilePath = null;
        }
        io.getOut().close();
        return resultFilePath;
    }
}
