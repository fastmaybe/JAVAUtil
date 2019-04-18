package com.secmask.util.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.SVGAbstractTranscoder;


/**
 * @author wd
 * @Program common-web
 * @create 2019-03-04 11:30
 */
public class ExportUtil {

    /**
     * svg转流
     * @param stream
     * @param svg
     * @param mime
     * @param width
     * @return
     * @throws TranscoderException
     * @throws UnsupportedEncodingException
     */
    public static synchronized ByteArrayOutputStream transcode(ByteArrayOutputStream stream, String svg, Integer mime, Float width) throws TranscoderException, UnsupportedEncodingException {
        //TranscoderInput input = new TranscoderInput(new StringReader(svg));
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(svg.getBytes("utf-8")));
        TranscoderOutput transOutput = new TranscoderOutput(stream);

        SVGAbstractTranscoder transcoder = getTranscoder(mime);
        if (width != null) {
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width);
        }
        transcoder.transcode(input, transOutput);
        return stream;
    }


    public static SVGAbstractTranscoder getTranscoder(Integer mime)  {
        SVGAbstractTranscoder transcoder = null;
        switch (mime) {
            case 0:
                transcoder = new PNGTranscoder();
                break;
            case 1:
                transcoder = new JPEGTranscoder();
                transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1.0D));
//                transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, new Float(800));
                break;
            default:
                break;
        }
        if (transcoder == null) {
        }
        return transcoder;
    }

    public static ByteArrayOutputStream convertToPng(String svgCode,ByteArrayOutputStream outputStream) throws TranscoderException,IOException {
        try {
            byte[] bytes = svgCode.getBytes ("UTF-8");
            PNGTranscoder t = new PNGTranscoder ();
            TranscoderInput input = new TranscoderInput (new ByteArrayInputStream (bytes));
            TranscoderOutput output = new TranscoderOutput (outputStream);
            t.transcode (input, output);
            outputStream.flush ();
            return outputStream;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
    }


}
