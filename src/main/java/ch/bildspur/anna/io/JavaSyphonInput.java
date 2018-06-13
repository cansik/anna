package ch.bildspur.anna.io;

import codeanticode.syphon.SyphonClient;
import org.jetbrains.annotations.NotNull;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class JavaSyphonInput extends IOConnection {
    private SyphonClient client;
    private PImage frame;
    private PImage blackFrame;


    public JavaSyphonInput(@NotNull PApplet parent) {
        super(parent);
    }

    @Override
    public void setup() {
        blackFrame = getParent().createImage(0, 0, PConstants.RGB);
    }

    @Override
    public void open() {
        client = new SyphonClient(getParent());
    }

    @Override
    public void update() {
        if(client.newFrame())
        {
            frame = client.getImage(frame);
        }
    }

    @Override
    public void close() {
        client.stop();
    }

    public PImage getFrame() {
        return frame != null ? frame : blackFrame;
    }
}
