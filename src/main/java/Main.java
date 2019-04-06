import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

    private static final int DEFAULT_INSTRUMENT = 1;

    private MidiChannel channel;

    public Main() throws MidiUnavailableException {
        this(DEFAULT_INSTRUMENT);
    }

    public Main(int instrument) throws MidiUnavailableException {
        channel = getChannel(instrument);
    }

    public void setInstrument(int instrument) {
        channel.programChange(instrument);
    }

    public int getInstrument() {
        return channel.getProgram();
    }

    public void play(final int note, final int velocity) {
        channel.noteOn(note, velocity);
    }

    public void release(final int note, final int velocity) {
        channel.noteOff(note, velocity);
        channel.noteOff(note);
    }

    public void play(final int note, final long length, final int velocity) throws InterruptedException {
        play(note, velocity);
        Thread.sleep(length);
        release(note, velocity);
    }

    public void stop() {
        channel.allNotesOff();
    }

    private static MidiChannel getChannel(int instrument) throws MidiUnavailableException {
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        return synthesizer.getChannels()[instrument];
    }

    public static void main(String[] args) throws Exception {

        BufferedReader in =
                new BufferedReader(new InputStreamReader(System.in));


        System.out.println("blop");
        byte[] bytes;
        var ubytes = new int[3];
        Main player = new Main();
        boolean flag = true;
        int instrument = 0x01;
        boolean instrumentFlag = true;
        int status_byte_instrument, data_byte_1_instrument, status_byte, data_byte_1, data_byte_2;
        player.setInstrument(0x01);
        System.out.println("status " + 0xC2 + " " + 0xC2);
        while (!Thread.currentThread().isInterrupted()) {

            bytes = new byte[3];
            String line = in.readLine();
            line=line.replace(" ","");
            String[] values=line.split(";");

            System.out.println(line);
            ubytes[2]=255-(Integer.valueOf(values[3])-50);
            ubytes[1]=150-(Integer.valueOf(values[1])/5);
            status_byte_instrument =ubytes[0];//channel

//            while(flag){
            data_byte_1 = ubytes[1];//note
            data_byte_2 = ubytes[2];//velocity

            if(instrumentFlag) {
                if (Integer.valueOf(values[0]) < 250) {
                    if(Integer.valueOf(values[1])>150){
                        System.out.println("instrument up");
                        instrument++;
                    } else{
                        System.out.println("instrument down");
                        instrument--;
                    }
                    player.setInstrument(instrument);
                    instrumentFlag = false;
                }
            }else {
                if(Integer.valueOf(values[0])>250){
                    instrumentFlag = true;
                }
            }

            if(flag) {
                if (Integer.valueOf(values[2]) > 100) {
                    flag = false;
                    ubytes[0] = 0x92;

                    status_byte = ubytes[0];//key on | key off
                    player.play(data_byte_1, data_byte_2);
                    System.out.println("note"+data_byte_1+" up, volume: "+ data_byte_2);
                }
            } else {
                if (Integer.valueOf(values[2]) < 100) {
                    flag=true;
                    ubytes[0] = 0x82;
                    status_byte = ubytes[0];//key on | key off
                    player.release(data_byte_1, data_byte_2);
                    System.out.println("note"+data_byte_1+" down, volume: "+ data_byte_2);
                }
            }

            //Thread.sleep(500);
        }//*/
    }
}
