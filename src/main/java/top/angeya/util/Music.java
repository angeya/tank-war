package top.angeya.util;

import top.angeya.constant.MusicPaths;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * 音乐工具类
 * @author: Angeya
 * @date: 2023/1/8 11:11
 **/
public class Music{
    private Music() {}

    /**
     * 背景音乐
     */
    private static Clip startMusic;

    /**
     * 爆炸音效
     */
    private static Clip explosionMusic;

    /**
     * 射击音效
     */
    private static Clip shotMusic;

    static {
        File startMusicFile = new File(MusicPaths.START);
        File shotMusicFile = new File(MusicPaths.SHOT);
        File explosionMusicFile = new File(MusicPaths.EXPLOSION);
        try {
            AudioInputStream startMusicStream = AudioSystem.getAudioInputStream(startMusicFile);
            startMusic = AudioSystem.getClip();
            startMusic.open(startMusicStream);

            AudioInputStream explosionMusicStream = AudioSystem.getAudioInputStream(explosionMusicFile);
            explosionMusic = AudioSystem.getClip();
            explosionMusic.open(explosionMusicStream);

            AudioInputStream shotMusicStream = AudioSystem.getAudioInputStream(shotMusicFile);
            shotMusic = AudioSystem.getClip();
            shotMusic.open(shotMusicStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放开场音乐
     */
    public static void playStartMusic(){
        startMusic.start();
        startMusic.setFramePosition(0);
    }

    /**
     * 播放爆炸音乐
     */
    public static void playExplosionMusic(){
        explosionMusic.start();
        explosionMusic.setFramePosition(0);
    }

    /**
     * 播放射击音乐
     */
    public static void playShotMusic(){
        shotMusic.start();
        shotMusic.setFramePosition(0);
    }
}