/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #1
 * 1 - 5026221016 - Arjuna Ahmad Dewangga Aljabbar
 * 2 - 5026221126 -  Nur Ghulam Musthafa Al Kautsar
 * 3 - 5026221147 -  Ahmad Hilmi Dwi Setiawan
 */

package sudoku;

import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    private static Clip backgroundMusic;
    private static boolean soundEnabled = true;

    public void toggleSound() {
        soundEnabled = !soundEnabled;

        if (backgroundMusic != null) {
            if (soundEnabled) {
                backgroundMusic.start();
            } else {
                backgroundMusic.stop();
                backgroundMusic.setFramePosition(0); // Reset the clip
            }
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void playBackgroundMusic(String filePath) {
        try {
            // Avoid creating multiple instances of the Clip
            if (backgroundMusic == null) {
                File musicFile = new File(filePath);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }

            if (soundEnabled) {
                backgroundMusic.start();
            }
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.setFramePosition(0); // Reset the clip
        }
    }
}
