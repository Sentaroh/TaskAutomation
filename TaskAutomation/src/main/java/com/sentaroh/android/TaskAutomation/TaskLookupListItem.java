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

import static com.sentaroh.android.TaskAutomation.CommonConstants.SERIALIZABLE_NUMBER;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TaskLookupListItem implements Externalizable{
	private static final long serialVersionUID = SERIALIZABLE_NUMBER;
	public String event_name=null;
	public int start_pos=0;
	public int end_pos=0;
	
	public TaskLookupListItem() {};
	
	@Override
	public void readExternal(ObjectInput input) throws IOException {
		long sid=input.readLong();
		if (serialVersionUID!=sid) {
			throw new IOException("serialVersionUID was not matched by saved UID");
		}

		event_name=input.readUTF();
		start_pos=input.readInt();
		end_pos=input.readInt();
	}
	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeLong(serialVersionUID);
		
		output.writeUTF(event_name);
		output.writeInt(start_pos);
		output.writeInt(end_pos);
	}
}