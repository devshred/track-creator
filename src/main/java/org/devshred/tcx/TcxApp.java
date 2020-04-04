package org.devshred.tcx;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import io.jenetics.jpx.GPX;
import org.apache.commons.lang3.StringUtils;
import org.devshred.Config;

import java.io.File;
import java.io.IOException;

import static org.devshred.tcx.Converter.createTcxFromGpx;

public class TcxApp {
    static final XmlMapper XML_MAPPER = new XmlMapper.Builder(new XmlMapper())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    static {
        XML_MAPPER.getFactory().enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
    }

    public static void writeTcx(GPX gpx, String trackName) throws IOException {
        final TrainingCenterDatabase trainingCenterDatabase = createTcxFromGpx(gpx);
        writeTcxToFile(trackName, trainingCenterDatabase);
    }

    private static void writeTcxToFile(String trackName, TrainingCenterDatabase trainingCenterDatabase) throws IOException {
        final String tcxDir = Config.INSTANCE.getProp("outputDir") + "/tcx/";
        if (!new File(tcxDir).exists()) {
            new File(tcxDir).mkdir();
        }
        final String filenameTcx = StringUtils.replace(trackName, " ", "_") + ".tcx";
        XML_MAPPER.writeValue(new File(tcxDir + filenameTcx), trainingCenterDatabase);
        System.out.println("wrote file " + filenameTcx);
    }
}
