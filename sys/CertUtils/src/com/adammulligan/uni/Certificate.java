package com.adammulligan.uni;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Object representation of a certificate.
 * Roughly follows the structure of x.509 based RFC3280
 * No calculations occur here, this is merely for structure
 * 
 * Cert { Algorithm, Signature (bit string) },
 * CertDetails {
 * 		Issuer { Bit String }
 * 		Serial { Integer },
 * 		Validity { notBefore, notAfter },
 * 		UniqIdent{ Bit String },
 * 		SubjectInfo { Name, Algorithm, Public Key }
 * }
 * 
 * @author adammulligan
 *
 */
public class Certificate {
	private byte[] SignatureAlgorithm,
				   Signature,
				   Issuer,
				   Serial,
				   Validity_notBefore,
				   Validity_notAfter,
				   UniqIdent,
				   Subject_name,
				   Subject_algorithm,
				   Subject_key;
	
	public static void main(String[] args) {
		Hashtable<String, byte[]> C = new Hashtable<String, byte[]>();
		C.put("SignatureAlgorithm",new byte[]{1,2,3});
		C.put("Signature",new byte[]{4,5,6});
		C.put("Issuer",new byte[]{7,8,9});
		C.put("Serial",new byte[]{10,11,12});
		C.put("Validity_notBefore",new byte[]{13,14,15});
		C.put("Validity_notAfter",new byte[]{16,17,18});
		C.put("UniqIdent",new byte[]{19,20,21});
		C.put("Subject_name",new byte[]{22,23,24});
		C.put("Subject_algorithm",new byte[]{25,26,27});
		C.put("Subject_key",new byte[]{28,29,30});
		
		Certificate c;
		try {
			c = new Certificate(new File("cert"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes a hash table of key->values for use in Certificate construction
	 * 
	 * @param Cert
	 */
	public Certificate(Hashtable<String, byte[]> Cert) {
		this.setSignatureAlgorithm(Cert.get("SignatureAlgorithm"));
		this.setSignature(Cert.get("Signature"));
		
		this.setIssuer(Cert.get("Issuer"));
		this.setSerial(Cert.get("Serial"));
		
		this.setValidity_notAfter(Cert.get("Validity_notAfter"));
		this.setValidity_notBefore(Cert.get("Validity_notBefore"));
		
		this.setUniqIdent(Cert.get("UniqIdent"));
		
		this.setSubject_name(Cert.get("Subject_name"));
		this.setSubject_algorithm(Cert.get("Subject_algorithm"));
		this.setSubject_key(Cert.get("Subject_key"));
	}
	
	/**
	 * Takes an Enigma Certificate file and parses it to create a Certificate object
	 * 
	 * @param c
	 * @throws IOException 
	 * @throws DataFormatException 
	 */
	public Certificate(File c) throws IOException, FileNotFoundException, DataFormatException {
		StringBuffer certificate;
		
		Pattern pattern = Pattern.compile("-----BEGIN-ENIGMA-CERTIFICATE-----\n(.*?)\n-----END-ENIGMA-CERTIFICATE-----");
        Matcher matcher = pattern.matcher(Certificate.readFile(c.toString()));
        if (matcher.find()) {
        	certificate = new StringBuffer(matcher.group(1));
        	
        	String[] cert 	   = certificate.subSequence(0, certificate.indexOf("//")).toString().split(",");
        	String[] certDetails = certificate.substring(certificate.indexOf("//")+2).split("\\|");
        	
        	this.setSignatureAlgorithm(new BigInteger(cert[0]).toByteArray());
        	this.setSignature(new BigInteger(cert[1]).toByteArray());
        	
        	this.setIssuer(new BigInteger(certDetails[0]).toByteArray());
        	this.setSerial(new BigInteger(certDetails[1]).toByteArray());
        	
        	String[] validity = certDetails[2].split(",");
        	this.setValidity_notAfter(new BigInteger(validity[0]).toByteArray());
        	this.setValidity_notBefore(new BigInteger(validity[1]).toByteArray());
        	
        	this.setUniqIdent(new BigInteger(certDetails[3]).toByteArray());
        	
        	String[] subject = certDetails[4].split(",");
        	this.setSubject_name(new BigInteger(subject[0]).toByteArray());
        	this.setSubject_algorithm(new BigInteger(subject[1]).toByteArray());
        	this.setSubject_key(new BigInteger(subject[2]).toByteArray());
        	
        	return;
        }
        
        throw new DataFormatException("The certificate file could not be parsed");
	}
	
	public byte[] getSignatureAlgorithm() { return SignatureAlgorithm; }
	public void setSignatureAlgorithm(byte[] signatureAlgorithm) { SignatureAlgorithm = signatureAlgorithm; }

	public byte[] getSignature() { return Signature; }
	public void setSignature(byte[] signature) { Signature = signature; }

	public byte[] getIssuer() {return Issuer; }
	public void setIssuer(byte[] issuer) { Issuer = issuer; }

	public byte[] getSerial() { return Serial; }
	public void setSerial(byte[] serial) { Serial = serial; }

	public byte[] getValidity_notBefore() { return Validity_notBefore; }
	public void setValidity_notBefore(byte[] validity_notBefore) { Validity_notBefore = validity_notBefore; }

	public byte[] getValidity_notAfter() { return Validity_notAfter; }
	public void setValidity_notAfter(byte[] validity_notAfter) { Validity_notAfter = validity_notAfter; }

	public byte[] getUniqIdent() { return UniqIdent; }
	public void setUniqIdent(byte[] uniqIdent) { UniqIdent = uniqIdent; }

	public byte[] getSubject_name() { return Subject_name; }
	public void setSubject_name(byte[] subject_name) { Subject_name = subject_name; }

	public byte[] getSubject_algorithm() { return Subject_algorithm; }
	public void setSubject_algorithm(byte[] subject_algorithm) { Subject_algorithm = subject_algorithm; }

	public byte[] getSubject_key() { return Subject_key; }
	public void setSubject_key(byte[] subject_key) { Subject_key = subject_key; }

	@Override
	public String toString() {
		StringBuffer details = new StringBuffer();
		
		details.append((new BigInteger(this.getSignatureAlgorithm())).toString()+","+(new BigInteger(this.getSignature())).toString());
		details.append("//");
		details.append(new BigInteger(this.getIssuer()).toString()+"|");
		details.append(new BigInteger(this.getSerial()).toString()+"|");
		details.append(new BigInteger(this.getValidity_notBefore()).toString()+","+new BigInteger(this.getValidity_notAfter()).toString()+"|");
		details.append(new BigInteger(this.getUniqIdent()).toString()+"|");
		details.append(new BigInteger(this.getSubject_name()).toString()+","+new BigInteger(this.getSubject_algorithm()).toString()+","+new BigInteger(this.getSubject_key()).toString()+"\n");
		
		StringBuffer output = new StringBuffer();
		
		output.append("-----BEGIN-ENIGMA-CERTIFICATE-----\n");
		output.append(details.toString());
		output.append("-----END-ENIGMA-CERTIFICATE-----");
		
		return output.toString();
	}
	
	/**
	 * Reads a file and returns the contents, minus new lines
	 * 
	 * @param filename String path to the file to be read
	 * @return String contents of the file read
	 * @throws FileNotFoundException
	 */
	public static String readFile(String filename) throws FileNotFoundException {
	    StringBuilder text = new StringBuilder();
	    Scanner scanner = new Scanner(new FileInputStream(filename));
	    try {
	      while (scanner.hasNextLine()){
	        text.append(scanner.nextLine() + System.getProperty("line.separator"));
	      }
	    }
	    finally{
	      scanner.close();
	    }
	    
	    return text.toString();
	}
}
