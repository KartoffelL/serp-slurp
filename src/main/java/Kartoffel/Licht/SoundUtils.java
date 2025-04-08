package Kartoffel.Licht;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class SoundUtils {
	
	
	private static Clip sequencerMainMusic;
	private static Sequencer sequencerMenuMusic;
	private static Synthesizer synthMusic;
	static boolean fr = true;
	private static byte[] sequencerMenuMusicis;
	private static byte[] sequencerMainMusicis;
	public static byte[] buttonFocus;
	public static byte[] deadth;
	public static byte[] charge;
	public static byte[] hit;
	public static byte[][] belly;
	public static byte[] eat;
	public static byte[][] diffs;
	
	public static byte[] tiktak;
	public static byte[] balon;
	public static byte[] plakfort;
	
	private static AudioInputStream sequencerMainMusicisStream;
	
	public static void init() throws IOException, MidiUnavailableException, InvalidMidiDataException, LineUnavailableException, UnsupportedAudioFileException {
		Soundbank s = MidiSystem.getSoundbank(Main.loadResourceIS("sound/font/GeneralUser GS v1.472.sf2"));
		{
	        HashMap<String, String> mappings = new HashMap<String, String>();
	        HashMap<String, Instrument> mappingsI = new HashMap<String, Instrument>();
	        
	        mappings.put("Trombone", "Heart Beat");
	        mappings.put("French Horns", "Scream");
	        mappings.put("Double Bass", "Pizzicato Strings");
	        mappings.put("Tuba", "Music Box");
			synthMusic = MidiSystem.getSynthesizer();
			synthMusic.open();
			synthMusic.loadAllInstruments(s);
			for(Instrument i : synthMusic.getLoadedInstruments()) {
				mappings.forEach((a, b)->{
					if(a.equals(i.getName()))
						mappingsI.put(a, i);
					else if(b.equals(i.getName()))
						mappingsI.put(b, i);
				});
			}
			mappings.forEach((a, b)->{
				var ia = mappingsI.get(a);
				var ib = mappingsI.get(b);
				if(ia == null || ib == null)
					System.err.println("Failed to remap '"+a+"' to '"+b+"' -> '"+ia+"' to '"+ib+"'");
				else
					synthMusic.remapInstrument(ia, ib);
			});
		}
        {
        	sequencerMenuMusicis = Main.loadResourceIS("sound/track0.mid").readAllBytes();
	        
        	sequencerMenuMusic = MidiSystem.getSequencer(false);
        	sequencerMenuMusic.open();
        	sequencerMenuMusic.getTransmitter().setReceiver(synthMusic.getReceiver());
        }
        {
            sequencerMainMusicis = Main.loadResourceIS("sound/track1.wav").readAllBytes();
	        
	//        //Init Music
//			sequencerMainMusic = MidiSystem.getSequencer(false);
//	        sequencerMainMusic.open();
//	        sequencerMainMusic.getTransmitter().setReceiver(synthMusic.getReceiver());
//	        sequencerMainMusic.setSequence(is);
//	        sequencerMainMusic.setLoopCount(500);
		}
        buttonFocus = Main.loadResourceIS("sound/button.wav").readAllBytes();
        deadth = Main.loadResourceIS("sound/death.wav").readAllBytes();
        charge = Main.loadResourceIS("sound/charge.wav").readAllBytes();
        hit = Main.loadResourceIS("sound/hurt.wav").readAllBytes();
        eat = Main.loadResourceIS("sound/wasted_finance.wav").readAllBytes();
        belly = new byte[3][];
        belly[0] = Main.loadResourceIS("sound/belly1.wav").readAllBytes();
        belly[1] = Main.loadResourceIS("sound/belly2.wav").readAllBytes();
        belly[2] = Main.loadResourceIS("sound/belly3.wav").readAllBytes();
        diffs = new byte[3][];
        diffs[0] = Main.loadResourceIS("sound/diff/easy.wav").readAllBytes();
        diffs[1] = Main.loadResourceIS("sound/diff/medium.wav").readAllBytes();
        diffs[2] = Main.loadResourceIS("sound/diff/hard.wav").readAllBytes();
        tiktak = Main.loadResourceIS("sound/tiktak.wav").readAllBytes();
        balon = Main.loadResourceIS("sound/balon.wav").readAllBytes();
        plakfort = Main.loadResourceIS("sound/plakfort.wav").readAllBytes();
        
        fr = false;
	}
	
	public static void startMainMenuMusic() throws IOException, InvalidMidiDataException {
		if(fr)
			return;
    	sequencerMenuMusic.setSequence(new ByteArrayInputStream(sequencerMenuMusicis));
    	sequencerMenuMusic.setLoopCount(500);
		sequencerMenuMusic.start();
	}
	public static void startGameMusic(Runnable r) {
		if(fr)
		return;
		sequencerMenuMusic.setLoopCount(0);
		sequencerMenuMusic.start();
		while(sequencerMenuMusic.isRunning() && !fr) r.run();
		if(fr)
			return;
		try {
			sequencerMenuMusic.getTransmitter().setReceiver(null);
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			sequencerMainMusicisStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(sequencerMainMusicis));
			var format = sequencerMainMusicisStream.getFormat();
			var info = new DataLine.Info(Clip.class, format);
			sequencerMainMusic = (Clip) AudioSystem.getLine(info);
			sequencerMainMusic.open(sequencerMainMusicisStream);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		sequencerMainMusic.start();
	}
	
	public static void playButtonSound() {
		playShortSound(buttonFocus, -25);
	}
	
	public static void playAmbientBelly() {
		playShortSound(belly[Main.RANDOM.nextInt(3)], -10);
	}
	
	public static void playTiktok() {
		playShortSound(tiktak, 0);
	}
	
	public static void playBallon() {
		playShortSound(balon, 0);
	}
	
	public static void playPlattform() {
		playShortSound(plakfort,0);
	}
	
	
	public static void playDiffSound(int s) {
		playShortSound(diffs[s], -2);
	}
	
	public static void playEatSound() {
		playShortSound(eat, -10);
	}
	public static void playHitSound() {
		playShortSound(hit, -10);
	}
	
	
	public static void playShortSound(byte[] b, float volume){
		try {
			AudioInputStream a = AudioSystem.getAudioInputStream(new ByteArrayInputStream(b));
			var format = a.getFormat();
			var info = new DataLine.Info(Clip.class, format);
			var bc = (Clip) AudioSystem.getLine(info);
			bc.open(a);
			FloatControl volumeControl = (FloatControl) bc.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl.setValue(volume);
			bc.start();
			bc.addLineListener(new LineListener() {
				
				@Override
				public void update(LineEvent event) {
					if(event.getType() == Type.STOP) {
						bc.close();
						try {
							a.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public static void playPlayerDamage() {
		playShortSound(hit, 1);
	}
	
	public static void playGameOver() {
		sequencerMainMusic.stop();
		playShortSound(deadth, 1);
	}
	
	public static void reset() {
		if(fr)
		return;
		sequencerMainMusic.stop();
		sequencerMenuMusic.stop();
	}
	
	public static void free() {
		if(fr)
		return;
		if(sequencerMainMusic != null) {
			sequencerMainMusic.stop();
			sequencerMainMusic.close();
		}
		if(sequencerMenuMusic != null) {
			sequencerMenuMusic.stop();
			sequencerMenuMusic.close();
		}
		synthMusic.close();
		fr = true;
	}

	
}
