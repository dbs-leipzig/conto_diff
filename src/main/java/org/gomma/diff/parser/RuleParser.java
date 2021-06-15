/*
 *
 *  * Copyright © 2014 - 2021 Leipzig University (Database Research Group)
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, version 3.
 *  *
 *  * This program is distributed in the hope that it will be useful, but
 *  * WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.gomma.diff.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gomma.diff.DiffExecutor;
import org.gomma.diff.model.InputAction;
import org.gomma.diff.model.Equation;
import org.gomma.diff.model.ResultAction;
import org.gomma.diff.model.Rule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RuleParser extends DefaultHandler {
    private		 String	ruleFileLocation = null;
    public		 List<Rule> rulesToImport = null;
    private		 Stack<String>		  stack = null;
    private 		 Rule currentRule = null;
    private		 InputAction currentInputAction = null;
    private        ResultAction currentResultAction = null;
    private	     Equation currentEquation = null;

    public RuleParser(String ruleFile) {
        this.ruleFileLocation = ruleFile;
    }

    public void importRules() {
        try {
            long start, duration;
            start = System.currentTimeMillis();
            System.out.print("Importing rules ");
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            System.out.println(this.ruleFileLocation);
            saxParser.parse( new File( this.ruleFileLocation ), this );
            duration = (System.currentTimeMillis()-start);
            System.out.println(" ...   "+rulesToImport.size()+" rules parsed ! ("+duration+" ms)");
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        } catch (IOException e) {
        }
    }

    public void startDocument() throws SAXException {
        this.stack = new Stack<String>();
    }

    public void endDocument() throws SAXException {
    }

    public void startElement( String namespaceURI,String localName,String qName,Attributes attrs ) throws SAXException {
        if (qName.equals("rules")) {
            this.rulesToImport = new Vector<Rule>();
        } else if (qName.equals("rule")) {
            this.currentRule = new Rule();
            if (attrs!=null) {
                for (int i=0;i<attrs.getLength();i++) {
                    String attName = attrs.getLocalName(i);
                    if (attName.equalsIgnoreCase("maxRound")) {
                        this.currentRule.applyUntilRound = Integer.parseInt(attrs.getValue(i).trim());
                    }
                }
            }

        } else if (qName.equals("input")) {

        } else if (qName.equals("inputAction")) {
            this.currentInputAction = new InputAction();
            if (attrs!=null) {
                for (int i=0;i<attrs.getLength();i++) {
                    String attName = attrs.getQName(i);
                    if (attName.equalsIgnoreCase("number")) {
                        this.currentInputAction.position = Integer.parseInt(attrs.getValue(i).trim());
                    } else if (attName.equalsIgnoreCase("negation")) {
                        this.currentInputAction.negation = Boolean.parseBoolean(attrs.getValue(i).trim());
                    } else if (attName.equalsIgnoreCase("name")) {
                        this.currentInputAction.actionDesc = DiffExecutor.getSingleton().getChangeActionDesc(attrs.getValue(i).trim());
                    } else if (attName.equalsIgnoreCase("checkOnly")) {
                        this.currentInputAction.checkOnly = Boolean.parseBoolean(attrs.getValue(i).trim());
                    }
                }
            }
        } else if (qName.equals("inputVariable")) {

        } else if (qName.equals("constraints")) {

        } else if (qName.equals("constraint")) {
            this.currentEquation = new Equation();
            if (attrs!=null) {
                for (int i=0;i<attrs.getLength();i++) {
                    String attName = attrs.getQName(i);
                    if (attName.equalsIgnoreCase("type")) {
                        this.currentEquation.equationType = attrs.getValue(i).trim();
                    } else if (attName.equalsIgnoreCase("leftSide")) {
                        this.currentEquation.leftSide = attrs.getValue(i).trim();
                    } else if (attName.equalsIgnoreCase("leftSideConstant")) {
                        this.currentEquation.leftSideConstant = Boolean.parseBoolean(attrs.getValue(i).trim());
                    } else if (attName.equalsIgnoreCase("rightSide")) {
                        this.currentEquation.rightSide = attrs.getValue(i).trim();
                    } else if (attName.equalsIgnoreCase("rightSideConstant")) {
                        this.currentEquation.rightSideConstant = Boolean.parseBoolean(attrs.getValue(i).trim());
                    }
                }
            }
        } else if (qName.equals("result")) {

        } else if (qName.equals("resultAction")) {
            this.currentResultAction = new ResultAction();
            if (attrs!=null) {
                if (attrs!=null) {
                    for (int i=0;i<attrs.getLength();i++) {
                        String attName = attrs.getQName(i);
                        if (attName.equalsIgnoreCase("name")) {
                            this.currentResultAction.actionDesc = DiffExecutor.getSingleton().getChangeActionDesc(attrs.getValue(i).trim());
                        }
                    }
                }
            }
        } else if (qName.equals("resultVariable")) {

        } else if (qName.equals("reduction")) {

        } else if (qName.equals("reduceAction")) {
            if (attrs!=null) {
                if (attrs!=null) {
                    for (int i=0;i<attrs.getLength();i++) {
                        String attName = attrs.getQName(i);
                        if (attName.equalsIgnoreCase("number")) {
                            this.currentRule.addReduceInputAction(Integer.parseInt(attrs.getValue(i).trim()));
                        }
                    }
                }
            }
        }
        this.stack.push(qName);
    }

    public void endElement(String namespaceURI,String localName,String qName ) throws SAXException {
        if (qName.equals("rule")) {
            this.rulesToImport.add(this.currentRule);
            this.currentRule = null;
        } else if (qName.equals("inputAction")) {
            this.currentRule.addInputAction(this.currentInputAction);
            this.currentInputAction = null;
        } else if (qName.equals("constraint")) {
            this.currentRule.addConstraint(this.currentEquation);
            this.currentEquation = null;
        } else if (qName.equals("resultAction")) {
            this.currentRule.resultAction = this.currentResultAction;
            this.currentResultAction = null;
        }
        this.stack.pop();
    }

    public void characters( char[] buf, int offset, int len ) throws SAXException {
        String s = new String( buf, offset, len ).trim();
        if (stack.peek().equals("inputVariable")) {
            this.currentInputAction.addParamVariable(s);
        } else if (stack.peek().equals("resultVariable")) {
            this.currentResultAction.addParamVariable(s);
        }
    }
}
