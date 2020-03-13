package ro.sdi.lab24.model.serialization.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ro.sdi.lab24.model.Client;

public class ClientXMLSerializer implements XMLSerializer<Client> {
    private static String getTextFromTagName(Element parentElement, String tagName) {
        Node node = parentElement.getElementsByTagName(tagName).item(0);
        return node.getTextContent();
    }

    private void appendChildWithTextNode(Document document, Node parent, String tagName, String content) {
        Element element = document.createElement(tagName);
        element.setTextContent(content);
        parent.appendChild(element);
    }

    @Override
    public Element serialize(Document document, Client entity) {
        Element element = document.createElement("client");
        appendChildWithTextNode(document, element, "id", String.valueOf(entity.getId()));
        appendChildWithTextNode(document, element, "name", entity.getName());
        return element;
    }

    @Override
    public Client deserialize(Element element) {
        int id = Integer.parseInt(getTextFromTagName(element, "id"));
        String name = getTextFromTagName(element, "name");
        return new Client(id, name);
    }
}
