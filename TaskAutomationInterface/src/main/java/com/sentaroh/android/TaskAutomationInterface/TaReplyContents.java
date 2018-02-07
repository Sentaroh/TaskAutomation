package com.sentaroh.android.TaskAutomationInterface;

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

final public class TaReplyContents {
	private TaSysInfo mSysInfo=null;
	private String[][] mReplyTaskList=null;
	private String[][] mReplyGroupList=null;
	private String mStatusDesc=null;

	public TaReplyContents(TaInterfaceParms taip) {
		if (taip.availavility_sys_info) mSysInfo=new TaSysInfo(taip);;
		mReplyTaskList=taip.reply_task_list;
		mReplyGroupList=taip.reply_group_list;
		mStatusDesc=TaApplicationInterface.REQUEST_RESULT_REASON_DESC[taip.reply_result_status_code];
	}

	final public String getTaStatusDesc() {
		return mStatusDesc;
	}
	
	final public TaSysInfo getTaSysInfo() {return mSysInfo;};
	
	final public String[][] getTaTaskList() {return mReplyTaskList;};
	
	final public String[][] getTaGroupList() {return mReplyGroupList;};
}
