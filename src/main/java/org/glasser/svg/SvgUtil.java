package org.glasser.svg;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;


public class SvgUtil {


    private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SvgUtil.class);

    public static BufferedImage svgToBufferedImage(URL url) 
        throws IOException, TranscoderException, URISyntaxException {

        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        TranscoderInput input = new TranscoderInput(url.toURI().toString());
        transcoder.transcode(input, null);
        return transcoder.getBufferedImage();
    }
}
