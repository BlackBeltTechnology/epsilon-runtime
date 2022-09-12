package hu.blackbelt.epsilon.runtime.execution.impl;

/*-
 * #%L
 * epsilon-runtime-execution
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import hu.blackbelt.epsilon.runtime.execution.UriResolver;
import org.eclipse.epsilon.egl.merge.partition.CommentBlockPartitioner;
import org.eclipse.epsilon.egl.merge.partition.CompositePartitioner;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DefaultXMLConfigReader extends DefaultHandler {

    private final String DTD_PATH      = "org/eclipse/epsilon/egl/config/";
    private final String DTD_FILE_NAME = "ContentTypes.dtd";
    private final String DTD_URL       = "http://www.epsilon.org/egl/" + DTD_FILE_NAME;
    private final String ROOT_ELEMENT  = "ContentTypes";

    private final Map<String, CompositePartitioner> contentTypes = new HashMap<>();

    private boolean correctRootFound = false;
    private String currentContentType;

    public Map<String, CompositePartitioner> read(InputStream stream) throws IOException {
        try {
            correctRootFound = false;

            // Instantiate a parser factory to produce validating parsers
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);

            // Create a new parser for the specified file
            final SAXParser parser = factory.newSAXParser();

            // Utilise an entity resolver that always uses the local DTD
            parser.getXMLReader().setEntityResolver((publicId, systemId) -> {
                // Intercept the reference to the fictional URL and
                // load the DTD from the file system
                if (systemId.equals(DTD_URL)) {
                    return new InputSource(UriResolver.calculateURI(this.getClass(), DTD_PATH + DTD_FILE_NAME).toURL().openStream());
                }
                return null;
            });

            // Use this object for handling any errors
            parser.getXMLReader().setErrorHandler(this);

            // Parse the stream, using this class to handle the events
            parser.getXMLReader().setContentHandler(this);
            parser.getXMLReader().parse(new InputSource(stream));

            if (!correctRootFound)
                throw new IOException("Did not find the expected root element: " + ROOT_ELEMENT);

            return contentTypes;

        } catch (SAXParseException e) {
            // Create a meaningful message for ill-formed or invalid XML files
            final String message = "Parse error at line "+e.getLineNumber()+", "+
                    "column "+e.getColumnNumber()+":\n\n"+e.getMessage();

            throw new IOException(message, e);

        } catch (SAXException e) {
            throw new IOException(e);

        } catch (IOException e) {
            throw e;

        } catch (ParserConfigurationException e) {
            throw new IOException(e);

        } finally {
            try {
                if (stream != null) stream.close();
            } catch (IOException e) {}
        }
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        // If this is the correct root element
        if (qName.equals(ROOT_ELEMENT)) {
            correctRootFound = true;

            // If this element is a contentType
        } else if (correctRootFound && qName.equals("ContentType")) {

            // Determine the name of this content type
            currentContentType = attributes.getValue("name");

            contentTypes.put(currentContentType, new CompositePartitioner());

            // If this element is a commentStyle
        } else if (correctRootFound && qName.equals("CommentStyle")) {
            // Retrieve the startsWith and endsWith attributes for this commentStyle
            final String startsWith = attributes.getValue("startsWith");
            final String endsWith   = attributes.getValue("endsWith");

            final CommentBlockPartitioner partitioner = new CommentBlockPartitioner(startsWith, endsWith);


            if (currentContentType !=null)
                contentTypes.get(currentContentType).addPartitioner(partitioner);

        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (correctRootFound && qName.equals("ContentType")) {
            currentContentType = null;
        }
    }


    @Override
    public void error(SAXParseException e) throws SAXParseException {
        // Propogate all parse errors up the stack
        throw e;
    }
}
