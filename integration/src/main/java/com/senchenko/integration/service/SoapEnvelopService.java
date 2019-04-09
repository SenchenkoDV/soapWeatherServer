package com.senchenko.integration.service;

import com.weather.senchenko.GetCityRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class SoapEnvelopService<T> {
    private static final Logger LOGGER = LogManager.getLogger();

    public String createEnvelop(String name, Class<T> clazz) {
        String envelop = null;
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Marshaller marshaller = JAXBContext.newInstance(clazz).createMarshaller();
            GetCityRequest getCityRequest = new GetCityRequest();
            getCityRequest.setName(name);
            marshaller.marshal(getCityRequest, document);
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            soapMessage.getSOAPBody().addDocument(document);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);
            envelop = new String(outputStream.toByteArray());
        } catch (JAXBException | SOAPException | IOException | ParserConfigurationException e) {
            LOGGER.log(Level.ERROR, "Error creating SOAP envelop");
        }
        return envelop;
    }
}
