package com.sentaroh.android.TaskAutomation;
/*
The MIT License (MIT)
Copyright (c) 2011-2013 Sentaroh

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal 
in the Software without restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to 
the following conditions:

The above copyright notice and this permission notice shall be included in all copies or 
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

*/

import static com.sentaroh.android.TaskAutomation.CommonConstants.APPLICATION_TAG;
import static com.sentaroh.android.TaskAutomation.CommonConstants.SERIALIZABLE_NUMBER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import android.util.Log;

public class TaskHistoryItem implements Serializable, Cloneable {
	private static final long serialVersionUID = SERIALIZABLE_NUMBER;
	public String task_status=null;
	public final static String TASK_HISTORY_TASK_STATUS_STARTED="A";
	public final static String TASK_HISTORY_TASK_STATUS_ENDED="E";
	public final static String TASK_HISTORY_TASK_STATUS_QUEUED="Q";
	public String group_name=null;
	public String event_name=null, task_name=null, msg_text=null;
	public String result=null;
	public String start_time=null, end_time=null, queued_time=null;
	
	public TaskHistoryItem() {};
	
	@Override
	final public TaskHistoryItem clone() throws CloneNotSupportedException{  
           return (TaskHistoryItem)super.clone();  
    };
    
	final static public TaskHistoryItem deSerialize(byte[] buf) {
		TaskHistoryItem o=null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(buf); 
			ObjectInput in = new ObjectInputStream(bis); 
		    o=(TaskHistoryItem) in.readObject(); 
		    in.close(); 
		} catch (StreamCorruptedException e) {
			Log.v(APPLICATION_TAG, "TaskHistoryItem deSerialize error", e);
		} catch (IOException e) {
			Log.v(APPLICATION_TAG, "TaskHistoryItem deSerialize error", e);
		} catch (ClassNotFoundException e) {
			Log.v(APPLICATION_TAG, "TaskHistoryItem deSerialize error", e);
		}
		return o;
	};
	final public byte[] serialize() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(100000); 
		byte[] buf=null; 
	    try { 
	    	ObjectOutput out = new ObjectOutputStream(bos); 
		    out.writeObject(this);
		    out.flush(); 
		    buf= bos.toByteArray(); 
	    } catch(IOException e) { 
	    	Log.v(APPLICATION_TAG, "TaskHistoryItem serialize error", e); 
		}
		return buf;
	};
};
