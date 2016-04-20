package edu.umn.biomedicus.uima.files;

import edu.umn.biomedicus.type.ClinicalNoteAnnotation;
import edu.umn.biomedicus.type.IllegalXmlCharacter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.ProcessingResourceMetaData;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.util.Date;

/**
 *
 */
public class RtfTextFileAdapter implements InputFileAdapter {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Date formatter for adding date to metadata.
     */
    private final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.LONG);

    /**
     * View to load data into.
     */
    @Nullable
    private String viewName;

    @Nullable
    private String version;

    @Override
    public void initialize(UimaContext uimaContext, ProcessingResourceMetaData processingResourceMetaData) {
        LOGGER.info("Initializing xml validating file adapter.");
        version = processingResourceMetaData.getVersion();
    }

    @Override
    public void adaptFile(CAS cas, Path path) throws CollectionException, IOException {
        if (cas == null) {
            LOGGER.error("Null CAS");
            throw new IllegalArgumentException("CAS was null");
        }

        LOGGER.info("Reading text from: {} into a CAS view: {}", path, viewName);
        JCas defaultView;
        try {
            defaultView = cas.getJCas();
        } catch (CASException e) {
            throw new CollectionException(e);
        }

        JCas targetView;
        try {
            targetView = defaultView.createView(viewName);
        } catch (CASException e) {
            throw new CollectionException(e);
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (Reader stringReader = Files.newBufferedReader(path, StandardCharsets.US_ASCII)) {
            int ch;
            while ((ch = stringReader.read()) != -1) {
                if (isValid(ch)) {
                    stringBuilder.append((char) ch);
                } else {
                    int len = stringBuilder.length();
                    LOGGER.warn("Illegal rtf character with code point: {} at {} in {}", ch, len, path.toString());
                    IllegalXmlCharacter illegalXmlCharacter = new IllegalXmlCharacter(targetView, len, len);
                    illegalXmlCharacter.setValue(ch);
                    illegalXmlCharacter.addToIndexes();
                }
            }
        }

        String documentText = stringBuilder.toString();
        targetView.setDocumentText(documentText);

        ClinicalNoteAnnotation documentAnnotation = new ClinicalNoteAnnotation(targetView, 0, documentText.length());
        String fileName = path.getFileName().toString();
        int period = fileName.lastIndexOf('.');
        if (period == -1) {
            period = fileName.length();
        }
        documentAnnotation.setDocumentId(fileName.substring(0, period));
        documentAnnotation.setAnalyzerVersion(version);
        documentAnnotation.setRetrievalTime(dateFormatter.format(new Date()));
        documentAnnotation.addToIndexes();
    }

    @Override
    public void setTargetView(String viewName) {
        this.viewName = viewName;
    }

    private static boolean isValid(int ch) {
        return (ch >= 0x20 && ch <= 0x7F) || ch == 0x09 || ch == 0x0A || ch == 0x0D;
    }
}