package com.cyanoryx.uni.enigma.net.protocol.xml;

import org.apache.xerces.framework.XMLErrorReporter;
import org.apache.xerces.readers.DefaultReaderFactory;
import org.apache.xerces.readers.StreamingCharReader;
import org.apache.xerces.readers.XMLEntityHandler;
import org.apache.xerces.utils.StringPool;
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
 * SAX parsers are generally not designed to handle streaming input,
 * and thus we must override Xerces so it uses a stream rather than a
 * buffer.
 * 
 * Solution from "Instant Messaging in Java", Manning
 * See above for usage licence.
 * 
 * @author adammulligan
 *
 */
public class StreamingCharFactory extends DefaultReaderFactory {
  public XMLEntityHandler.EntityReader createCharReader(XMLEntityHandler  entityHandler,
                                                        XMLErrorReporter errorReporter,
                                                        boolean sendCharDataAsCharArray,
                                                        Reader reader,
                                                        StringPool stringPool)
  throws Exception {
      return new StreamingCharReader(entityHandler,
                                     errorReporter,
                                     sendCharDataAsCharArray,
                                     reader,
                                     stringPool);
  }

  public XMLEntityHandler.EntityReader createUTF8Reader(XMLEntityHandler entityHandler, 
                                                        XMLErrorReporter errorReporter,
                                                        boolean sendCharDataAsCharArray,
                                                        InputStream data,
                                                        StringPool stringPool)
  throws Exception {
      XMLEntityHandler.EntityReader reader;
      reader = new StreamingCharReader(entityHandler,
                                       errorReporter,
                                       sendCharDataAsCharArray,
                                       new InputStreamReader(data, "UTF8"),
                                       stringPool);
      return reader;
  }
}

