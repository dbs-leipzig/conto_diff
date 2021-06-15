package org.gomma.diff.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gomma.diff.model.ActionDesc;
import org.gomma.diff.model.Type;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ActionParser extends DefaultHandler {
    private		 String	changeActionDescFileLocation = null;
    public		 List<ActionDesc> changeActionDescToImport = null;
    private		 Stack<String>		  stack = null;
    private 		 ActionDesc currentChangeActionDesc = null;
    public		 List<String> primTypes = null;
    public 		 List<Type> availableTypes = null;

    public ActionParser(String descFile) {
        this.changeActionDescFileLocation = descFile;
    }



    public void startDocument() throws SAXException {
        this.stack = new Stack<String>();
    }

    public void endDocument() throws SAXException {
    }

    public void startElement( String namespaceURI,String localName,String qName,Attributes attrs ) throws SAXException {
        if (qName.equals("diffConfig")) {
        } else if (qName.equals("types")) {
            this.primTypes = new Vector<String>();
            this.availableTypes = new Vector<Type>();
        } else if (qName.equals("primType")) {
            if (attrs!=null) {
                for (int i=0;i<attrs.getLength();i++) {
                    String attName = attrs.getQName(i);
                    if (attName.equalsIgnoreCase("name")) {
                        this.primTypes.add(attrs.getValue(i).trim());
                    }
                }
            }
        } else if(qName.equals("type")) {
            Type tmpType = new Type();
            if (attrs!=null) {
                for (int i=0;i<attrs.getLength();i++) {
                    String attName = attrs.getQName(i);
                    if (attName.equalsIgnoreCase("name")) {
                        tmpType.name = attrs.getValue(i).trim();
                    } else if (attName.equalsIgnoreCase("baseType")) {
                        tmpType.baseType = attrs.getValue(i).trim();
                    }
                }
            }
            this.availableTypes.add(tmpType);
        } else if (qName.equals("contains")) {
            if (attrs!=null) {
                for (int i=0;i<attrs.getLength();i++) {
                    String attName = attrs.getQName(i);
                    if (attName.equalsIgnoreCase("name")) {
                        this.availableTypes.get(this.availableTypes.size()-1).addPrimType(attrs.getValue(i).trim());
                    }
                }
            }
        } else if (qName.equals("changeActions")) {
            this.changeActionDescToImport = new Vector<ActionDesc>();
        } else if (qName.equals("changeAction")) {
            this.currentChangeActionDesc = new ActionDesc();
            if (attrs!=null) {
                for (int i=0;i<attrs.getLength();i++) {
                    String attName = attrs.getQName(i);
                    if (attName.equalsIgnoreCase("name")) {
                        this.currentChangeActionDesc.name = attrs.getValue(i).trim();
                    } else if (attName.equalsIgnoreCase("id")) {
                        this.currentChangeActionDesc.id = Integer.parseInt(attrs.getValue(i).trim());
                    } else if (attName.equalsIgnoreCase("level")) {
                        this.currentChangeActionDesc.level = Integer.parseInt(attrs.getValue(i).trim());
                    }
                }
            }
        } else if (qName.equals("inputParameter")) {
            if (attrs!=null) {
                for (int i=0;i<attrs.getLength();i++) {
                    String attName = attrs.getQName(i);
                    if (attName.equalsIgnoreCase("type")) {
                        this.currentChangeActionDesc.paramTypes.add(attrs.getValue(i).trim());
                    } else if (attName.equalsIgnoreCase("multipleValues")) {
                        this.currentChangeActionDesc.multipleValues.add(Boolean.parseBoolean(attrs.getValue(i).trim()));
                    }
                }
            }
        }
        this.stack.push(qName);
    }

    public void endElement(String namespaceURI,String localName,String qName ) throws SAXException {
        if (qName.equals("changeAction")) {
            this.changeActionDescToImport.add(this.currentChangeActionDesc);
            this.currentChangeActionDesc = null;
        }
        this.stack.pop();
    }
}
