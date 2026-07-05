package ThreeDGame;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static Map<String, Clip> clips = new HashMap<>();

    public static void load(String filename){
        try{
            File soundFile = new File("ThreeDGame/"+ filename);
            AudioInputStream baseStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat baseFormat = baseStream.getFormat();
            AudioFormat targeFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false
            );
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(targeFormat, baseStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clips.put(filename, clip);
        }catch(Exception e){
            System.out.println("Error loading sound: "+ filename);
            e.printStackTrace();
        }
    }
    public static void playSound(String filename) {
        Clip clip = clips.get(filename);
        if (clip != null) {
            // 1. Instantly stop and rewind on the calling thread to kill any old sound
            clip.stop();
            clip.setFramePosition(0);
            // 2. Only use the background thread to handle the start trigger
            new Thread(() -> {
                clip.start();
            }).start();
        }
    }
}
