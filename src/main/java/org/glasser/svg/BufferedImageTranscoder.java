package org.glasser.svg;

import java.awt.image.BufferedImage;
import org.apache.batik.transcoder.image.*;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;


public class BufferedImageTranscoder extends ImageTranscoder {

    protected BufferedImage image = null;  

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void writeImage(BufferedImage image, TranscoderOutput transcoderOutput) throws TranscoderException {
        this.image = image;
    }

    public BufferedImage getBufferedImage() {
        return image;
    }
}
