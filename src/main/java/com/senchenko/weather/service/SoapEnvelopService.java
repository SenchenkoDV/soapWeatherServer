package com.senchenko.weather.service;

import com.weather.senchenko.GetCityRequest;
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

    public String createEnvelop(String name, Class<T> clazz) {
        Document document;
        Marshaller marshaller;
        String output = null;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            marshaller = JAXBContext.newInstance(clazz).createMarshaller();
            GetCityRequest getCityRequest = new GetCityRequest();
            getCityRequest.setName(name);
            marshaller.marshal(getCityRequest, document);
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            soapMessage.getSOAPBody().addDocument(document);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);
            output = new String(outputStream.toByteArray());
        } catch (JAXBException | SOAPException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return output;
    }
}
