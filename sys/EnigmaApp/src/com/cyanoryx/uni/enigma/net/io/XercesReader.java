package com.cyanoryx.uni.enigma.net.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/*
 * ========================================================================= *
 *                                                                           *
 *                 The Shigeoka Software License,  Version 1.1               *
 *                   based on Apache Software License, v 1.1                 *
 * ========================================================================= *
 *                                                                           *
 * Redistribution and use in source and binary forms,  with or without modi- *
 * fication, are permitted provided that the following conditions are met:   *
 *                                                                           *
 * 1. The names  "Iain Shigeoka" and  "Manning Publicatons" must not be used *
 *    to endorse or promote  products derived                                *
 *    from this  software without  prior  written  permission.  For  written *
 *    permission, please contact <iainshigeoka@yahoo.com>.                   *
 *                                                                           *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES *
 * INCLUDING, BUT NOT LIMITED TO,  THE IMPLIED WARRANTIES OF MERCHANTABILITY *
 * AND FITNESS FOR  A PARTICULAR PURPOSE  ARE DISCLAIMED.  IN NO EVENT SHALL *
 * THE APACHE  SOFTWARE  FOUNDATION OR  ITS CONTRIBUTORS  BE LIABLE  FOR ANY *
 * DIRECT,  INDIRECT,   INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR  CONSEQUENTIAL *
 * DAMAGES (INCLUDING,  BUT NOT LIMITED TO,  PROCUREMENT OF SUBSTITUTE GOODS *
 * OR SERVICES;  LOSS OF USE,  DATA,  OR PROFITS;  OR BUSINESS INTERRUPTION) *
 * HOWEVER CAUSED AND  ON ANY  THEORY  OF  LIABILITY,  WHETHER IN  CONTRACT, *
 * STRICT LIABILITY, OR TORT  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN *
 * ANY  WAY  OUT OF  THE  USE OF  THIS  SOFTWARE,  EVEN  IF  ADVISED  OF THE *
 * POSSIBILITY OF SUCH DAMAGE.                                               *
 *                                                                           *
 * ========================================================================= *
 * 
 */

/**
 * Implementation of Apache Xerces (http://xerces.apache.org/)
 * for parsing XML (using SAX)
 * 
 * Based on Iain Shigeoka's "Instant Messaging in Java"
 * See above for the licence of use.
 * 
 * @author adammulligan
 *
 */
public class XercesReader extends FilterReader {
	int sendBlank = 0;
	
	public XercesReader(InputStream in){
	    super(new InputStreamReader(in));
    }

	private XercesReader(Reader in){
	    super(in);
	}

	public int read() throws IOException {
	    if (sendBlank > 0) {
	    	sendBlank--;
	    	return (int)' ';
	    }
	    
	    int b = in.read();
	    if (b == (int)'>') sendBlank = 2;
	    
	    return b;
	}

	public int read(char [] text, int offset, int length) throws IOException {
	    int numRead = 0;
	    for (int i = offset; i < offset + length; i++){
	    	int temp = this.read();
	      	if (temp == -1) break;
	        text[i] = (char) temp;
	        numRead++;
	    }
	    if (numRead == 0 && length != 0) numRead = -1;
	    return numRead;
	}
}