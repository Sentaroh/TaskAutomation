package com.sentaroh.android.TaskAutomation.Common;
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

import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_NO;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
import static com.sentaroh.android.TaskAutomation.Common.CommonConstants.SERIALIZABLE_NUMBER;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ActivityExtraDataItem implements Cloneable, Externalizable{
	private static final long serialVersionUID = SERIALIZABLE_NUMBER;
	public String key_value="";
	public String data_type=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_STRING;
	public String data_value="";
	public String data_value_array=PROFILE_ACTION_TYPE_ACTIVITY_EXTRA_DATA_VALUE_ARRAY_NO;
	
	public ActivityExtraDataItem() {};
	
	@Override
	public ActivityExtraDataItem clone() {  
        ActivityExtraDataItem aedi=new ActivityExtraDataItem();
		aedi.key_value=this.key_value;
		aedi.data_type=this.data_type;
		aedi.data_value=this.data_value;
		aedi.data_value_array=this.data_value_array;
		return aedi;  
    }
	@Override
	public void readExternal(ObjectInput input) throws IOException{
		long sid=input.readLong();
		if (serialVersionUID!=sid) {
			throw new IOException("serialVersionUID was not matched by saved UID");
		}

		key_value=input.readUTF();
		data_type=input.readUTF();
		data_value=input.readUTF();
		data_value_array=input.readUTF();
	}
	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeLong(serialVersionUID);

		output.writeUTF(key_value);
		output.writeUTF(data_type);
		output.writeUTF(data_value);
		output.writeUTF(data_value_array);
	}
}