/******************************************************************************
 * BasicTextFileOps
 * 
 * This class provides basic services for reading and writing text files.
 * The following conventions are enforced:
 * 
 * 1. To read a text file:
 * 
 * 		myFile.openForRead();
 * 		str = myFile.readLine(); // Read one or more lines from the file.
 * 		...
 * 		myFile.close();
 * 
 * 2. To write a text file:
 * 
 * 		myFile.openForWrite();
 * 		myFile.readLine(str); // Write one or more lines to the file.
 * 		...
 * 		myFile.close();
 * 
 * If these conventions are violated this class throws exceptions and handles
 * them internally.  The intent is to not allow the caller to handle them (or
 * have to).
 *****************************************************************************/

package org.usfirst.frc4579.instrumentation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BasicTextFileOps {

	// Data common to reads and writes.
	protected File	  file;
	protected String  fileName;
	
	// Data for reads
	private boolean        isOpenForRead = false;
    private BufferedReader br;
    
    // Data for writes.
	private boolean          isOpenForWrite = false;
	private BufferedWriter   bw;
	private FileWriter       fw;
    
	/******************************************************************************
	* Constructor
	******************************************************************************/
	public BasicTextFileOps (String fileName) {
		
		this.fileName = fileName;
		
		file = new File(fileName);
	}

	/******************************************************************************
	* Internal exception definition.
	******************************************************************************/
	private class BasicTextFileOpsException extends Exception 
	{

		private String theError;

		public BasicTextFileOpsException(String error)
		{
			this.theError = error;
		}

		public String getError()
		{
			return this.theError;
		}
	}

	/******************************************************************************
	* When opening a file for reading, enforce that it is not already open, and that 
	* the file exists.
	******************************************************************************/
	private void validateOpenForRead () throws BasicTextFileOpsException {

		// Enforce that file is not already open.
		if (isOpenForRead)  throw new BasicTextFileOpsException("ERROR: " + this.fileName + " is already open for read.");

		if (isOpenForWrite) throw new BasicTextFileOpsException("ERROR: " + this.fileName + " is open for write.");

		// Enforce that the file must exist.
		if (!file.exists()) throw new BasicTextFileOpsException("ERROR: " + this.fileName + " does not exist to read.");
	}

	/******************************************************************************
	* Opens a file for reading.
	******************************************************************************/
	public void openForRead() {
		
		// Validate the request.
		try {
			validateOpenForRead();
		} catch (BasicTextFileOpsException e1) {
			System.out.println(e1.getError());
			e1.printStackTrace();
			return;
		}
		
		// Created the BufferedReader object.
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Note that the file is open for reading.
		isOpenForRead = true;
		
	}
	
	/******************************************************************************
	* When opening a file for writing make sure that the file is not already open.
	******************************************************************************/
	private void validateOpenForWrite () throws BasicTextFileOpsException {

		// Enforce that file is not already open.
		if (isOpenForWrite) throw new BasicTextFileOpsException("ERROR: " + this.fileName + " is already open for write.");

		if (isOpenForRead ) throw new BasicTextFileOpsException("ERROR: " + this.fileName + " is already open for read.");

	}
	
	/******************************************************************************
	* Opens a file for writing.
	******************************************************************************/
	public void openForWrite() {

		// Validate the request.
		try {
			validateOpenForWrite();
		} catch (BasicTextFileOpsException e1) {
			System.out.println(e1.getError());
			e1.printStackTrace();
			return;
		}
		
		// if the file already exists, delete it.
		if (file.exists()) delete();
		
		// if the file does not exist, try to create it.
		if (!file.exists()) {
			
			try {
				file.createNewFile();
			}
			catch (IOException e) {
				System.out.println("ERROR: File " + fileName + " could not be created.  Make sure that path name exists.");
				e.printStackTrace();
			}
		}

		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		isOpenForWrite = true;

	}
	
	/******************************************************************************
	* Make sure that the file is open for reading before attempting a read.
	******************************************************************************/
	private void validateReadLine () throws BasicTextFileOpsException {

		// Enforce that file is open for read.
		if (!isOpenForRead) throw new BasicTextFileOpsException("ERROR: " + this.fileName + " is not open for read.");

	}
	
	/******************************************************************************
	* Read a line from the file.  A null string is returned if at the end of file.
	******************************************************************************/
	public String readLine() {
		
		// Validate the request.
		try {
			validateReadLine();
		} catch (BasicTextFileOpsException e1) {
			System.out.println(e1.getError());
			e1.printStackTrace();
			return null;
		}
		
		String str = null;
		
		try {
			str = br.readLine();
		}
	    catch (IOException e) {
		    e.printStackTrace();
	    }
		
		return str;
	}
	
	/******************************************************************************
	* Make sure that the file is open for writing before attempting a write.
	******************************************************************************/
	private void validateWriteLine () throws BasicTextFileOpsException {

		// Enforce that file must be open for write.
		if (!isOpenForWrite) throw new BasicTextFileOpsException("ERROR: " + this.fileName + " is not open for write.");

	}
	
	/******************************************************************************
	* Writes a line of text to the file.
	******************************************************************************/
	public void writeLine(String str) {
		
		// Validate the request.
		try {
			validateWriteLine();
		} catch (BasicTextFileOpsException e1) {
			System.out.println(e1.getError());
			e1.printStackTrace();
			return;
		}
		
		try {
			bw.write(str + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/******************************************************************************
	* Make sure that the file is open before attempting to close it.
	******************************************************************************/
	private void validateClose () throws BasicTextFileOpsException {

		// Enforce that file is open.
		if (!isOpenForRead && !isOpenForWrite) throw new BasicTextFileOpsException("ERROR: " + this.fileName + " is not open.");

	}
	
	/******************************************************************************
	* Closes an open file.
	******************************************************************************/
	public void close() {

		// Validate the request.
		try {
			validateClose();
		} catch (BasicTextFileOpsException e1) {
			System.out.println(e1.getError());
			e1.printStackTrace();
			return;
		}
	
		if (isOpenForRead) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			isOpenForRead = false;
		}
		else {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			isOpenForWrite = false;
		}
	}
	
	/******************************************************************************
	* Make sure that the file is closed before deleting it.
	******************************************************************************/
	private void validateDelete () throws BasicTextFileOpsException {

		// Enforce that file is open.
		if (isOpenForRead || isOpenForWrite) 
			throw new BasicTextFileOpsException("ERROR: " + this.fileName + "  must be closed before deleting..");

	}
	
	/******************************************************************************
	* Deletes the file.
	******************************************************************************/
	public void delete() {
		
		// Validate the request.
		try {
			validateDelete();
		} catch (BasicTextFileOpsException e1) {
			System.out.println(e1.getError());
			e1.printStackTrace();
			return;
		}
	
		file.delete();
	}

}
