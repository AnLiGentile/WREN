package uk.ac.shef.dcs.oak.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.io.IOUtils;

/**
 * @author annalisa
 * this class is taken from http://keaplogik.blogspot.de/2013/02/ignore-header-and-trailer-lines-in-java.html
 */
public class FilterHeaderTrailerInputStream extends FilterInputStream {
	private Queue<Integer> readBuffer = new LinkedList<Integer>();
	int lastByte;
	boolean hasReadHeader;

	public FilterHeaderTrailerInputStream(InputStream inputStream) {
		super(inputStream);
		this.lastByte = -1;
		this.hasReadHeader = false;
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		return read(bytes, 0, bytes.length);
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws IOException {
		int bytesRead = 0;
		final byte[] readBytes = new byte[length - offset];
		while (bytesRead < length - offset) {
			final int readByte = read();
			if (readByte != -1) {
				readBytes[bytesRead++] = (byte) readByte;
			} else {
				break;
			}
		}
		if (bytesRead == 0) {
			return -1;
		}
		System.arraycopy(readBytes, 0, bytes, offset, bytesRead);
		return bytesRead;
	}

	@Override
	public int read() throws IOException {

		// The buffer has stuff in it, so serve it off.
		if (!this.readBuffer.isEmpty()) {
			return readBuffer.remove();
		}

		// A previous buffered read contained an extra byte. Place it on the
		// queue.
		if (lastByte != -1) {
			readBuffer.offer(lastByte);
		}

		// Read bytes until reaching a line feed or end of file
		int currentRead;
		do {
			currentRead = super.read();
			readBuffer.offer(currentRead);
		} while (currentRead != '\n' && currentRead != '\r'
				&& currentRead != -1);

		// The byte after a newline can either be another '\n', the first byte
		// on the next line or
		// there are no more bytes left.
		int afterLineByte = currentRead == -1 ? currentRead : super.read();

		// The byte signifies end of file. Guaranteed to have the trailer line
		// in the buffer.
		if (afterLineByte == -1) {
			readBuffer.clear(); // Clear out the buffer as to consume the line
			return -1; // return end of file indicator
		}
		// A double line break is found. Add it to the read buffer.
		// If the next bite is not available, end of file is reached.
		else if (afterLineByte == '\n') {
			readBuffer.offer(afterLineByte);
			if (super.available() == 0) {// end of file. Guaranteed to have the
											// trailer line in the buffer.
				readBuffer.clear();// Clear out the buffer as to consume the
									// line
			}
		} else {
			// Hold the last byte. Will be added to the queue on the next
			// recursive call.
			lastByte = afterLineByte;
		}

		// When header hasn't been read, take the current line and consume it.
		// Set indicator that header's been read.
		if (!hasReadHeader) {
			System.out.println("BUFFER header: "+readBuffer);
			readBuffer.clear();
			hasReadHeader = true;
		}

		return this.read();
	}

	
	// convert InputStream to String
	private static String getCleanHtmlString(InputStream is) {
 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()&!line.toUpperCase().startsWith("<!DOCTYPE"))
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
 
		return sb.toString();
 
	}
 

	private static InputStream getInputStream(String s) {
		InputStream cleanStream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		return cleanStream;

	}
	

	public static InputStream cleanHtmlInputStream (InputStream inputStream){
		String s = getCleanHtmlString(inputStream);
		InputStream is = getInputStream(s);
		return is;
		
		
	}

	public static void main (String[] args){
    	
    	
    	
		InputStream inputStreamRead = null;
		InputStream inputStream = null;

			try {
				inputStreamRead = new FileInputStream("./resources/datasets/WEIR/test/uk.eurosport.yahoo.com/aaron-hunt.html");
			    System.out.println(getCleanHtmlString(inputStreamRead));

			    
			    
//		        StringWriter writer = new StringWriter();
//		        IOUtils.copy(new FileInputStream(new File("C:/temp/test.txt")), writer, "UTF-8");
//		        String theString = writer.toString();
//		        System.out.println(theString);
		        
		        
		        
				inputStream = new FileInputStream("./resources/datasets/WEIR/test/uk.eurosport.yahoo.com/aaron-hunt.html");

			    FilterHeaderTrailerInputStream fhis = 
			            new FilterHeaderTrailerInputStream(inputStream);
			    System.out.println(getCleanHtmlString(fhis));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		

    }
}