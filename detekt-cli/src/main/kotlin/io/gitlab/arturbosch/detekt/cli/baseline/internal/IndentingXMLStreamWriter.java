/*
 * Copyright (c) 2005, 2012, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package io.gitlab.arturbosch.detekt.cli.baseline.internal;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Stack;

/**
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings("all")
public class IndentingXMLStreamWriter extends DelegatingXMLStreamWriter {
	private final static Object SEEN_NOTHING = new Object();
	private final static Object SEEN_ELEMENT = new Object();
	private final static Object SEEN_DATA = new Object();

	private Object state = SEEN_NOTHING;
	private Stack<Object> stateStack = new Stack<Object>();

	private String indentStep = "  ";
	private int depth = 0;

	public IndentingXMLStreamWriter(XMLStreamWriter writer) {
		super(writer);
	}

	/**
	 * Return the current indent step.
	 * <p>
	 * <p>Return the current indent step: each start tag will be
	 * indented by this number of spaces times the number of
	 * ancestors that the element has.</p>
	 *
	 * @return The number of spaces in each indentation step,
	 * or 0 or less for no indentation.
	 * @see #setIndentStep(int)
	 * @deprecated Only return the length of the indent string.
	 */
	public int getIndentStep() {
		return indentStep.length();
	}


	/**
	 * Set the current indent step.
	 *
	 * @param indentStep The new indent step (0 or less for no
	 *                   indentation).
	 * @see #getIndentStep()
	 * @deprecated Should use the version that takes string.
	 */
	public void setIndentStep(int indentStep) {
		StringBuilder s = new StringBuilder();
		for (; indentStep > 0; indentStep--) s.append(' ');
		setIndentStep(s.toString());
	}

	public void setIndentStep(String s) {
		this.indentStep = s;
	}

	private void onStartElement() throws XMLStreamException {
		stateStack.push(SEEN_ELEMENT);
		state = SEEN_NOTHING;
		if (depth > 0) {
			super.writeCharacters("\n");
		}
		doIndent();
		depth++;
	}

	private void onEndElement() throws XMLStreamException {
		depth--;
		if (state == SEEN_ELEMENT) {
			super.writeCharacters("\n");
			doIndent();
		}
		state = stateStack.pop();
	}

	private void onEmptyElement() throws XMLStreamException {
		state = SEEN_ELEMENT;
		if (depth > 0) {
			super.writeCharacters("\n");
		}
		doIndent();
	}

	/**
	 * Print indentation for the current level.
	 *
	 * @throws org.xml.sax.SAXException If there is an error
	 *                                  writing the indentation characters, or if a filter
	 *                                  further down the chain raises an exception.
	 */
	private void doIndent() throws XMLStreamException {
		if (depth > 0) {
			for (int i = 0; i < depth; i++)
				super.writeCharacters(indentStep);
		}
	}


	public void writeStartDocument() throws XMLStreamException {
		super.writeStartDocument();
		super.writeCharacters("\n");
	}

	public void writeStartDocument(String version) throws XMLStreamException {
		super.writeStartDocument(version);
		super.writeCharacters("\n");
	}

	public void writeStartDocument(String encoding, String version) throws XMLStreamException {
		super.writeStartDocument(encoding, version);
		super.writeCharacters("\n");
	}

	public void writeStartElement(String localName) throws XMLStreamException {
		onStartElement();
		super.writeStartElement(localName);
	}

	public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
		onStartElement();
		super.writeStartElement(namespaceURI, localName);
	}

	public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		onStartElement();
		super.writeStartElement(prefix, localName, namespaceURI);
	}

	public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
		onEmptyElement();
		super.writeEmptyElement(namespaceURI, localName);
	}

	public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		onEmptyElement();
		super.writeEmptyElement(prefix, localName, namespaceURI);
	}

	public void writeEmptyElement(String localName) throws XMLStreamException {
		onEmptyElement();
		super.writeEmptyElement(localName);
	}

	public void writeEndElement() throws XMLStreamException {
		onEndElement();
		super.writeEndElement();
	}

	public void writeCharacters(String text) throws XMLStreamException {
		state = SEEN_DATA;
		super.writeCharacters(text);
	}

	public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
		state = SEEN_DATA;
		super.writeCharacters(text, start, len);
	}

	public void writeCData(String data) throws XMLStreamException {
		state = SEEN_DATA;
		super.writeCData(data);
	}
}
